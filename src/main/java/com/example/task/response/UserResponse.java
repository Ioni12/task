package com.example.task.response;

import com.example.task.entity.Account;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class UserResponse {

    private Long id;
    private String username;
    private String name;
    private String email;
    private String defaultCurrency;
    private List<AccountInfo> accounts;

    public record AccountInfo(String accountName, BigDecimal amount, String currency) {}

    public UserResponse(Long id, String username, String name,
                        String email, String defaultCurrency) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.email = email;
        this.defaultCurrency = defaultCurrency;
    }

    public UserResponse(Long id, String username, String name,
                        String email, String defaultCurrency,
                        List<AccountInfo> accounts) {
        this(id, username, name, email, defaultCurrency);
        this.accounts = accounts;
    }

}
