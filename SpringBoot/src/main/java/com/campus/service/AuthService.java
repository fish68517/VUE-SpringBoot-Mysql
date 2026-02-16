package com.campus.service;

import com.campus.dto.LoginRequest;
import com.campus.dto.LoginResponse;
import com.campus.dto.RegisterRequest;
import com.campus.dto.TokenVerifyResponse;
import com.campus.entity.User;
import com.campus.exception.BusinessException;
import com.campus.repository.UserRepository;
import com.campus.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Date;

/**
 * Authentication Service
 * Handles user registration, login, and token verification
 */
@Slf4j
@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Register a new user
     * 
     * @param request Registration request containing username, email, password
     * @return Registered user
     * @throws BusinessException if username or email already exists, or passwords don't match
     */
    public User register(RegisterRequest request) {
        log.info("Registering new user with username: {}", request.getUsername());

        // Validate passwords match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            log.warn("Password mismatch for registration attempt with username: {}", request.getUsername());
            throw new BusinessException(400, "Passwords do not match");
        }

        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Username already exists: {}", request.getUsername());
            throw new BusinessException(409, "Username already exists");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Email already exists: {}", request.getEmail());
            throw new BusinessException(409, "Email already exists");
        }

        // Create new user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.UserRole.USER)
                .status(User.AccountStatus.ACTIVE)
                .build();

        user = userRepository.save(user);
        log.info("User registered successfully with username: {}", request.getUsername());

        return user;
    }

    /**
     * Login user and generate JWT token
     * 
     * @param request Login request containing username and password
     * @return Login response with JWT token and user info
     * @throws BusinessException if username not found or password is incorrect
     */
    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for username: {}", request.getUsername());

        // Find user by username
        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());
        if (userOptional.isEmpty()) {
            log.warn("Login failed: username not found: {}", request.getUsername());
            throw new BusinessException(401, "Invalid username or password");
        }

        User user = userOptional.get();

        // Check if account is active
        if (user.getStatus() == User.AccountStatus.DISABLED) {
            log.warn("Login failed: account disabled for username: {}", request.getUsername());
            throw new BusinessException(403, "Account has been disabled");
        }

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Login failed: incorrect password for username: {}", request.getUsername());
            throw new BusinessException(401, "Invalid username or password");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().toString(), user.getId());
        log.info("User logged in successfully: {}", request.getUsername());

        return LoginResponse.of(token, jwtExpiration, user);
    }

    /**
     * Verify JWT token validity
     * 
     * @param token JWT token to verify
     * @return Token verification response with validity status and extracted claims
     */
    public TokenVerifyResponse verifyToken(String token) {
        log.debug("Verifying token");

        // Remove "Bearer " prefix if present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // Validate token
        if (!jwtUtil.validateToken(token)) {
            log.warn("Token validation failed");
            return TokenVerifyResponse.of(false, null, null);
        }

        // Extract username and role from token
        String username = jwtUtil.extractUsername(token);
        String role = jwtUtil.extractRole(token);

        log.debug("Token verified successfully for username: {}", username);
        return TokenVerifyResponse.of(true, username, role);
    }

    /**
     * Refresh JWT token
     * 
     * @param token Current JWT token
     * @return New JWT token
     * @throws BusinessException if token is invalid
     */
    public String refreshToken(String token) {
        log.debug("Refreshing token");

        // Remove "Bearer " prefix if present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // Validate token
        if (!jwtUtil.validateToken(token)) {
            log.warn("Token refresh failed: invalid token");
            throw new BusinessException(401, "Invalid token");
        }

        // Extract username and role
        String username = jwtUtil.extractUsername(token);
        String role = jwtUtil.extractRole(token);
        Long userId = jwtUtil.extractUserId(token);

        // Generate new token
        String newToken = jwtUtil.generateToken(username, role, userId);
        log.debug("Token refreshed successfully for username: {}", username);

        return newToken;
    }

    /**
     * Logout user by blacklisting the token
     * 
     * @param token JWT token to blacklist
     * @throws BusinessException if token is invalid
     */
    public void logout(String token) {
        log.info("Logout request received");

        // Remove "Bearer " prefix if present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // Validate token before blacklisting
        if (!jwtUtil.validateToken(token)) {
            log.warn("Logout failed: invalid token");
            throw new BusinessException(401, "Invalid token");
        }

        // Add token to blacklist
        tokenBlacklistService.blacklistToken(token);
        log.info("User logged out successfully, token blacklisted");
    }

}