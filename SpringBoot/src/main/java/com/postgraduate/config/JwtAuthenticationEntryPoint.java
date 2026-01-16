package com.postgraduate.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postgraduate.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Custom AuthenticationEntryPoint for handling authentication errors.
 * Returns a JSON response when authentication fails.
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Handle authentication exception by returning a JSON error response.
     *
     * @param request that resulted in an AuthenticationException
     * @param response so that the user agent can be advised of failure
     * @param authException that caused the invocation
     * @throws IOException if an input or output exception occurs
     * @throws ServletException if a servlet exception occurs
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        log.error("Authentication failed: {}", authException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        final ApiResponse<?> body = ApiResponse.error(401, "Unauthorized: " + authException.getMessage());

        response.getOutputStream().println(objectMapper.writeValueAsString(body));
    }

}
