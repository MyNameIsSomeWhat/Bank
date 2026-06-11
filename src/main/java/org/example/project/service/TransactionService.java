package org.example.project.service;

import org.example.project.dto.TransactionResponseDto;
import org.example.project.dto.TransferRequest;
import org.example.project.entity.Account;
import org.example.project.entity.Transaction;
import org.example.project.repository.AccountRepository;
import org.example.project.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(AccountRepository accountRepository,
                              TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void transfer(TransferRequest request) {
        Account source = accountRepository.findByAccountNumber(request.getSourceAccountNumber())
                .orElseThrow(() -> new RuntimeException("Source account not found"));

        Account target = accountRepository.findByAccountNumber(request.getTargetAccountNumber())
                .orElseThrow(() -> new RuntimeException("Target account not found"));

        if (source.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // Trừ tiền nguồn
        source.setBalance(source.getBalance().subtract(request.getAmount()));
        // Cộng tiền đích
        target.setBalance(target.getBalance().add(request.getAmount()));

        accountRepository.save(source);
        accountRepository.save(target);

        // Tạo Transaction record
        Transaction tx = new Transaction();
        tx.setFromAccount(source);
        tx.setToAccount(target);
        tx.setAmount(request.getAmount());
        tx.setTransactionType("TRANSFER");
        tx.setTimestamp(LocalDateTime.now());
        transactionRepository.save(tx);
    }

    public Page<TransactionResponseDto> getTransactionHistory(String accountNumber, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        // Query OR from/to account (theo SRS UC-06)
        Page<Transaction> txPage = transactionRepository.findByFromAccount_AccountNumberOrToAccount_AccountNumber(
                accountNumber, accountNumber, pageable);

        return txPage.map(tx -> new TransactionResponseDto(
                tx.getId(),
                tx.getFromAccount().getAccountNumber(),
                tx.getToAccount().getAccountNumber(),
                tx.getAmount(),
                tx.getTransactionType(),
                tx.getTimestamp()
        ));
    }
}