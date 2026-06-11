package org.example.project.service;

import org.example.project.entity.KycProfile;
import org.example.project.entity.User;
import org.example.project.entity.enums.KycStatus;
import org.example.project.repository.KycProfileRepository;
import org.example.project.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class KycService {

    private final KycProfileRepository kycProfileRepository;
    private final UserRepository userRepository;

    public KycService(KycProfileRepository kycProfileRepository, UserRepository userRepository) {
        this.kycProfileRepository = kycProfileRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public String uploadKyc(String username, MultipartFile front, MultipartFile back) {
        User user = userRepository.findByUsername(username).orElseThrow();

        // TODO: Upload to Cloudinary / AWS S3 → lấy URL
        String frontUrl = "https://example.com/front.jpg"; // Thay bằng URL thật sau
        String backUrl = "https://example.com/back.jpg";

        KycProfile kyc = KycProfile.builder()
                .user(user)
                .idCardFrontUrl(frontUrl)
                .idCardBackUrl(backUrl)
                .status(KycStatus.PENDING)
                .build();

        kycProfileRepository.save(kyc);
        return "KYC uploaded successfully - PENDING approval";
    }
}