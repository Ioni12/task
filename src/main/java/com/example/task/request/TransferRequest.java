package com.example.task.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {

    @NotNull(message = "fromAccountId is required")
    private Long fromAccountId;

    @NotBlank(message = "username cannot be empty")
    private String username;

    @NotNull(message = "amount is required")
    @Positive(message = "amount must be greater than zero")
    private BigDecimal amount;

    public TransferRequest(long fromAccountId, String username, BigDecimal amount) {
        this.fromAccountId = fromAccountId;
        this.username = username;
        this.amount = amount;
    }
}
