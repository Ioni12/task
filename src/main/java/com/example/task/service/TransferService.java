package com.example.task.service;

import com.example.task.Errors;
import com.example.task.client.CurrencyApiClient;
import com.example.task.entity.*;
import com.example.task.exception.ResourceNotFoundException;
import com.example.task.exception.TransactionException;
import com.example.task.repository.AccountRepository;
import com.example.task.repository.BalanceHistoryRepository;
import com.example.task.repository.TransactionRepository;
import com.example.task.repository.TransferRepository;
import com.example.task.request.TransferRequest;
import com.example.task.response.CurrencyResponse;
import com.example.task.utils.CurrencyConverter;
import com.example.task.utils.TransformerUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final BalanceHistoryRepository balanceHistoryRepository;
    private final CurrencyApiClient currencyApiClient;
    private final TransferRepository transferRepository;

    public TransferService(
            AccountRepository accountRepository,
            TransactionRepository transactionRepository,
            BalanceHistoryRepository balanceHistoryRepository,
            CurrencyApiClient currencyApiClient,
            TransferRepository transferRepository
    ) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.balanceHistoryRepository = balanceHistoryRepository;
        this.currencyApiClient = currencyApiClient;
        this.transferRepository = transferRepository;
    }

    @Caching(evict = {
            @CacheEvict(value = "transactions", allEntries = true),
            @CacheEvict(value = "account", allEntries = true)
    })
    public List<Transaction> transfer(TransferRequest request) {
        String senderUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Transfer request - sender: {}, fromAccount: {}, toAccountId: {}, amount: {}",
                senderUsername, request.getFromAccountName(), request.getAccountId(), request.getAmount());

        Account senderAccount = accountRepository.findAccountByNameAndUsername(request.getFromAccountName(), senderUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found", request.getFromAccountName()));

        Account recipientAccount = accountRepository.findAccountByIdAndUsername(request.getAccountId(), request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Recipient account not found", request.getAccountId()));

        if (senderAccount.getId().equals(recipientAccount.getId())) {
            throw new TransactionException(Errors.SAME_ACCOUNT_TRANSFER);
        }

        if (senderAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new TransactionException(Errors.ACCOUNT_FROZEN);
        }

        if (recipientAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new TransactionException(Errors.ACCOUNT_FROZEN);
        }

        if (senderAccount.getAmount().compareTo(request.getAmount()) < 0) {
            throw new TransactionException(Errors.INSUFFICIENT_FUNDS);
        }

        BigDecimal convertedTransferAmount;
        BigDecimal exchangeRate = null;

        if (senderAccount.getCurrency().equalsIgnoreCase(recipientAccount.getCurrency())) {
            convertedTransferAmount = request.getAmount();
        } else {
            try {
                CurrencyResponse currencyRates = currencyApiClient.getCurrency(senderAccount.getCurrency());
                convertedTransferAmount = CurrencyConverter.convertDeposit(currencyRates, senderAccount.getCurrency(), recipientAccount.getCurrency(), request.getAmount());
                exchangeRate = convertedTransferAmount.divide(request.getAmount(), 10, RoundingMode.HALF_UP);
            } catch (Exception e) {
                log.error("Currency conversion failed: {}", e.getMessage());
                throw new TransactionException(Errors.CURRENCY_FETCH_FAILED);
            }
        }

        senderAccount.setAmount(senderAccount.getAmount().subtract(request.getAmount()));
        recipientAccount.setAmount(recipientAccount.getAmount().add(convertedTransferAmount));

        accountRepository.save(senderAccount);
        accountRepository.save(recipientAccount);

        Transfer transfer = Transfer.builder()
                .amount(request.getAmount())
                .fromCurrency(senderAccount.getCurrency())
                .toCurrency(recipientAccount.getCurrency())
                .exchangeRate(exchangeRate)
                .createdAt(LocalDateTime.now())
                .status(TransferStatus.COMPLETED)
                .build();
        transfer = transferRepository.save(transfer);

        Transaction withdrawalTransaction = transactionRepository.save(
                TransformerUtility.createWithdrawTransaction(senderAccount, request.getAmount(), recipientAccount, transfer));

        Transaction depositTransaction = transactionRepository.save(
                TransformerUtility.createDepositTransaction(recipientAccount, convertedTransferAmount, senderAccount, transfer));

        balanceHistoryRepository.save(
                TransformerUtility.createWithdrawBalanceHistory(senderAccount, withdrawalTransaction, request.getAmount()));

        balanceHistoryRepository.save(
                TransformerUtility.createDepositBalanceHistory(recipientAccount, depositTransaction, convertedTransferAmount));

        log.info("Transfer completed - sender: {}, recipient: {}, amount: {}, converted: {}, exchangeRate: {}",
                senderAccount.getIban(), recipientAccount.getIban(), request.getAmount(), convertedTransferAmount, exchangeRate);

        return List.of(withdrawalTransaction, depositTransaction);
    }
}