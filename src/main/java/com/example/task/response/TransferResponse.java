package com.example.task.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransferResponse {

    private String status;
    private String message;
    private String fromAccount;
    private String toAccount;
    private BigDecimal currentBalance;
    private BigDecimal balanceBefore;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime timestamp;

    public TransferResponse(
            String status,
            String message,
            String fromAccount,
            String toAccount,
            String currency,
            BigDecimal currentBalance,
            BigDecimal amount
    ) {
        this.status = status;
        this.message = message;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.currentBalance = currentBalance;
        this.balanceBefore = currentBalance.add(amount);
        this.currency = currency;
        this.timestamp = LocalDateTime.now();
    }

}
