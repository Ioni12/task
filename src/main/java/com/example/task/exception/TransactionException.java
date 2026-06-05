package com.example.task.exception;

import com.example.task.Errors;
import lombok.Data;

@Data
public class TransactionException extends RuntimeException {

    private Errors error;
    public TransactionException(Errors error) {
        super(error.message);
        this.error=error;
    }
}