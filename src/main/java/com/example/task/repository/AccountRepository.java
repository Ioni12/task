package com.example.task.repository;

import com.example.task.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("SELECT a FROM Account a WHERE a.user.id = :userId")
    List<Account> findAccountsByUserId(@Param("userId") Long userId);

    @Query("SELECT a FROM Account a WHERE LOWER(a.user.username) = LOWER(:username)")
    List<Account> findAccountsByUsername(@Param("username") String username);

    @Query("SELECT a FROM Account a WHERE a.id = :accountId")
    Optional<Account> findAccountById(@Param("accountId") long accountId);
}
