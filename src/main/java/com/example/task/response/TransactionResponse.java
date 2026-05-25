package com.example.task.response;

import com.example.task.entity.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionResponse {

    private LocalDateTime transactionDate;
    private BigDecimal amount;
    private String transactionDetails;
    private TransactionType type;
    private String currency;

    public TransactionResponse(
            LocalDateTime transactionDate,
            BigDecimal amount,
            String transactionDetails,
            TransactionType type,
            String currency) {
        this.transactionDate = transactionDate;
        this.amount = amount;
        this.transactionDetails = transactionDetails;
        this.type = type;
        this.currency = currency;
    }
}
