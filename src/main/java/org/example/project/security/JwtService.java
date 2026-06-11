package org.example.project.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.example.project.entity.TokenBlacklist;
import org.example.project.repository.TokenBlacklistRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;

@Service
public class JwtService {

    private final TokenBlacklistRepository tokenBlacklistRepository;

    @Value("${jwt.secret:rikkeiBankSuperSecretKey2026MustBeAtLeast256BitsLongForHS256}")
    private String secret;

    @Value("${jwt.access.expiration:300000}")
    private long accessExpiration;

    @Value("${jwt.refresh.expiration:86400000}")
    private long refreshExpiration;

    public JwtService(TokenBlacklistRepository tokenBlacklistRepository) {
        this.tokenBlacklistRepository = tokenBlacklistRepository;
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Tạo Access Token
    public String generateAccessToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    // Tạo Refresh Token
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean isTokenValid(String token, String username) {
        try {
            String extractedUsername = extractUsername(token);
            return extractedUsername.equals(username) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
        return expiration.before(new Date());
    }

    // === PHƯƠNG THỨC ĐÃ THÊM ĐỂ SỬA LỖI blacklistToken ===
    public void blacklistToken(String token) {
        TokenBlacklist blacklist = TokenBlacklist.builder()
                .token(token)
                .expiryDate(LocalDateTime.now().plusMinutes(5)) // theo thời hạn Access Token
                .build();
        tokenBlacklistRepository.save(blacklist);
        System.out.println("[AUDIT] Token đã được đưa vào blacklist: " + token.substring(0, 20) + "...");
    }

    // Phương thức kiểm tra blacklist
    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklistRepository.findByToken(token).isPresent();
    }
}