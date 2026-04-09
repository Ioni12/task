package com.example.task.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionRequest (
        String name,
        LocalDateTime transactionDate,
        BigDecimal amount,
        String transactionDetails
) {}