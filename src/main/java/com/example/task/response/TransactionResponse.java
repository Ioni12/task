package com.example.task.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionResponse {

    private LocalDateTime transactionDate;
    private Long amount;
    private String transactionsDetails;

    public TransactionResponse(LocalDateTime transactionDate, Long amount, String transactionsDetails) {
        this.transactionDate = transactionDate;
        this.amount = amount;
        this.transactionsDetails = transactionsDetails;
    }
}
