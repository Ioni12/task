package com.example.task.service;

import com.example.task.entity.Transaction;
import com.example.task.entity.User;
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
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public List<Transaction> getTransactionsByName(String name) {
        log.debug("Fetching transactions for user: {}", name);
        return transactionRepository.findByUserNameTransactions(name);
    }

    public Transaction createTransaction(TransactionRequest request) {
        log.info("Creating transaction for user: {}", request.base().name());
        User user = userRepository.findByName(request.base().name())
                .orElseThrow(() -> {
                    log.warn("User not found: {}", request.base().name());
                    return new EntityNotFoundException("user not found" + request.base().name());
                });

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setTransactionDate(request.transactionDate());
        transaction.setAmount(request.amount()
                .multiply(BigDecimal.valueOf(100))
                .longValue());
        transaction.setTransactionDetails(request.transactionDetails());

        Transaction saved = transactionRepository.save(transaction);
        log.info("Transaction created with id: {} for user: {}", saved.getId(), user.getName());
        return saved;
    }
}