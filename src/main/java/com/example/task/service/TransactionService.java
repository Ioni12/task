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
import com.example.task.request.TransactionRequest;
import com.example.task.response.CurrencyResponse;
import com.example.task.utils.CurrencyConverter;
import com.example.task.utils.TransformerUtility;
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

        //TODO join user and account
        Account account = accountRepository.findAccountByIdAndUsername(request.getAccountId(), request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found or does not belong to user", request.getAccountId()));

        String requestCurrency = request.getCurrency().toLowerCase();
        String accountCurrency = account.getCurrency().toLowerCase();
        log.debug("Currency info - requestCurrency: {}, accountCurrency: {}", requestCurrency, accountCurrency);

        BigDecimal convertedAmount;

        convertedAmount = applyTransaction(request, requestCurrency, accountCurrency, account);

        accountRepository.save(account);
        log.debug("Account balance persisted - accountId: {}, newBalance: {}", account.getId(), account.getAmount());


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

        BalanceHistory balanceHistory;
        if (request.getType() == TransactionType.DEPOSIT) {
            balanceHistory = TransformerUtility.createDepositBalanceHistory(account, saved, convertedAmount);
        } else {
            balanceHistory = TransformerUtility.createWithdrawBalanceHistory(account, saved, convertedAmount);
        }

        balanceHistory.setTransaction(saved);
        balanceHistoryRepository.save(balanceHistory);
        log.info("balance history saved successfuly");
        return saved;
    }

    private BigDecimal applyTransaction(TransactionRequest request,
                                    String requestCurrency,
                                    String accountCurrency,
                                    Account account
                                    ) {
        BigDecimal convertedAmount = BigDecimal.ZERO;

        if (request.getType() == TransactionType.DEPOSIT) {
            log.debug("Processing DEPOSIT");


            if (requestCurrency.equals(accountCurrency)) {
                convertedAmount = request.getAmount();
                log.debug("Same currency deposit, no conversion needed - amount: {}", convertedAmount);
            } else {
                try {
                    log.debug("Currency conversion required for DEPOSIT: {} -> {}", requestCurrency, accountCurrency);
                    CurrencyResponse response = currencyApiClient.getCurrency(requestCurrency);
                    log.debug("Currency API response received: {}", response);
                    convertedAmount = CurrencyConverter.convertDeposit(response, requestCurrency, accountCurrency, request.getAmount());
                    log.debug("Converted deposit amount: {} {} -> {} {}", request.getAmount(), requestCurrency, convertedAmount, accountCurrency);

                } catch (Exception e) {
                    log.error("Currency API call failed: {}", e.getMessage());
                    throw new TransactionException(Errors.CURRENCY_FETCH_FAILED);
                }
            }

            account.setAmount(account.getAmount().add(convertedAmount));
        } else if (request.getType() == TransactionType.WITHDRAW) {
            log.debug("Processing WITHDRAW");

            if (requestCurrency.equals(accountCurrency)) {
                convertedAmount = request.getAmount();
                log.debug("Same currency withdrawal, no conversion needed - amount: {}", convertedAmount);
            } else {
                try {
                    log.debug("Currency conversion required for WITHDRAW: {} -> {}", accountCurrency, requestCurrency);
                    CurrencyResponse response = currencyApiClient.getCurrency(accountCurrency);
                    log.debug("Currency API response received: {}", response);
                    convertedAmount = CurrencyConverter.convertWithdraw(response, accountCurrency, requestCurrency, request.getAmount());
                    log.debug("Converted withdrawal amount: {} {} -> {} {}", request.getAmount(), requestCurrency, convertedAmount, accountCurrency);
                } catch (Exception e) {
                    log.error("Currency API call failed: {}", e.getMessage());
                    throw new TransactionException(Errors.CURRENCY_FETCH_FAILED);
                }
            }

            if (account.getAmount().compareTo(convertedAmount) < 0) {
                log.warn("Insufficient funds - accountId: {}, balance: {}, requestedAmount: {}, currency: {}",
                        account.getId(), account.getAmount(), request.getAmount(), requestCurrency);
                throw new IllegalArgumentException("Insufficient funds");
            }

            account.setAmount(account.getAmount().subtract(convertedAmount));
        } else {
            log.error("Unsupported transaction type: {}", request.getType());
            throw new BadRequestException("Unsupported transaction type: " + request.getType());
        }
        return convertedAmount;

    }
}