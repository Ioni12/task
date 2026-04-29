package com.example.task.service;

import com.example.task.entity.Account;
import com.example.task.entity.User;
import com.example.task.repository.AccountRepository;
import com.example.task.repository.UserRepository;
import com.example.task.request.AccountRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public List<Account> getAllAccounts(Long userId) {
        log.debug("Getting all of the accounts for user with id: {}", userId);
        return accountRepository.findAccountsByUserId(userId);
    }

    public List<Account> getAllAccounts(String username) {
        log.debug("Getting all of the accounts for user with username: {}", username);
        return accountRepository.findAccountsByUsername(username);
    }

    public Account createAccount(AccountRequest accountRequest, String username) {
        log.info("creating account for user with username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("user not found: {}", username);
                    return new EntityNotFoundException("user not found " + username);
                        });
        Account account = new Account();
        account.setUser(user);
        account.setAmount(accountRequest.amount());
        Account saved = accountRepository.save(account);
        log.info("account created for user");
        return saved;
    }
}
