package com.example.task.repository;

import com.example.task.entity.BalanceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BalanceHistoryRepository extends JpaRepository<BalanceHistory, Long> {
    @Query("SELECT bh FROM BalanceHistory bh WHERE bh.account.id = :account_id")
    List<BalanceHistory> findByAccountId(@Param("account_id")Long account_id);
}
