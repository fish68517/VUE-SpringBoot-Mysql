package com.travelMemory.service;

import com.travelMemory.dto.AuthResponse;
import com.travelMemory.dto.LoginRequest;
import com.travelMemory.dto.RegisterRequest;
import com.travelMemory.dto.UserResponse;
import com.travelMemory.entity.User;
import com.travelMemory.entity.UserRole;
import com.travelMemory.repository.UserRepository;
import com.travelMemory.security.JwtTokenProvider;
import com.travelMemory.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthResponse register(RegisterRequest registerRequest) {
        String email = SecurityUtils.sanitizeEmail(registerRequest.getEmail());
        String username = SecurityUtils.sanitizeUsername(registerRequest.getUsername());

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (!isValidPassword(registerRequest.getPassword())) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordEncoder.encode(registerRequest.getPassword()))
                .role(UserRole.USER)
                .build();

        User savedUser = userRepository.save(user);
        String token = jwtTokenProvider.generateToken(savedUser.getId(), savedUser.getEmail(), savedUser.getRole().name());

        return AuthResponse.of(token, convertToUserResponse(savedUser));
    }

    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public AuthResponse login(LoginRequest loginRequest) {
        String email = SecurityUtils.sanitizeEmail(loginRequest.getEmail());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email or password is incorrect"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Email or password is incorrect");
        }

        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        return AuthResponse.of(token, convertToUserResponse(user));
    }

    public AuthResponse refreshToken(String expiredToken) {
        if (expiredToken == null || expiredToken.isEmpty()) {
            throw new IllegalArgumentException("Token is required");
        }

        try {
            String userId = jwtTokenProvider.getUserIdFromToken(expiredToken);
            String email = jwtTokenProvider.getEmailFromToken(expiredToken);

            if (userId == null || email == null) {
                throw new IllegalArgumentException("Invalid token");
            }

            User user = userRepository.findById(Long.parseLong(userId))
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            if (!user.getEmail().equals(email)) {
                throw new IllegalArgumentException("Token email does not match user email");
            }

            String newToken = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole().name());
            return AuthResponse.of(newToken, convertToUserResponse(user));
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token: " + e.getMessage());
        }
    }

    private UserResponse convertToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .role(user.getRole() != null ? user.getRole().name() : UserRole.USER.name())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
