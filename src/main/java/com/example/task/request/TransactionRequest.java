package com.example.task.request;

import com.example.task.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionRequest (
        BaseRequest base,
        Long accountId,
        LocalDateTime transactionDate,
        BigDecimal amount,
        String transactionDetails,
        TransactionType type ){}