package com.agricultural.security;

import com.agricultural.util.LoggerUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

/**
 * 权限拦截器
 * 用于在请求处理前检查用户是否具有访问权限
 * 支持基于角色的访问控制（RBAC）
 * 
 * @author Agricultural Platform Team
 * @version 1.0.0
 */
@Slf4j
@Component
public class PermissionInterceptor implements HandlerInterceptor {
    
    /**
     * 请求前处理
     * 检查用户权限
     * 
     * @param request HTTP请求
     * @param response HTTP响应
     * @param handler 处理器
     * @return 是否继续处理
     * @throws Exception 异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 检查是否是HandlerMethod
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        
        // 检查方法上是否有RequirePermission注解
        RequirePermission methodPermission = handlerMethod.getMethodAnnotation(RequirePermission.class);
        
        // 检查类上是否有RequirePermission注解
        RequirePermission classPermission = handlerMethod.getBeanType().getAnnotation(RequirePermission.class);
        
        // 优先使用方法上的注解，如果没有则使用类上的注解
        RequirePermission permission = methodPermission != null ? methodPermission : classPermission;
        
        // 如果没有权限注解，则允许访问
        if (permission == null) {
            return true;
        }
        
        // 获取用户信息
        String userType = (String) request.getAttribute("userType");
        Long userId = (Long) request.getAttribute("userId");
        String username = (String) request.getAttribute("username");
        
        // 检查是否需要认证
        if (permission.requireAuth()) {
            if (userType == null || userId == null) {
                LoggerUtil.warn("用户未认证，访问受保护资源: {}", request.getRequestURI());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"code\": 401, \"message\": \"用户未认证\"}");
                return false;
            }
        }
        
        // 检查角色权限
        String[] allowedRoles = permission.roles();
        if (allowedRoles != null && allowedRoles.length > 0) {
            boolean hasPermission = Arrays.asList(allowedRoles).contains(userType);
            
            if (!hasPermission) {
                LoggerUtil.warn("用户权限不足，用户: {}, 用户类型: {}, 访问资源: {}, 所需角色: {}", 
                        username, userType, request.getRequestURI(), Arrays.toString(allowedRoles));
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("{\"code\": 403, \"message\": \"权限不足\"}");
                return false;
            }
        }
        
        LoggerUtil.debug("权限检查通过，用户: {}, 用户类型: {}, 访问资源: {}", 
                username, userType, request.getRequestURI());
        
        return true;
    }
}
