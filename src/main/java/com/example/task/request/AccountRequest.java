package com.example.task.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountRequest extends BaseRequest{

    @NotNull(message = "amount is required")
    @Positive(message = "amount must be greater than zero")
    private BigDecimal amount;

    @NotBlank(message = "please give a account name")
    private String accountName;

    @NotBlank(message = "please give a account currency")
    private String currency;

    public AccountRequest(String username, BigDecimal amount, String currency) {
        super(username);
        this.amount = amount;
        this.currency = currency;
    }
}
