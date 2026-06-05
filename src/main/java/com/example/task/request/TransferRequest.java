package com.example.task.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {

    @NotBlank(message = "fromAccountName is required")
    private String fromAccountName;

    @NotBlank(message = "username cannot be empty")
    private String username;

    @NotNull(message = "the toAccountId is required")
    private Long accountId;

    @NotNull(message = "amount is required")
    @Positive(message = "amount must be greater than zero")
    private BigDecimal amount;

    public TransferRequest(String fromAccountName, String username, Long accountId, BigDecimal amount) {
        this.fromAccountName = fromAccountName;
        this.username = username;
        this.accountId = accountId;
        this.amount = amount;
    }
}
