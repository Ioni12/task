package com.example.task.service;

import com.example.task.Errors;
import com.example.task.client.CurrencyApiClient;
import com.example.task.entity.*;
import com.example.task.exception.BadRequestException;
import com.example.task.exception.ResourceNotFoundException;
import com.example.task.exception.TransactionException;
import com.example.task.repository.AccountRepository;
import com.example.task.repository.BalanceHistoryRepository;
import com.example.task.repository.TransactionRepository;
import com.example.task.repository.UserRepository;
import com.example.task.request.TransferRequest;
import com.example.task.response.CurrencyResponse;
import com.example.task.utils.CurrencyConverter;
import com.example.task.utils.TransformerUtility;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final BalanceHistoryRepository balanceHistoryRepository;
    private final CurrencyApiClient currencyApiClient;

    public TransferService(
            AccountRepository accountRepository,
            TransactionRepository transactionRepository,
            BalanceHistoryRepository balanceHistoryRepository,
            CurrencyApiClient currencyApiClient
    ) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.balanceHistoryRepository = balanceHistoryRepository;
        this.currencyApiClient = currencyApiClient;
    }

    @Caching(evict= {
            @CacheEvict(value = "transactions", allEntries = true),
            @CacheEvict(value = "account", allEntries = true)
    })
    public List<Transaction> transfer(@RequestBody TransferRequest request) {

        Account senderAccount = accountRepository.findAccountByName(request.getFromAccountName())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found", request.getFromAccountName()));

        Account recipientAccount = accountRepository.findAccountByIdAndUsername(request.getAccountId(), request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("account not found", request.getAccountId()));

        if(senderAccount.getAmount().compareTo(request.getAmount()) < 0) {
            throw new TransactionException(Errors.INSUFFICIENT_FUNDS);
        }

        BigDecimal convertedTransferAmount;
        if (senderAccount.getCurrency().equalsIgnoreCase(recipientAccount.getCurrency())) {
            convertedTransferAmount = request.getAmount();
        } else {
            try {
                CurrencyResponse currencyRates = currencyApiClient.getCurrency(senderAccount.getCurrency());
                convertedTransferAmount = CurrencyConverter.convertDeposit(currencyRates, senderAccount.getCurrency(), recipientAccount.getCurrency(), request.getAmount());
            } catch (Exception e) {
                throw new TransactionException(Errors.CURRENCY_FETCH_FAILED);
            }
        }

        senderAccount.setAmount(senderAccount.getAmount().subtract(request.getAmount()));
        recipientAccount.setAmount(recipientAccount.getAmount().add(convertedTransferAmount));

        accountRepository.save(senderAccount);
        accountRepository.save(recipientAccount);

        Transaction withdrawalTransaction = transactionRepository.save(TransformerUtility.createWithdrawTransaction(senderAccount,request.getAmount(), recipientAccount));

        Transaction depositTransaction = transactionRepository.save(TransformerUtility.createDepositTransaction(recipientAccount, convertedTransferAmount, senderAccount));

        balanceHistoryRepository.save(TransformerUtility.createWithdrawBalanceHistory(senderAccount, withdrawalTransaction, request.getAmount()));

        balanceHistoryRepository.save(TransformerUtility.createDepositBalanceHistory(recipientAccount, depositTransaction, convertedTransferAmount));

        return List.of(withdrawalTransaction, depositTransaction);
    }

}
