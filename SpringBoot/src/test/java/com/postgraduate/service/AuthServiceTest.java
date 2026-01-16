package com.postgraduate.service;

import com.postgraduate.config.JwtTokenProvider;
import com.postgraduate.dto.LoginRequest;
import com.postgraduate.dto.LoginResponse;
import com.postgraduate.dto.RegisterRequest;
import com.postgraduate.dto.UserDTO;
import com.postgraduate.entity.User;
import com.postgraduate.exception.ValidationException;
import com.postgraduate.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test class for AuthService.
 * Tests user registration and login functionality with real database.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        // Clear any existing test data
        userRepository.deleteAll();

        registerRequest = RegisterRequest.builder()
                .username("testuser")
                .password("password123")
                .build();

        loginRequest = LoginRequest.builder()
                .username("testuser")
                .password("password123")
                .build();
    }

    @Test
    void testRegisterSuccess() {
        UserDTO userDTO = authService.register(registerRequest);

        assertNotNull(userDTO);
        assertNotNull(userDTO.getId());
        assertEquals("testuser", userDTO.getUsername());
        assertEquals("USER", userDTO.getRole());

        // Verify user was saved to database
        assertTrue(userRepository.existsByUsername("testuser"));
    }

    @Test
    void testRegisterDuplicateUsername() {
        // Register first user
        authService.register(registerRequest);

        // Try to register with same username
        assertThrows(ValidationException.class, () -> {
            authService.register(registerRequest);
        });
    }

    @Test
    void testLoginSuccess() {
        // First register a user
        authService.register(registerRequest);

        // Then login
        LoginResponse loginResponse = authService.login(loginRequest);

        assertNotNull(loginResponse);
        assertNotNull(loginResponse.getUser());
        assertNotNull(loginResponse.getToken());
        assertEquals("testuser", loginResponse.getUser().getUsername());
        assertEquals("USER", loginResponse.getUser().getRole());

        // Verify token is valid
        assertTrue(jwtTokenProvider.validateToken(loginResponse.getToken()));
        assertEquals("testuser", jwtTokenProvider.getUsernameFromToken(loginResponse.getToken()));
    }

    @Test
    void testLoginWithInvalidPassword() {
        // Register a user
        authService.register(registerRequest);

        // Try to login with wrong password
        LoginRequest wrongPasswordRequest = LoginRequest.builder()
                .username("testuser")
                .password("wrongpassword")
                .build();

        assertThrows(ValidationException.class, () -> {
            authService.login(wrongPasswordRequest);
        });
    }

    @Test
    void testLoginWithNonExistentUser() {
        LoginRequest nonExistentRequest = LoginRequest.builder()
                .username("nonexistent")
                .password("password123")
                .build();

        assertThrows(ValidationException.class, () -> {
            authService.login(nonExistentRequest);
        });
    }

    @Test
    void testPasswordIsHashed() {
        authService.register(registerRequest);

        User user = userRepository.findByUsername("testuser").orElseThrow();

        // Verify password is hashed (not plain text)
        assertNotEquals("password123", user.getPasswordHash());
        assertTrue(passwordEncoder.matches("password123", user.getPasswordHash()));
    }

}
