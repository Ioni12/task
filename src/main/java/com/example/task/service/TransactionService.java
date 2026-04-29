package com.example.task.service;

import com.example.task.entity.Account;
import com.example.task.entity.Transaction;
import com.example.task.entity.TransactionType;
import com.example.task.entity.User;
import com.example.task.repository.AccountRepository;
import com.example.task.repository.TransactionRepository;
import com.example.task.repository.UserRepository;
import com.example.task.request.TransactionRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public TransactionService(
            TransactionRepository transactionRepository,
            AccountRepository accountRepository,
            UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public List<Transaction> getTransactionsByName(String name) {
        log.debug("Fetching transactions for user: {}", name);
        return transactionRepository.findByUserNameTransactions(name);
    }

    public Transaction createTransaction(TransactionRequest request) {
        User user = userRepository.findByUsername(request.base().username())
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + request.base().username()));

        Account account = accountRepository.findById(request.accountId())
                .orElseThrow(() -> new EntityNotFoundException("Account not found: " + request.accountId()));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Account does not belong to this user");
        }

        if(request.type() == TransactionType.DEPOSIT) {
            account.setAmount(account.getAmount().add(request.amount()));
        } else if (request.type() == TransactionType.WITHDRAW) {
            if (account.getAmount().compareTo(request.amount()) < 0) {
                throw new IllegalArgumentException("Insufficient funds");
            }
            account.setAmount(account.getAmount().subtract(request.amount()));
        } else {
            throw new IllegalArgumentException("unsupported transaction type: " + request.type());
        }

        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(request.amount());
        transaction.setTransactionDate(request.transactionDate());
        transaction.setTransactionDetails(request.transactionDetails());
        transaction.setType(request.type());

        return transactionRepository.save(transaction);
    }
}