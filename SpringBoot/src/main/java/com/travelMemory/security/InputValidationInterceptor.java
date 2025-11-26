package com.travelMemory.security;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Interceptor for validating and sanitizing incoming requests.
 * Prevents common security attacks like SQL injection and XSS.
 */
@Component
public class InputValidationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Validate request method
        String method = request.getMethod();
        if (!isValidHttpMethod(method)) {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return false;
        }

        // Validate request path
        String requestPath = request.getRequestURI();
        if (containsSuspiciousPatterns(requestPath)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }

        // Validate query parameters
        String queryString = request.getQueryString();
        if (queryString != null && containsSuspiciousPatterns(queryString)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }

        return true;
    }

    /**
     * Check if HTTP method is valid.
     *
     * @param method the HTTP method
     * @return true if method is valid
     */
    private boolean isValidHttpMethod(String method) {
        return method != null && (
            method.equals("GET") ||
            method.equals("POST") ||
            method.equals("PUT") ||
            method.equals("DELETE") ||
            method.equals("PATCH") ||
            method.equals("OPTIONS") ||
            method.equals("HEAD")
        );
    }

    /**
     * Check if input contains suspicious patterns that might indicate attacks.
     *
     * @param input the input to check
     * @return true if suspicious patterns are detected
     */
    private boolean containsSuspiciousPatterns(String input) {
        if (input == null) {
            return false;
        }

        String lowerInput = input.toLowerCase();

        // Check for directory traversal attempts
        if (lowerInput.contains("../") || lowerInput.contains("..\\")) {
            return true;
        }

        // Check for null byte injection
        if (input.contains("\0")) {
            return true;
        }

        // Check for common SQL injection patterns
        String[] sqlPatterns = {
            "union select",
            "union all select",
            "select from",
            "insert into",
            "update set",
            "delete from",
            "drop table",
            "exec(",
            "execute(",
            "script>",
            "javascript:",
            "onerror=",
            "onload="
        };

        for (String pattern : sqlPatterns) {
            if (lowerInput.contains(pattern)) {
                return true;
            }
        }

        return false;
    }
}
