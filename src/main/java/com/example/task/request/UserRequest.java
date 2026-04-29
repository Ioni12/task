package com.example.task.request;

public record UserRequest(
        BaseRequest base,
        String email,
        String password){}
