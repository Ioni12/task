package com.example.task.utils;

import com.example.task.entity.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class TransformerUtility {

    private TransformerUtility() {}

    // Transfer withdraw
    public static Transaction createWithdrawTransaction(Account senderAccount,
                                                        BigDecimal amount,
                                                        Account recipientAccount,
                                                        Transfer transfer) {
        return Transaction.builder()
                .account(senderAccount)
                .amount(amount.negate())
                .currency(senderAccount.getCurrency())
                .transactionDate(LocalDateTime.now())
                .type(TransactionType.WITHDRAW)
                .transactionDetails("Transfer to account #" + recipientAccount.getId())
                .transfer(transfer)
                .build();
    }

    // Transfer deposit
    public static Transaction createDepositTransaction(Account recipientAccount,
                                                       BigDecimal convertedTransferAmount,
                                                       Account senderAccount,
                                                       Transfer transfer) {
        return Transaction.builder()
                .account(recipientAccount)
                .amount(convertedTransferAmount)
                .currency(recipientAccount.getCurrency())
                .transactionDate(LocalDateTime.now())
                .type(TransactionType.DEPOSIT)
                .transactionDetails("Transfer from account #" + senderAccount.getId())
                .transfer(transfer)
                .build();
    }

    // Standalone withdraw
    public static Transaction createWithdrawTransaction(Account account,
                                                        BigDecimal amount) {
        return Transaction.builder()
                .account(account)
                .amount(amount.negate())
                .currency(account.getCurrency())
                .transactionDate(LocalDateTime.now())
                .type(TransactionType.WITHDRAW)
                .transactionDetails("Withdrawal")
                .build();
    }

    // Standalone deposit
    public static Transaction createDepositTransaction(Account account,
                                                       BigDecimal amount) {
        return Transaction.builder()
                .account(account)
                .amount(amount)
                .currency(account.getCurrency())
                .transactionDate(LocalDateTime.now())
                .type(TransactionType.DEPOSIT)
                .transactionDetails("Deposit")
                .build();
    }

    public static BalanceHistory createWithdrawBalanceHistory(Account senderAccount,
                                                              Transaction withdrawalTransaction,
                                                              BigDecimal amount) {
        return BalanceHistory.builder()
                .account(senderAccount)
                .transaction(withdrawalTransaction)
                .balanceBefore(senderAccount.getAmount().add(amount))
                .balanceAfter(senderAccount.getAmount())
                .changeAmount(amount.negate())
                .transactionType(TransactionType.WITHDRAW)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static BalanceHistory createDepositBalanceHistory(Account recipientAccount,
                                                             Transaction depositTransaction,
                                                             BigDecimal convertedTransferAmount) {
        return BalanceHistory.builder()
                .account(recipientAccount)
                .transaction(depositTransaction)
                .balanceBefore(recipientAccount.getAmount().subtract(convertedTransferAmount))
                .balanceAfter(recipientAccount.getAmount())
                .changeAmount(convertedTransferAmount)
                .transactionType(TransactionType.DEPOSIT)
                .timestamp(LocalDateTime.now())
                .build();
    }
}