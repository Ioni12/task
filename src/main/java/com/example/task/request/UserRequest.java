package com.example.task.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRequest extends BaseRequest{

    @NotBlank(message = "please provide a name")
    private String name;

    @NotBlank(message = "please provide a email")
    private String email;

    @NotBlank(message = "please provide a personal Id")
    private String personalId;

    @NotBlank(message = "please provide a password")
    private String password;

    @NotBlank(message = "please provide the default currency")
    private String defaultCurrency;

    public UserRequest(String username, String email, String password, String defaultCurrency) {
        super(username);
        this.email = email;
        this.password = password;
        this.defaultCurrency = defaultCurrency;
    }
}
