package org.example.project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    private String accountType = "SAVINGS";

    private boolean isActive = true;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime createdAt = LocalDateTime.now();
}