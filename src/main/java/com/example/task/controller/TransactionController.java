package com.example.task.controller;

import com.example.task.entity.Transaction;
import com.example.task.exception.BadRequestException;
import com.example.task.exception.ResourceNotFoundException;
import com.example.task.request.TransactionRequest;
import com.example.task.response.TransactionResponse;
import com.example.task.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<TransactionResponse>> getAllResponsesByUserName(@RequestParam String name) {
        long start = System.currentTimeMillis();
        log.info("Fetching transactions for user: {}, time: {}", name, start);

        if (name.isBlank()) {
            throw new BadRequestException("name parameter must not be blank");
        }

        List<Transaction> transactions = transactionService.getTransactionsByName(name);

        if (transactions.isEmpty()) {
            throw new ResourceNotFoundException("Transactions for user", name);
        }

        log.info("Found {} transactions for user: {}, time: {}", transactions.size(), name, System.currentTimeMillis() - start);
        List<TransactionResponse> responses = new ArrayList<>();
        for (Transaction transaction : transactions) {
            responses.add(new TransactionResponse(transaction.getTransactionDate(), transaction.getAmount(), transaction.getTransactionDetails()));
        }
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@RequestBody TransactionRequest request) {
        long start = System.currentTimeMillis();
        log.info("Create transaction request for user: {}, time: {}", request.getName(), start);
        Transaction created = transactionService.createTransaction(request);
        log.info("Transaction created with id: {}, time: {}", created.getId(), System.currentTimeMillis() - start);
        TransactionResponse response = new TransactionResponse(
                created.getTransactionDate(),
                created.getAmount(),
                created.getTransactionDetails()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}