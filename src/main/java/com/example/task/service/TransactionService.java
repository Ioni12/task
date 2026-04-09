package com.example.task.service;

import com.example.task.entity.Transaction;
import com.example.task.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> getTransactionsByName(String name) {
        return transactionRepository.findByUserName(name);
    }
}
