package com.campus.service;

import com.campus.dto.LoginRequest;
import com.campus.dto.LoginResponse;
import com.campus.dto.RegisterRequest;
import com.campus.entity.User;
import com.campus.exception.BusinessException;
import com.campus.repository.UserRepository;
import com.campus.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private String testToken;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("hashedPassword")
                .role(User.UserRole.USER)
                .status(User.AccountStatus.ACTIVE)
                .build();
        testUser.setId(1L);

        testToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlciIsInJvbGUiOiJVU0VSIn0.test";

        // Set JWT expiration
        ReflectionTestUtils.setField(authService, "jwtExpiration", 86400000L);
    }

    @Test
    void testLogoutWithValidToken() {
        // Arrange
        when(jwtUtil.validateToken(testToken)).thenReturn(true);

        // Act
        authService.logout(testToken);

        // Assert
        verify(tokenBlacklistService, times(1)).blacklistToken(testToken);
    }

    @Test
    void testLogoutWithBearerToken() {
        // Arrange
        String bearerToken = "Bearer " + testToken;
        when(jwtUtil.validateToken(testToken)).thenReturn(true);

        // Act
        authService.logout(bearerToken);

        // Assert
        verify(tokenBlacklistService, times(1)).blacklistToken(testToken);
    }

    @Test
    void testLogoutWithInvalidToken() {
        // Arrange
        when(jwtUtil.validateToken(testToken)).thenReturn(false);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.logout(testToken);
        });

        assertEquals(401, exception.getCode());
        assertEquals("Invalid token", exception.getMessage());
        verify(tokenBlacklistService, never()).blacklistToken(anyString());
    }

    @Test
    void testRefreshTokenWithValidToken() {
        // Arrange
        String newToken = "newGeneratedToken";
        when(jwtUtil.validateToken(testToken)).thenReturn(true);
        when(jwtUtil.extractUsername(testToken)).thenReturn("testuser");
        when(jwtUtil.extractRole(testToken)).thenReturn("USER");
        when(jwtUtil.generateToken("testuser", "USER")).thenReturn(newToken);

        // Act
        String result = authService.refreshToken(testToken);

        // Assert
        assertEquals(newToken, result);
        verify(jwtUtil, times(1)).generateToken("testuser", "USER");
    }

    @Test
    void testRefreshTokenWithBearerToken() {
        // Arrange
        String bearerToken = "Bearer " + testToken;
        String newToken = "newGeneratedToken";
        when(jwtUtil.validateToken(testToken)).thenReturn(true);
        when(jwtUtil.extractUsername(testToken)).thenReturn("testuser");
        when(jwtUtil.extractRole(testToken)).thenReturn("USER");
        when(jwtUtil.generateToken("testuser", "USER")).thenReturn(newToken);

        // Act
        String result = authService.refreshToken(bearerToken);

        // Assert
        assertEquals(newToken, result);
        verify(jwtUtil, times(1)).generateToken("testuser", "USER");
    }

    @Test
    void testRefreshTokenWithInvalidToken() {
        // Arrange
        when(jwtUtil.validateToken(testToken)).thenReturn(false);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.refreshToken(testToken);
        });

        assertEquals(401, exception.getCode());
        assertEquals("Invalid token", exception.getMessage());
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    void testRegisterWithValidData() {
        // Arrange
        RegisterRequest request = RegisterRequest.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password("Password123")
                .confirmPassword("Password123")
                .build();

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = authService.register(request);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterWithPasswordMismatch() {
        // Arrange
        RegisterRequest request = RegisterRequest.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password("Password123")
                .confirmPassword("Password456")
                .build();

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.register(request);
        });

        assertEquals(400, exception.getCode());
        assertEquals("Passwords do not match", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterWithDuplicateUsername() {
        // Arrange
        RegisterRequest request = RegisterRequest.builder()
                .username("existinguser")
                .email("newuser@example.com")
                .password("Password123")
                .confirmPassword("Password123")
                .build();

        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.register(request);
        });

        assertEquals(409, exception.getCode());
        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterWithDuplicateEmail() {
        // Arrange
        RegisterRequest request = RegisterRequest.builder()
                .username("newuser")
                .email("existing@example.com")
                .password("Password123")
                .confirmPassword("Password123")
                .build();

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.register(request);
        });

        assertEquals(409, exception.getCode());
        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

}
