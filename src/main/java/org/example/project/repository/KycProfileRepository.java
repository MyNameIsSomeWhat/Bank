package org.example.project.repository;

import org.example.project.entity.KycProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KycProfileRepository extends JpaRepository<KycProfile, Long> {
}