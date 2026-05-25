package com.example.task.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest extends BaseRequest{

    @NotBlank(message = "please provide a password")
    private String password;

    public LoginRequest(String username, String password) {
        super(username);
        this.password = password;
    }

}
