package com.campus.service;

import com.campus.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TokenBlacklistServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    private TokenBlacklistService tokenBlacklistService;

    private String testToken;

    @BeforeEach
    void setUp() {
        tokenBlacklistService = new TokenBlacklistService();
        ReflectionTestUtils.setField(tokenBlacklistService, "jwtUtil", jwtUtil);
        testToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlciIsInJvbGUiOiJVU0VSIn0.test";
    }

    @Test
    void testBlacklistToken() {
        // Act
        tokenBlacklistService.blacklistToken(testToken);

        // Assert
        assertTrue(tokenBlacklistService.isTokenBlacklisted(testToken));
    }

    @Test
    void testBlacklistTokenWithBearer() {
        // Arrange
        String bearerToken = "Bearer " + testToken;

        // Act
        tokenBlacklistService.blacklistToken(bearerToken);

        // Assert
        assertTrue(tokenBlacklistService.isTokenBlacklisted(testToken));
    }

    @Test
    void testIsTokenBlacklistedReturnsFalseForNonBlacklistedToken() {
        // Act & Assert
        assertFalse(tokenBlacklistService.isTokenBlacklisted(testToken));
    }

    @Test
    void testRemoveFromBlacklist() {
        // Arrange
        tokenBlacklistService.blacklistToken(testToken);
        assertTrue(tokenBlacklistService.isTokenBlacklisted(testToken));

        // Act
        tokenBlacklistService.removeFromBlacklist(testToken);

        // Assert
        assertFalse(tokenBlacklistService.isTokenBlacklisted(testToken));
    }

    @Test
    void testClearBlacklist() {
        // Arrange
        tokenBlacklistService.blacklistToken(testToken);
        tokenBlacklistService.blacklistToken("anotherToken");
        assertEquals(2, tokenBlacklistService.getBlacklistSize());

        // Act
        tokenBlacklistService.clearBlacklist();

        // Assert
        assertEquals(0, tokenBlacklistService.getBlacklistSize());
    }

    @Test
    void testGetBlacklistSize() {
        // Arrange
        tokenBlacklistService.blacklistToken(testToken);
        tokenBlacklistService.blacklistToken("token2");
        tokenBlacklistService.blacklistToken("token3");

        // Act
        int size = tokenBlacklistService.getBlacklistSize();

        // Assert
        assertEquals(3, size);
    }

}
