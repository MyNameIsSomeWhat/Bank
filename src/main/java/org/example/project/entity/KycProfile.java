package org.example.project.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.project.entity.enums.KycStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "kyc_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String idCardFrontUrl;
    private String idCardBackUrl;

    @Enumerated(EnumType.STRING)
    private KycStatus status = KycStatus.PENDING;

    private LocalDateTime submittedAt = LocalDateTime.now();
    private LocalDateTime reviewedAt;
}