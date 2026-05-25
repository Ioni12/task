package com.example.task.request;

import com.example.task.entity.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
public class TransactionRequest extends BaseRequest {

    @NotNull(message = "accountId is required")
    private Long accountId;

    @NotNull(message = "transactionDate is required")
    private LocalDateTime transactionDate;

    @NotNull(message = "amount is required")
    @Positive(message = "amount must be greater than zero")
    private BigDecimal amount;

    @NotBlank(message = "transactionDetails is required")
    private String transactionDetails;

    @NotNull(message = "type is required")
    private TransactionType type;

    @NotBlank(message = "currency is required")
    private String currency;

    public TransactionRequest(
            String username,
            Long accountId,
            LocalDateTime transactionDate,
            BigDecimal amount,
            String transactionDetails,
            TransactionType type
    ) {
        super(username);
        this.accountId = accountId;
        this.transactionDate = transactionDate;
        this.amount = amount;
        this.transactionDetails = transactionDetails;
        this.type = type;
    }
}