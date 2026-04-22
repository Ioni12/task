package com.example.task.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionResponse {

    private LocalDateTime transactionDate;
    private BigDecimal amount;
    private String transactionDetails;

    public TransactionResponse(LocalDateTime transactionDate, BigDecimal amount, String transactionDetails) {
        this.transactionDate = transactionDate;
        this.amount = amount;
        this.transactionDetails = transactionDetails;
    }
}
