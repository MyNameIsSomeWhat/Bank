package org.example.project.repository;

import org.example.project.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // UC-06: Query OR from_account hoặc to_account (theo SRS)
    Page<Transaction> findByFromAccount_AccountNumberOrToAccount_AccountNumber(
            String fromAccountNumber, String toAccountNumber, Pageable pageable);
}