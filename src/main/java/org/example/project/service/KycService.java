package org.example.project.service;

import org.example.project.dto.KycDto;  // Giả sử có hoặc tạo stub
import org.example.project.entity.KycProfile;
import org.example.project.entity.User;
import org.example.project.repository.KycProfileRepository;
import org.example.project.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
public class KycService {

    private final KycProfileRepository kycRepository;
    private final UserRepository userRepository;

    public KycService(KycProfileRepository kycRepository, UserRepository userRepository) {
        this.kycRepository = kycRepository;
        this.userRepository = userRepository;
    }

    public KycDto uploadKyc(MultipartFile file, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // TODO: Upload file lên Cloudinary/AWS S3 (theo SRS UC-05)
        String documentUrl = "https://fake-cloud-storage/" + file.getOriginalFilename();

        KycProfile kyc = new KycProfile();
        kyc.setUser(user);
        kyc.setDocumentUrl(documentUrl);
        kyc.setStatus("PENDING");
        kyc.setSubmittedAt(LocalDateTime.now());
        kycRepository.save(kyc);

        return new KycDto(kyc.getId(), user.getId(), documentUrl, "PENDING");
    }

    public void approveKyc(Long kycId) {
        KycProfile kyc = kycRepository.findById(kycId)
                .orElseThrow(() -> new RuntimeException("KYC not found"));

        kyc.setStatus("CONFIRMED");
        kycRepository.save(kyc);

        // Cập nhật User isKyc = true
        User user = kyc.getUser();
        user.setKyc(true);
        userRepository.save(user);
    }
}