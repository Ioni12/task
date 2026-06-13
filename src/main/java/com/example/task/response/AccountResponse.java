package com.example.task.response;

import com.example.task.entity.AccountStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponse(
        Long id,
        String iban,
        BigDecimal amount,
        String currency,
        String accountName,
        AccountStatus status,
        LocalDateTime createdAt
) {}