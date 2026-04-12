package com.travelMemory.service;

import com.travelMemory.dto.AuthResponse;
import com.travelMemory.dto.LoginRequest;
import com.travelMemory.dto.RegisterRequest;
import com.travelMemory.dto.UserResponse;
import com.travelMemory.entity.User;
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

    /**
     * Register a new user
     * @param registerRequest the registration request containing username, email, and password
     * @return AuthResponse containing JWT token and user information
     * @throws IllegalArgumentException if email or username already exists
     */
    public AuthResponse register(RegisterRequest registerRequest) {
        // Sanitize and validate email
        String email = SecurityUtils.sanitizeEmail(registerRequest.getEmail());

        // Sanitize and validate username
        String username = SecurityUtils.sanitizeUsername(registerRequest.getUsername());

        // Check if email already exists (using parameterized query via JPA)
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }

        // Check if username already exists (using parameterized query via JPA)
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already taken");
        }

        // Validate password strength
        if (!isValidPassword(registerRequest.getPassword())) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }

        // Create new user
        User user = User.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordEncoder.encode(registerRequest.getPassword()))
                .build();

        // Save user to database (using parameterized queries via JPA)
        User savedUser = userRepository.save(user);

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(savedUser.getId(), savedUser.getEmail());

        // Convert to response DTO
        UserResponse userResponse = convertToUserResponse(savedUser);

        return AuthResponse.of(token, userResponse);
    }

    /**
     * Validate email format
     * @param email the email to validate
     * @return true if email format is valid
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    /**
     * Validate password strength
     * @param password the password to validate
     * @return true if password meets minimum requirements
     */
    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    /**
     * Login user with email and password
     * @param loginRequest the login request containing email and password
     * @return AuthResponse containing JWT token and user information
     * @throws IllegalArgumentException if email not found or password is incorrect
     */
    public AuthResponse login(LoginRequest loginRequest) {
        // Sanitize and validate email
        String email = SecurityUtils.sanitizeEmail(loginRequest.getEmail());

        // Find user by email (using parameterized query via JPA)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email or password is incorrect"));

        // Verify password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Email or password is incorrect");
        }

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail());

        // Convert to response DTO
        UserResponse userResponse = convertToUserResponse(user);

        return AuthResponse.of(token, userResponse);
    }

    /**
     * Refresh JWT token
     * @param expiredToken the expired JWT token
     * @return AuthResponse containing new JWT token and user information
     * @throws IllegalArgumentException if token is invalid or user not found
     */
    public AuthResponse refreshToken(String expiredToken) {
        // Validate token format (even if expired)
        if (expiredToken == null || expiredToken.isEmpty()) {
            throw new IllegalArgumentException("Token is required");
        }

        try {
            // Extract user ID and email from expired token
            String userId = jwtTokenProvider.getUserIdFromToken(expiredToken);
            String email = jwtTokenProvider.getEmailFromToken(expiredToken);

            if (userId == null || email == null) {
                throw new IllegalArgumentException("Invalid token");
            }

            // Find user by ID
            User user = userRepository.findById(Long.parseLong(userId))
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            // Verify email matches
            if (!user.getEmail().equals(email)) {
                throw new IllegalArgumentException("Token email does not match user email");
            }

            // Generate new JWT token
            String newToken = jwtTokenProvider.generateToken(user.getId().toString(), user.getEmail());

            // Convert to response DTO
            UserResponse userResponse = convertToUserResponse(user);

            return AuthResponse.of(newToken, userResponse);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token: " + e.getMessage());
        }
    }

    /**
     * Convert User entity to UserResponse DTO
     * @param user the user entity
     * @return UserResponse DTO
     */
    private UserResponse convertToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
