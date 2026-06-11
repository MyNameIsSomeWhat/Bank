package org.example.project.repository;

import org.example.project.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    Page<Account> findAll(Pageable pageable);

    // Tìm tài khoản theo user
    Optional<Account> findByUserId(Long userId);
}