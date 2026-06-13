package com.example.task.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "account",
    uniqueConstraints = {
        @UniqueConstraint(
                columnNames = {"account_name", "user_id"}
        )
    })
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    //todo add iban instead of account name
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false, unique = true, length = 28)
    private String iban;

    @Column(name = "account_name", nullable = false)
    private String accountName;

    @Column(nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status = AccountStatus.ACTIVE;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "account")
    @JsonIgnore
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "account")
    @JsonIgnore
    private List<BalanceHistory> balanceHistories;
}
