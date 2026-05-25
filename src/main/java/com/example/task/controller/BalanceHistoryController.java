package com.example.task.controller;

import com.example.task.entity.BalanceHistory;
import com.example.task.repository.BalanceHistoryRepository;
import com.example.task.request.BalanceHistoryRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/balanceHistory")
public class BalanceHistoryController {
    private final BalanceHistoryRepository balanceHistoryRepository;

    public BalanceHistoryController(BalanceHistoryRepository balanceHistoryRepository) {
        this.balanceHistoryRepository = balanceHistoryRepository;
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<List<BalanceHistory>> getBalanceHistoryByAccountId(@PathVariable Long accountId) {
        log.info("Fetching balance history for account: {}", accountId);
        List<BalanceHistory> balanceHistories = balanceHistoryRepository.findByAccountId(accountId);
        return ResponseEntity.ok(balanceHistories);
    }
}
