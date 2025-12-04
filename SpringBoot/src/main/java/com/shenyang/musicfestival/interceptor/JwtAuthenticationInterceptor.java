package com.shenyang.musicfestival.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shenyang.musicfestival.util.ApiResponse;
import com.shenyang.musicfestival.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * JWT Authentication Interceptor
 * Validates JWT token in request headers and handles authentication failures
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Extract token from Authorization header
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            sendUnauthorizedResponse(response, "缺少认证令牌");
            return false;
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        // Validate token
        if (!jwtUtil.validateToken(token)) {
            sendUnauthorizedResponse(response, "认证令牌无效或已过期");
            return false;
        }

        // Extract user ID and role from token and store in request attributes
        try {
            Long userId = jwtUtil.extractUserId(token);
            String role = jwtUtil.extractRole(token);
            
            request.setAttribute("userId", userId);
            request.setAttribute("role", role);
            
            log.debug("User {} authenticated successfully with role {}", userId, role);
            return true;
        } catch (Exception e) {
            log.error("Failed to extract user information from token", e);
            sendUnauthorizedResponse(response, "无法解析认证令牌");
            return false;
        }
    }

    /**
     * Send unauthorized response
     */
    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        ApiResponse<Object> errorResponse = ApiResponse.error(401, message);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

}
