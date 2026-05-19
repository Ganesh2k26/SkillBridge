package com.ganesh.skillbridge.service;

import com.ganesh.skillbridge.config.JwtService;
import com.ganesh.skillbridge.dto.*;
import com.ganesh.skillbridge.entity.User;
import com.ganesh.skillbridge.exception.ResourceNotFoundException;
import com.ganesh.skillbridge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * AuthService handles register/login only.
 * Does NOT implement UserDetailsService — that's UserDetailsServiceImpl.
 * This breaks the circular dependency completely.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest req) {
        if (userRepo.existsByEmail(req.getEmail()))
            throw new IllegalArgumentException("Email already registered");

        User user = User.builder()
            .name(req.getName())
            .email(req.getEmail())
            .password(encoder.encode(req.getPassword()))
            .collegeName(req.getCollegeName())
            .graduationYear(req.getGraduationYear())
            .targetCompany(req.getTargetCompany())
            .role("STUDENT")
            .build();

        userRepo.save(user);
        String token = jwtService.generateToken(user.getEmail());

        return AuthResponse.builder()
            .token(token)
            .name(user.getName())
            .email(user.getEmail())
            .role(user.getRole())
            .targetCompany(user.getTargetCompany())
            .message("Registration successful! Welcome to SkillBridge 🎉")
            .build();
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepo.findByEmail(req.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        user.setLastLogin(LocalDateTime.now());
        userRepo.save(user);

        String token = jwtService.generateToken(user.getEmail());
        return AuthResponse.builder()
            .token(token)
            .name(user.getName())
            .email(user.getEmail())
            .role(user.getRole())
            .targetCompany(user.getTargetCompany())
            .message("Login successful!")
            .build();
    }

    public User getByEmail(String email) {
        return userRepo.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
