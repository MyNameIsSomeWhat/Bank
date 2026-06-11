package org.example.project.controller;

import org.example.project.dto.KycDto;
import org.example.project.service.KycService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/kyc")
public class KycController {

    private final KycService kycService;

    public KycController(KycService kycService) {
        this.kycService = kycService;
    }

    // FR-04: Đăng ký mở tài khoản + upload eKYC
    @PostMapping("/upload")
    public ResponseEntity<KycDto> uploadKyc(@RequestParam MultipartFile file, @RequestParam Long userId) {
        return ResponseEntity.ok(kycService.uploadKyc(file, userId));
    }

    // FR-09: Phê duyệt eKYC (Staff)
    @PutMapping("/approve/{kycId}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<String> approveKyc(@PathVariable Long kycId) {
        kycService.approveKyc(kycId);
        return ResponseEntity.ok("KYC approved");
    }
}