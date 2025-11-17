package com.sharkfitness.config;

import com.sharkfitness.entity.User;
import com.sharkfitness.exception.UnauthorizedException;
import com.sharkfitness.repository.UserRepository;
import com.sharkfitness.util.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Authentication interceptor to validate tokens and extract current user
 * Intercepts requests to protected endpoints
 */
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Allow OPTIONS requests for CORS preflight
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header");
            throw new UnauthorizedException("Missing or invalid token");
        }

        String token = authHeader.substring(7); // Remove "Bearer " prefix
        
        try {
            Long userId = TokenUtil.extractUserId(token);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UnauthorizedException("Invalid token: user not found"));
            
            // Store user in request attribute for controller access
            request.setAttribute("currentUser", user);
            request.setAttribute("currentUserId", userId);
            
            log.debug("Authenticated user: {} ({})", user.getUsername(), user.getRole());
            return true;
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid token format: {}", e.getMessage());
            throw new UnauthorizedException("Invalid token format");
        }
    }
}
