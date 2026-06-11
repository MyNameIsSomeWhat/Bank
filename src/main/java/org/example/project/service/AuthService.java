package org.example.project.service;

import org.example.project.dto.AuthRequest;
import org.example.project.dto.AuthResponse;
import org.example.project.entity.User;
import org.example.project.repository.UserRepository;
import org.example.project.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(AuthRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getUsername()); // tạm thời
        user.setRole("CUSTOMER");
        user.setKyc(false);

        userRepository.save(user);

        String access = jwtService.generateAccessToken(user.getUsername());
        String refresh = jwtService.generateRefreshToken(user.getUsername());

        return new AuthResponse(access, refresh, "Đăng ký thành công");
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        String access = jwtService.generateAccessToken(request.getUsername());
        String refresh = jwtService.generateRefreshToken(request.getUsername());

        return new AuthResponse(access, refresh, "Đăng nhập thành công");
    }

    public void logout(String token) {
        if (token != null) {
            jwtService.blacklistToken(token);
        }
    }
}