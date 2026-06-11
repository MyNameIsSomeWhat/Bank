package org.example.project.controller;

import org.example.project.dto.AccountResponseDto;
import org.example.project.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // FR-06: Vấn tin số dư tài khoản
    @GetMapping("/balance/{accountNumber}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<AccountResponseDto> getBalance(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.getBalance(accountNumber));
    }

    // FR-05: Quản lý tài khoản (Admin/Staff)
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<AccountResponseDto>> getAllAccounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(accountService.getAllAccounts(page, size));
    }
}