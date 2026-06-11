package org.example.project.controller;

import org.example.project.dto.AuthRequest;
import org.example.project.dto.AuthResponse;
import org.example.project.dto.RefreshTokenRequest;
import org.example.project.entity.User;
import org.example.project.service.AccountService;
import org.example.project.service.UserService;
import org.example.project.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, UserService userService,
                          AccountService accountService, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        if (userService.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getUsername() + "@rikkei.bank");
        user.setFullName("User " + request.getUsername());

        User savedUser = userService.save(user);
        accountService.createAccountForUser(savedUser);

        return ResponseEntity.ok("User registered successfully with account");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        String accessToken = jwtService.generateAccessToken(request.getUsername());
        String refreshToken = jwtService.generateRefreshToken(request.getUsername());
        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken, "Login successful"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshTokenRequest request) {
        String username = jwtService.extractUsername(request.getRefreshToken());
        if (jwtService.isTokenValid(request.getRefreshToken(), username)) {
            String newAccessToken = jwtService.generateAccessToken(username);
            return ResponseEntity.ok(new AuthResponse(newAccessToken, request.getRefreshToken(), "Token refreshed successfully"));
        }
        return ResponseEntity.status(401).body(new AuthResponse(null, null, "Invalid refresh token"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String header) {
        if (header != null && header.startsWith("Bearer ")) {
            jwtService.blacklistToken(header.substring(7));
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logged out successfully");
    }
}