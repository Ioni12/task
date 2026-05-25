package com.example.task.service;

import com.example.task.client.CurrencyApiClient;
import com.example.task.entity.*;
import com.example.task.repository.AccountRepository;
import com.example.task.repository.BalanceHistoryRepository;
import com.example.task.repository.TransactionRepository;
import com.example.task.repository.UserRepository;
import com.example.task.request.TransactionRequest;
import com.example.task.response.CurrencyResponse;
import com.example.task.utils.CurrencyConverter;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final CurrencyApiClient currencyApiClient;
    private final BalanceHistoryRepository balanceHistoryRepository;

    public TransactionService(
            TransactionRepository transactionRepository,
            AccountRepository accountRepository,
            UserRepository userRepository,
            BalanceHistoryRepository balanceHistoryRepository,
            CurrencyApiClient currencyApiClient) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.balanceHistoryRepository = balanceHistoryRepository;
        this.currencyApiClient = currencyApiClient;
    }

    @Cacheable(value = "transactions", key = "#username")
    public List<Transaction> getTransactionsByName(String username) {
        log.debug("Fetching transactions for user: {}", username);
        return transactionRepository.findByUserNameTransactions(username);
    }


    @Caching(evict={
            @CacheEvict(value = "transactions", allEntries = true),
            @CacheEvict(value = "account", allEntries = true)
    })
    public Transaction createTransaction(TransactionRequest request) {
        log.info("Creating transaction - user: {}, accountId: {}, type: {}, amount: {}, currency: {}",
                request.getUsername(), request.getAccountId(), request.getType(),
                request.getAmount(), request.getCurrency());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + request.getUsername()));
        log.debug("User found - id: {}, username: {}", user.getId(), user.getUsername());

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new EntityNotFoundException("Account not found: " + request.getAccountId()));
        log.debug("Account found - id: {}, currency: {}, currentBalance: {}, ownerId: {}",
                account.getId(), account.getCurrency(), account.getAmount(), account.getUser().getId());

        if (!account.getUser().getId().equals(user.getId())) {
            log.warn("Ownership mismatch - accountOwnerId: {}, requestUserId: {}", account.getUser().getId(), user.getId());
            throw new IllegalArgumentException("Account does not belong to this user");
        }

        String requestCurrency = request.getCurrency().toLowerCase();
        String accountCurrency = account.getCurrency().toLowerCase();
        log.debug("Currency info - requestCurrency: {}, accountCurrency: {}", requestCurrency, accountCurrency);

        BigDecimal balanceBefore = account.getAmount();
        double convertedAmount;

        if (request.getType() == TransactionType.DEPOSIT) {
            log.debug("Processing DEPOSIT");


            if (requestCurrency.equals(accountCurrency)) {
                convertedAmount = request.getAmount().doubleValue();
                log.debug("Same currency deposit, no conversion needed - amount: {}", convertedAmount);
            } else {
                log.debug("Currency conversion required for DEPOSIT: {} -> {}", requestCurrency, accountCurrency);
                CurrencyResponse response = currencyApiClient.getCurrency(requestCurrency);
                log.debug("Currency API response received: {}", response);
                convertedAmount = CurrencyConverter.convertDeposit(response, requestCurrency, accountCurrency, request.getAmount().doubleValue());
                log.debug("Converted deposit amount: {} {} -> {} {}", request.getAmount(), requestCurrency, convertedAmount, accountCurrency);
            }

            account.setAmount(account.getAmount().add(BigDecimal.valueOf(convertedAmount)));
            log.info("DEPOSIT applied - accountId: {}, balanceBefore: {}, convertedAmount: {}, balanceAfter: {}",
                    account.getId(), balanceBefore, convertedAmount, account.getAmount());

        } else if (request.getType() == TransactionType.WITHDRAW) {
            log.debug("Processing WITHDRAW");

            if (requestCurrency.equals(accountCurrency)) {
                convertedAmount = request.getAmount().doubleValue();
                log.debug("Same currency withdrawal, no conversion needed - amount: {}", convertedAmount);
            } else {
                log.debug("Currency conversion required for WITHDRAW: {} -> {}", accountCurrency, requestCurrency);
                CurrencyResponse response = currencyApiClient.getCurrency(accountCurrency);
                log.debug("Currency API response received: {}", response);
                convertedAmount = CurrencyConverter.convertWithdraw(response, accountCurrency, requestCurrency, request.getAmount().doubleValue());
                log.debug("Converted withdrawal amount: {} {} -> {} {}", request.getAmount(), requestCurrency, convertedAmount, accountCurrency);
            }

            if (account.getAmount().compareTo(BigDecimal.valueOf(convertedAmount)) < 0) {
                log.warn("Insufficient funds - accountId: {}, balance: {}, requestedAmount: {}, currency: {}",
                        account.getId(), account.getAmount(), request.getAmount(), requestCurrency);
                throw new IllegalArgumentException("Insufficient funds");
            }

            account.setAmount(account.getAmount().subtract(BigDecimal.valueOf(convertedAmount)));
            log.info("WITHDRAW applied - accountId: {}, balanceBefore: {}, convertedAmount: {}, balanceAfter: {}",
                    account.getId(), balanceBefore, convertedAmount, account.getAmount());

        } else {
            log.error("Unsupported transaction type: {}", request.getType());
            throw new IllegalArgumentException("Unsupported transaction type: " + request.getType());
        }

        accountRepository.save(account);
        log.debug("Account balance persisted - accountId: {}, newBalance: {}", account.getId(), account.getAmount());

        BalanceHistory balanceHistory = BalanceHistory.builder()
                .account(account)
                .balanceBefore(balanceBefore)
                .balanceAfter(account.getAmount())
                .changeAmount(BigDecimal.valueOf(convertedAmount))
                .transactionType(request.getType())
                .timestamp(LocalDateTime.now())
                .build();

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(request.getAmount());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setTransactionDetails(request.getTransactionDetails());
        transaction.setCurrency(request.getCurrency());
        transaction.setType(request.getType());

        Transaction saved = transactionRepository.save(transaction);
        log.info("Transaction created successfully - transactionId: {}, accountId: {}, type: {}, amount: {}, currency: {}",
                saved.getId(), account.getId(), saved.getType(), saved.getAmount(), saved.getCurrency());

        balanceHistory.setTransaction(saved);
        balanceHistoryRepository.save(balanceHistory);
        log.info("balance history saved successfuly");
        return saved;
    }
}