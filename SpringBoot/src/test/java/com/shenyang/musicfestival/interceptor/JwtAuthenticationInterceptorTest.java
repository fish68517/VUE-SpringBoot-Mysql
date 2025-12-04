package com.shenyang.musicfestival.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shenyang.musicfestival.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test cases for JWT authentication interceptor
 */
@SpringBootTest
@TestPropertySource(properties = {
    "jwt.secret=shenyang-music-festival-secret-key-2024-must-be-at-least-256-bits-long-for-security",
    "jwt.expiration=86400000",
    "jwt.refresh-expiration=604800000"
})
class JwtAuthenticationInterceptorTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private JwtAuthenticationInterceptor interceptor;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        interceptor = new JwtAuthenticationInterceptor(jwtUtil, objectMapper);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }

    @Test
    void testPreHandleWithValidToken() throws Exception {
        Long userId = 1L;
        String role = "user";
        String token = jwtUtil.generateToken(userId, role);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        boolean result = interceptor.preHandle(request, response, new Object());

        assertTrue(result);
        verify(request).setAttribute("userId", userId);
        verify(request).setAttribute("role", role);
    }

    @Test
    void testPreHandleWithoutAuthorizationHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        boolean result = interceptor.preHandle(request, response, new Object());

        assertFalse(result);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void testPreHandleWithInvalidTokenFormat() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("InvalidFormat");

        boolean result = interceptor.preHandle(request, response, new Object());

        assertFalse(result);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void testPreHandleWithInvalidToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid.token.here");

        boolean result = interceptor.preHandle(request, response, new Object());

        assertFalse(result);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void testPreHandleWithAdminRole() throws Exception {
        Long userId = 2L;
        String role = "admin";
        String token = jwtUtil.generateToken(userId, role);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        boolean result = interceptor.preHandle(request, response, new Object());

        assertTrue(result);
        verify(request).setAttribute("userId", userId);
        verify(request).setAttribute("role", role);
    }

}
