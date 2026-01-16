package com.postgraduate.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postgraduate.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Custom AccessDeniedHandler for handling authorization errors.
 * Returns a JSON response when user lacks required permissions.
 */
@Slf4j
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Handle access denied exception by returning a JSON error response.
     *
     * @param request that resulted in an AccessDeniedException
     * @param response so that the user agent can be advised of failure
     * @param accessDeniedException that caused the invocation
     * @throws IOException if an input or output exception occurs
     * @throws ServletException if a servlet exception occurs
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.error("Access denied: {}", accessDeniedException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        final ApiResponse<?> body = ApiResponse.error(403, "Forbidden: " + accessDeniedException.getMessage());

        response.getOutputStream().println(objectMapper.writeValueAsString(body));
    }

}
