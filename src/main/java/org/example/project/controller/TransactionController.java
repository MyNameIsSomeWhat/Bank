package org.example.project.controller;

import org.example.project.dto.TransferRequest;
import org.example.project.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransferRequest request) {
        String result = transactionService.transfer(request);
        return ResponseEntity.ok(result);
    }
}