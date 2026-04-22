package com.example.task.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionRequest extends BaseRequest  {
        private LocalDateTime transactionDate;
        private BigDecimal amount;
        private String transactionDetails;
}