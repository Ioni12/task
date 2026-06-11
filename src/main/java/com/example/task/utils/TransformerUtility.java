package com.example.task.utils;

import com.example.task.entity.Account;
import com.example.task.entity.BalanceHistory;
import com.example.task.entity.Transaction;
import com.example.task.entity.TransactionType;
import com.example.task.request.TransferRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class TransformerUtility {

    private TransformerUtility() {}

    public static Transaction createWithdrawTransaction(Account senderAccount,
                                                        BigDecimal amount,
                                                        Account recipientAccount) {
        return Transaction.builder()
                .account(senderAccount)
                .amount(amount.negate())
                .currency(senderAccount.getCurrency())
                .transactionDate(LocalDateTime.now())
                .type(TransactionType.WITHDRAW)
                .transactionDetails("Transfer to account #" + recipientAccount.getId())
                .build();
    }

    public static Transaction createDepositTransaction(Account recipientAccount,
                                                       BigDecimal convertedTransferAmount,
                                                       Account senderAccount) {
        return Transaction.builder()
                .account(recipientAccount)
                .amount(convertedTransferAmount.negate())
                .currency(recipientAccount.getCurrency())
                .transactionDate(LocalDateTime.now())
                .type(TransactionType.DEPOSIT)
                .transactionDetails("Transfer from account #" + senderAccount.getId())
                .build();
    }

    public static BalanceHistory createWithdrawBalanceHistory(Account senderAccount,
                                                              Transaction withdrawlTransaction,
                                                              BigDecimal amount) {
        return BalanceHistory.builder()
                .account(senderAccount)
                .transaction(withdrawlTransaction)
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
                .balanceBefore(recipientAccount.getAmount().add(convertedTransferAmount))
                .balanceAfter(recipientAccount.getAmount())
                .changeAmount(convertedTransferAmount)
                .transactionType(TransactionType.DEPOSIT)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
