package com.shenyang.musicfestival.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for JWT utility
 */
@SpringBootTest
@TestPropertySource(properties = {
    "jwt.secret=shenyang-music-festival-secret-key-2024-must-be-at-least-256-bits-long-for-security",
    "jwt.expiration=86400000",
    "jwt.refresh-expiration=604800000"
})
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    private Long testUserId;
    private String testRole;

    @BeforeEach
    void setUp() {
        testUserId = 1L;
        testRole = "user";
    }

    @Test
    void testGenerateToken() {
        String token = jwtUtil.generateToken(testUserId, testRole);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testValidateToken() {
        String token = jwtUtil.generateToken(testUserId, testRole);
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    void testValidateInvalidToken() {
        String invalidToken = "invalid.token.here";
        assertFalse(jwtUtil.validateToken(invalidToken));
    }

    @Test
    void testExtractUserId() {
        String token = jwtUtil.generateToken(testUserId, testRole);
        Long extractedUserId = jwtUtil.extractUserId(token);
        assertEquals(testUserId, extractedUserId);
    }

    @Test
    void testExtractRole() {
        String token = jwtUtil.generateToken(testUserId, testRole);
        String extractedRole = jwtUtil.extractRole(token);
        assertEquals(testRole, extractedRole);
    }

    @Test
    void testGenerateRefreshToken() {
        String refreshToken = jwtUtil.generateRefreshToken(testUserId);
        assertNotNull(refreshToken);
        assertTrue(jwtUtil.validateToken(refreshToken));
    }

    @Test
    void testTokenWithAdminRole() {
        String adminRole = "admin";
        String token = jwtUtil.generateToken(testUserId, adminRole);
        
        assertTrue(jwtUtil.validateToken(token));
        assertEquals(testUserId, jwtUtil.extractUserId(token));
        assertEquals(adminRole, jwtUtil.extractRole(token));
    }

}
