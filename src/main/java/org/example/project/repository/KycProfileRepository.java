package org.example.project.repository;

import org.example.project.entity.KycProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KycProfileRepository extends JpaRepository<KycProfile, Long> {

    Optional<KycProfile> findByUserId(Long userId);
}