package com.example.task.response;

import com.example.task.entity.AccountStatus;
import com.example.task.entity.UserStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record UserResponse(
        Long id,
        String username,
        String name,
        String email,
        String defaultCurrency,
        String phone,
        LocalDate dateOfBirth,
        String street,
        String city,
        String country,
        String postalCode,
        UserStatus status,
        LocalDateTime createdAt,
        List<AccountInfo> accounts
) {
    public record AccountInfo(
            String iban,
            String accountName,
            BigDecimal amount,
            String currency,
            AccountStatus status,
            LocalDateTime createdAt
    ) {}
}