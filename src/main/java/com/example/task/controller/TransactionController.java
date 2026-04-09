package com.example.task.controller;

import com.example.task.entity.Transaction;
import com.example.task.request.TransactionRequest;
import com.example.task.response.TransactionResponse;
import com.example.task.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<TransactionResponse>> getAllResponsesByUserName(@RequestParam String name) {
        List<Transaction> transactions = transactionService.getTransactionsByName(name);
        List<TransactionResponse> responses = new ArrayList<>();
        for(Transaction transaction: transactions) {
            responses.add(new TransactionResponse(transaction.getTransactionDate(), transaction.getAmount(), transaction.getTransactionDetails()));
        }

        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@RequestBody TransactionRequest request) {
        Transaction created = transactionService.createTransaction(request);
        TransactionResponse response = new TransactionResponse(
                created.getTransactionDate(),
                created.getAmount(),
                created.getTransactionDetails()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
