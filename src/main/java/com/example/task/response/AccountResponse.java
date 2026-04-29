package com.example.task.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponse(Long id, BigDecimal amount) {
}
