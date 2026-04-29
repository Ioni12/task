package com.example.task.request;

public record LoginRequest(
        BaseRequest base,
        String password){}
