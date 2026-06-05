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
    private final UserRepository userRepository;

    public TransferService(
            AccountRepository accountRepository,
            TransactionRepository transactionRepository,
            BalanceHistoryRepository balanceHistoryRepository,
            CurrencyApiClient currencyApiClient,
            UserRepository userRepository
    ) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.balanceHistoryRepository = balanceHistoryRepository;
        this.currencyApiClient = currencyApiClient;
        this.userRepository = userRepository;
    }

    @Caching(evict= {
            @CacheEvict(value = "transactions", allEntries = true),
            @CacheEvict(value = "account", allEntries = true)
    })
    public List<Transaction> transfer(@RequestBody TransferRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Account from = accountRepository.findAccountByName(request.getFromAccountName())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found", request.getFromAccountName()));

        if(!from.getUser().getUsername().equals(username)) {
            throw new BadRequestException("Forbidden: you dont own this account");
        }

        User toUser = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Recipient not found", request.getUsername()));


        Account to = accountRepository.findAccountById(request.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("account not found", request.getAccountId()));

        if(!to.getUser().getId().equals(toUser.getId())) {
            throw new ResourceNotFoundException("account not found for user ", to.getUser().getId());
        }

        if(from.getAmount().compareTo(request.getAmount()) < 0) {
            throw new TransactionException(Errors.INSUFFICIENT_FUNDS);
        }

        BigDecimal convertedAmount;
        if (from.getCurrency().equalsIgnoreCase(to.getCurrency())) {
            convertedAmount = request.getAmount();
        } else {
            try {
                CurrencyResponse rates = currencyApiClient.getCurrency(from.getCurrency());
                double converted = CurrencyConverter.convertDeposit(rates, from.getCurrency(), to.getCurrency(), request.getAmount().doubleValue());
                convertedAmount = BigDecimal.valueOf(converted);
            } catch (Exception e) {
                throw new TransactionException(Errors.CURRENCY_FETCH_FAILED);
            }
        }

        from.setAmount(from.getAmount().subtract(request.getAmount()));
        to.setAmount(to.getAmount().add(convertedAmount));

        accountRepository.save(from);
        accountRepository.save(to);

        Transaction fromTx = transactionRepository.save(Transaction.builder()
                .account(from)
                .amount(request.getAmount().negate())
                .currency(from.getCurrency())
                .transactionDate(LocalDateTime.now())
                .type(TransactionType.WITHDRAW)
                .transactionDetails("Transfer to account #" + to.getId())
                .build());

        Transaction toTx = transactionRepository.save(Transaction.builder()
                .account(to)
                .amount(convertedAmount)
                .currency(to.getCurrency())
                .transactionDate(LocalDateTime.now())
                .type(TransactionType.DEPOSIT)
                .transactionDetails("Transfer from account #" + from.getId())
                .build());

        balanceHistoryRepository.save(BalanceHistory.builder()
                .account(from)
                .transaction(fromTx)
                .balanceBefore(from.getAmount().add(request.getAmount())) // before subtract
                .balanceAfter(from.getAmount())
                .changeAmount(request.getAmount().negate())
                .transactionType(TransactionType.WITHDRAW)
                .timestamp(LocalDateTime.now())
                .build());

        balanceHistoryRepository.save(BalanceHistory.builder()
                .account(to)
                .transaction(toTx)
                .balanceBefore(to.getAmount().subtract(convertedAmount)) // before add
                .balanceAfter(to.getAmount())
                .changeAmount(convertedAmount)
                .transactionType(TransactionType.DEPOSIT)
                .timestamp(LocalDateTime.now())
                .build());

        return List.of(fromTx, toTx);
    }

}
