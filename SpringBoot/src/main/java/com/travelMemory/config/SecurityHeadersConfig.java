package com.travelMemory.config;

import com.travelMemory.security.InputValidationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Configuration for security headers.
 * Adds HTTP security headers to all responses to prevent various attacks.
 */
@Configuration
public class SecurityHeadersConfig implements WebMvcConfigurer {

    @Autowired
    private InputValidationInterceptor inputValidationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Add input validation interceptor first to catch suspicious requests early
        registry.addInterceptor(inputValidationInterceptor).addPathPatterns("/**");
        
        // Add security headers interceptor
        registry.addInterceptor(new SecurityHeadersInterceptor()).addPathPatterns("/**");
    }

    /**
     * Interceptor that adds security headers to HTTP responses.
     */
    public static class SecurityHeadersInterceptor implements HandlerInterceptor {

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            // Prevent clickjacking attacks
            response.setHeader("X-Frame-Options", "DENY");

            // Prevent MIME type sniffing
            response.setHeader("X-Content-Type-Options", "nosniff");

            // Enable XSS protection in older browsers
            response.setHeader("X-XSS-Protection", "1; mode=block");

            // Content Security Policy - restricts resource loading
            response.setHeader("Content-Security-Policy", 
                    "default-src 'self'; " +
                    "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                    "style-src 'self' 'unsafe-inline'; " +
                    "img-src 'self' data: https:; " +
                    "font-src 'self'; " +
                    "connect-src 'self' https:; " +
                    "frame-ancestors 'none'; " +
                    "base-uri 'self'; " +
                    "form-action 'self'");

            // Referrer Policy - controls referrer information
            response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

            // Feature Policy / Permissions Policy
            response.setHeader("Permissions-Policy", 
                    "geolocation=(), " +
                    "microphone=(), " +
                    "camera=(), " +
                    "payment=(), " +
                    "usb=(), " +
                    "magnetometer=(), " +
                    "gyroscope=(), " +
                    "accelerometer=()");

            // Strict Transport Security (HSTS) - enforce HTTPS
            response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload");

            return true;
        }
    }
}
