package com.example.task.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionRequest (
        BaseRequest base,
        LocalDateTime transactionDate,
        BigDecimal amount,
        String transactionDetails
) {}