package com.example.task.request;

import java.time.LocalDateTime;

public record TransactionRequest (
        String name,
        LocalDateTime transactionDate,
        Long amount,
        String transactionDetails
) {}