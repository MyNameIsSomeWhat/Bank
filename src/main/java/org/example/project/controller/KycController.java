package org.example.project.controller;

import org.example.project.entity.KycProfile;
import org.example.project.entity.enums.KycStatus;
import org.example.project.entity.User;
import org.example.project.repository.KycProfileRepository;
import org.example.project.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/kyc")
public class KycController {

    private final UserRepository userRepository;
    private final KycProfileRepository kycProfileRepository;

    public KycController(UserRepository userRepository, KycProfileRepository kycProfileRepository) {
        this.userRepository = userRepository;
        this.kycProfileRepository = kycProfileRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadKyc(@RequestParam Long userId,
                                       @RequestParam("document") org.springframework.web.multipart.MultipartFile document) throws IOException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String fileName = UUID.randomUUID() + "_" + document.getOriginalFilename();
        Path uploadPath = Paths.get("uploads/" + fileName);
        Files.createDirectories(uploadPath.getParent());
        Files.write(uploadPath, document.getBytes());

        KycProfile kyc = new KycProfile();
        kyc.setUser(user);
        kyc.setIdCardFrontUrl("/uploads/" + fileName);   // Field thực tế trong entity
        kyc.setStatus(KycStatus.PENDING);
        kycProfileRepository.save(kyc);

        return ResponseEntity.ok("KYC uploaded successfully");
    }
}   