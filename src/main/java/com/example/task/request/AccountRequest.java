package com.example.task.request;

import java.math.BigDecimal;

public record AccountRequest(
        BaseRequest base,
        BigDecimal amount){}
