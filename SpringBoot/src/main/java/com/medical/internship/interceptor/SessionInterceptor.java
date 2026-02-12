package com.medical.internship.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.internship.common.SessionContext;
import com.medical.internship.dto.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 会话拦截器 - 验证用户身份和组织隔离
 */
@Slf4j
@Component
public class SessionInterceptor implements HandlerInterceptor {
    
    private static final String USER_SESSION_KEY = "currentUser";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求路径
        String requestPath = request.getRequestURI();
        
        // 放行登录、注册和健康检查接口
        if (requestPath.contains("/api/users/login") || 
            requestPath.contains("/api/users/register") ||
            requestPath.contains("/health")) {
            return true;
        }
        
        // 获取会话
        HttpSession session = request.getSession(false);
        if (session == null) {
            log.warn("请求缺少有效的会话: {}", requestPath);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未登录或会话已过期\"}");
            return false;
        }
        
        // 从会话中获取用户信息
        UserResponse user = (UserResponse) session.getAttribute(USER_SESSION_KEY);
        if (user == null) {
            log.warn("会话中不存在用户信息: {}", requestPath);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未登录或会话已过期\"}");
            return false;
        }
        
        // 将用户信息存储到ThreadLocal中
        SessionContext.setCurrentUser(user);
        
        log.debug("用户 {} 访问: {}", user.getUsername(), requestPath);
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清除ThreadLocal中的用户信息
        SessionContext.clear();
    }
    
    /**
     * 将用户信息存储到会话中
     */
    public static void storeUserInSession(HttpSession session, UserResponse user) {
        session.setAttribute(USER_SESSION_KEY, user);
    }
    
    /**
     * 从会话中获取用户信息
     */
    public static UserResponse getUserFromSession(HttpSession session) {
        return (UserResponse) session.getAttribute(USER_SESSION_KEY);
    }
    
    /**
     * 清除会话中的用户信息
     */
    public static void clearUserFromSession(HttpSession session) {
        session.removeAttribute(USER_SESSION_KEY);
    }
}
