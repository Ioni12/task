package com.example.task.controller;

import com.example.task.entity.Transaction;
import com.example.task.entity.TransactionType;
import com.example.task.request.TransferRequest;
import com.example.task.response.TransferResponse;
import com.example.task.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/transfer")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }


    @PostMapping
    public ResponseEntity<TransferResponse> transfer(@Valid @RequestBody TransferRequest request) {

        List<Transaction> transactions = transferService.transfer(request);

        Transaction fromTx = null;
        for(Transaction t: transactions) {
            if(t.getType() == TransactionType.WITHDRAW) {
                fromTx = t;
                break;
            }
        }

        TransferResponse response = new TransferResponse(
                "201",
                "transfer successful",
                String.valueOf(request.getFromAccountName()),
                request.getUsername(),
                fromTx.getCurrency(),
                fromTx.getAccount().getAmount(),
                request.getAmount()
        );
        return ResponseEntity.ok(response);
    }
}
