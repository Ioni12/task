package com.example.task;


public enum Errors {

    USER_NOT_FOUND(101, "User not found"),
    USER_ALREADY_EXISTS(102, "The user already exists"),
    INVALID_CREDENTIALS(103, "Invalid username or password"),
    INVALID_PASSWORD(104, "Current password is incorrect"),

    ACCOUNT_NOT_FOUND(201, "Account not found"),
    ACCOUNT_ALREADY_EXISTS(202, "An account with these credentials already exists"),
    ACCOUNT_CONSTRAINT_VIOLATION_ERROR(203, "You are trying to insert an already registered account name"),

    TRANSACTION_NOT_FOUND(301, "Transaction not found"),
    INSUFFICIENT_FUNDS(302, "Insufficient funds for this operation"),
    INVALID_AMOUNT(303, "Amount must be greater than zero"),
    SAME_ACCOUNT_TRANSFER(304, "Source and destination accounts must be different"),

    CURRENCY_FETCH_FAILED(401, "Failed to fetch currency rates"),
    UNSUPPORTED_CURRENCY(402, "Currency is not supported");

    public final Integer errorCode;
    public final String message;
    Errors(Integer errorCode, String message) {
        this.errorCode=errorCode;
        this.message=message;
    }
}
