package com.example.task.controller;

import com.example.task.entity.Account;
import com.example.task.request.AccountRequest;
import com.example.task.response.AccountResponse;
import com.example.task.service.AccountService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody AccountRequest request) {
        Long start = System.currentTimeMillis();
        log.info("started creating a account at time: {}", start);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Account created = accountService.createAccount(request, username);
        AccountResponse response = new AccountResponse(
                created.getId(),
                created.getIban(),
                created.getAmount(),
                created.getCurrency(),
                created.getAccountName());
        log.info("created account at: {} ms", System.currentTimeMillis() - start);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAccounts() {
        Long start = System.currentTimeMillis();
        log.info("started searching accounts at time: {}", start);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Account> accounts = accountService.getAllAccounts(username);
        List<AccountResponse> response = new ArrayList<>();
        for(Account account: accounts) {
            response.add(new AccountResponse(
                    account.getId(),
                    account.getIban(),
                    account.getAmount(),
                    account.getCurrency(),
                    account.getAccountName()));
        }
        log.info("searched accounts at: {} ms", System.currentTimeMillis() - start);
        return ResponseEntity.ok(response);
    }
}
