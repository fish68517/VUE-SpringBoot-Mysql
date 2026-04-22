package com.powerinspection.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.powerinspection.common.ApiResponse;
import com.powerinspection.entity.User;
import com.powerinspection.entity.UserStatus;
import com.powerinspection.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Let browser preflight requests pass directly, otherwise CORS will fail before real API calls.
        if (CorsUtils.isPreFlightRequest(request) || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token == null || token.isBlank()) {
            writeUnauthorized(response, "未登录");
            return false;
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            Long userId = jwtService.parseUserId(token);
            User user = userRepository.findById(userId).orElse(null);
            if (user == null || user.getStatus() == UserStatus.DISABLED) {
                writeUnauthorized(response, "账号不可用");
                return false;
            }
            AuthContext.setUser(user);
            return true;
        } catch (Exception ex) {
            writeUnauthorized(response, "登录已失效");
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuthContext.clear();
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.fail(message)));
    }
}
