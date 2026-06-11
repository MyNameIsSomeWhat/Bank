package org.example.project.controller;

import org.example.project.dto.TransactionResponseDto;
import org.example.project.dto.TransferRequest;
import org.example.project.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // FR-07: Chuyển tiền
    @PostMapping("/transfer")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> transfer(@RequestBody TransferRequest request) {
        transactionService.transfer(request);
        return ResponseEntity.ok("Chuyển tiền thành công");
    }

    // FR-08: Xem sao kê lịch sử giao dịch
    @GetMapping("/history/{accountNumber}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Page<TransactionResponseDto>> getHistory(
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(transactionService.getTransactionHistory(accountNumber, page, size));
    }
}