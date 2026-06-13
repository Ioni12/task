package com.example.task.service;

import com.example.task.Errors;
import com.example.task.entity.Account;
import com.example.task.entity.AccountStatus;
import com.example.task.entity.User;
import com.example.task.entity.UserStatus;
import com.example.task.exception.TransactionException;
import com.example.task.repository.AccountRepository;
import com.example.task.repository.AccountSequenceRepository;
import com.example.task.repository.UserRepository;
import com.example.task.request.AccountRequest;
import com.example.task.utils.IbanGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountSequenceRepository sequenceRepository;

    public AccountService(AccountRepository accountRepository,
                          UserRepository userRepository,
                          AccountSequenceRepository sequenceRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.sequenceRepository = sequenceRepository;
    }

    @Cacheable(value = "account", key = "#username")
    public List<Account> getAllAccounts(String username) {
        log.debug("Getting all of the accounts for user with username: {}", username);
        return accountRepository.findAccountsByUsername(username);
    }

    @CacheEvict(value = "account", allEntries = true)
    public Account createAccount(AccountRequest accountRequest, String username) {
        log.info("creating account for user with username: {}", username);
        Account saved = new Account();
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        log.warn("user not found: {}", username);
                        return new EntityNotFoundException("user not found " + username);
                    });

            if (user.getStatus() != UserStatus.ACTIVE) {
                log.warn("Account creation denied for user: {} — status: {}", username, user.getStatus());
                throw new TransactionException(Errors.ACCOUNT_USER_NOT_ACTIVE);
            }

            String accountNumber = String.format("%016d", sequenceRepository.nextAccountNumber());
            String iban = IbanGenerator.generate(accountNumber);

            Account account = new Account();
            account.setUser(user);
            account.setIban(iban);
            account.setAmount(accountRequest.getAmount());
            account.setAccountName(accountRequest.getAccountName().toLowerCase().trim());
            account.setCurrency(accountRequest.getCurrency());
            account.setStatus(AccountStatus.ACTIVE);
            account.setCreatedAt(LocalDateTime.now());
             saved = accountRepository.save(account);
        } catch (DataIntegrityViolationException e) {
            throw new TransactionException(Errors.ACCOUNT_CONSTRAINT_VIOLATION_ERROR);
        }
        log.info("account created for user");
        return saved;
    }
}
