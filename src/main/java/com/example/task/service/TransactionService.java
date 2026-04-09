package com.example.task.service;

import com.example.task.entity.Transaction;
import com.example.task.entity.User;
import com.example.task.repository.TransactionRepository;
import com.example.task.repository.UserRepository;
import com.example.task.request.TransactionRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public List<Transaction> getTransactionsByName(String name) {
        return transactionRepository.findByUserName(name);
    }

    public Transaction createTransaction(TransactionRequest request) {
        User user = userRepository.findByName(request.name()).
                orElseThrow(() -> new EntityNotFoundException("user not found" + request.name()));

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setTransactionDate(request.transactionDate());
        transaction.setAmount(request.amount().
                multiply(BigDecimal.valueOf(100))
                .longValue());
        transaction.setTransactionDetails(request.transactionDetails());

        return transactionRepository.save(transaction);
    }
}
