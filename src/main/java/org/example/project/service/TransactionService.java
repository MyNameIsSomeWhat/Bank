package org.example.project.service;

import org.example.project.dto.TransactionResponseDto;
import org.example.project.dto.TransferRequest;
import org.example.project.entity.Account;
import org.example.project.entity.Transaction;
import org.example.project.entity.User;
import org.example.project.entity.enums.TransactionType;
import org.example.project.repository.AccountRepository;
import org.example.project.repository.TransactionRepository;
import org.example.project.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(AccountRepository accountRepository,
                              TransactionRepository transactionRepository,
                              UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TransactionResponseDto transfer(TransferRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Account fromAccount = accountRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Source account not found"));

        Account toAccount = accountRepository.findByAccountNumber(request.getTargetAccountNumber())
                .orElseThrow(() -> new RuntimeException("Target account not found"));

        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // Thực hiện chuyển tiền
        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));

        Transaction tx = Transaction.builder()
                .fromAccount(fromAccount)
                .toAccount(toAccount)
                .amount(request.getAmount())
                .description(request.getDescription() != null ? request.getDescription() : "Chuyển tiền")
                .type(TransactionType.DEBIT)
                .build();

        transactionRepository.save(tx);
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        return mapToDto(tx, fromAccount.getAccountNumber());
    }

    public Page<TransactionResponseDto> getTransactionHistory(Long accountId, Pageable pageable) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Account account = accountRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Page<Transaction> transactions = transactionRepository
                .findByFromAccountIdOrToAccountId(account.getId(), account.getId(), pageable);

        return transactions.map(tx -> mapToDto(tx, account.getAccountNumber()));
    }

    private TransactionResponseDto mapToDto(Transaction tx, String currentAccountNumber) {
        String type = tx.getFromAccount().getAccountNumber().equals(currentAccountNumber) ? "DEBIT" : "CREDIT";

        return TransactionResponseDto.builder()
                .id(tx.getId())
                .fromAccountNumber(tx.getFromAccount().getAccountNumber())
                .toAccountNumber(tx.getToAccount().getAccountNumber())
                .amount(tx.getAmount())
                .type(type)
                .description(tx.getDescription())
                .transactionDate(tx.getTransactionDate())
                .build();
    }
}