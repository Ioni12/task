package com.example.task.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public abstract class BaseRequest{

    @NotBlank(message = "username cannot be blank")
    private String username;

    public BaseRequest(String username) {
        this.username = username;
    }
}
