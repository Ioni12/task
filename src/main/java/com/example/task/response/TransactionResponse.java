package com.example.task.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionResponse {

    private LocalDateTime transactionDate;
    private Long amount;
    private String transactionDetails;

    public TransactionResponse(LocalDateTime transactionDate, Long amount, String transactionDetails) {
        this.transactionDate = transactionDate;
        this.amount = amount;
        this.transactionDetails = transactionDetails;
    }
}
