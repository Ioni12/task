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

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody AccountRequest request) {
        long start = System.currentTimeMillis();
        log.info("Creating account, started at: {}", start);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Account created = accountService.createAccount(request, username);
        log.info("Account created in: {} ms", System.currentTimeMillis() - start);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAccounts() {
        long start = System.currentTimeMillis();
        log.info("Fetching accounts, started at: {}", start);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Account> accounts = accountService.getAllAccounts(username);
        log.info("Accounts fetched in: {} ms", System.currentTimeMillis() - start);
        return ResponseEntity.ok(accounts.stream().map(this::toResponse).toList());
    }

    private AccountResponse toResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getIban(),
                account.getAmount(),
                account.getCurrency(),
                account.getAccountName(),
                account.getStatus(),
                account.getCreatedAt()
        );
    }
}