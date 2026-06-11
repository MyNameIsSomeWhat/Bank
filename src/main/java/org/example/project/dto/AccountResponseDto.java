package org.example.project.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDto {
    private Long id;
    private String accountNumber;
    private BigDecimal balance;
    private String ownerName;   // Tên chủ tài khoản
}