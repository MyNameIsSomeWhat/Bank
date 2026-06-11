package org.example.project.controller;

import org.example.project.entity.Account;
import org.example.project.entity.Transaction;
import org.example.project.repository.AccountRepository;
import org.example.project.repository.TransactionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionController(AccountRepository accountRepository,
                                 TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @PostMapping("/transfer")
    @Transactional
    public ResponseEntity<?> transfer(@RequestParam String targetAccountNumber,
                                      @RequestParam BigDecimal amount) {

        // Tạm thời dùng accountId = 1 (sau này sẽ cải tiến với SecurityContext)
        Account source = accountRepository.findByUserId(1L)
                .orElseThrow(() -> new RuntimeException("Source account not found"));

        Account target = accountRepository.findByAccountNumber(targetAccountNumber)
                .orElseThrow(() -> new RuntimeException("Target account not found"));

        if (source.getBalance().compareTo(amount) < 0) {
            return ResponseEntity.status(409).body("Insufficient balance");
        }

        source.setBalance(source.getBalance().subtract(amount));
        target.setBalance(target.getBalance().add(amount));

        Transaction tx = new Transaction();
        tx.setFromAccount(source);
        tx.setToAccount(target);
        tx.setAmount(amount);
        transactionRepository.save(tx);

        accountRepository.save(source);
        accountRepository.save(target);

        return ResponseEntity.ok("Transfer successful");
    }
}