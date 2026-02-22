package com.agricultural.security;

import com.agricultural.util.LoggerUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * 权限切面
 * 用于在方法执行前检查用户权限
 * 支持基于角色的访问控制（RBAC）
 * 
 * @author Agricultural Platform Team
 * @version 1.0.0
 */
@Slf4j
@Aspect
@Component
public class PermissionAspect {
    
    /**
     * 权限检查前置通知
     * 在标注了@RequirePermission的方法执行前进行权限检查
     * 
     * @param joinPoint 连接点
     * @param permission 权限注解
     * @throws Exception 异常
     */
    @Before("@annotation(permission)")
    public void checkPermission(JoinPoint joinPoint, RequirePermission permission) throws Exception {
        // 获取HTTP请求
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            LoggerUtil.warn("无法获取HTTP请求属性");
            throw new SecurityException("无法获取请求信息");
        }
        
        HttpServletRequest request = attributes.getRequest();
        
        // 获取用户信息
        String userType = (String) request.getAttribute("userType");
        Long userId = (Long) request.getAttribute("userId");
        String username = (String) request.getAttribute("username");
        
        // 检查是否需要认证
        if (permission.requireAuth()) {
            if (userType == null || userId == null) {
                LoggerUtil.warn("用户未认证，尝试访问方法: {}", joinPoint.getSignature().getName());
                throw new SecurityException("用户未认证");
            }
        }
        
        // 检查角色权限
        String[] allowedRoles = permission.roles();
        if (allowedRoles != null && allowedRoles.length > 0) {
            boolean hasPermission = Arrays.asList(allowedRoles).contains(userType);
            
            if (!hasPermission) {
                LoggerUtil.warn("用户权限不足，用户: {}, 用户类型: {}, 方法: {}, 所需角色: {}", 
                        username, userType, joinPoint.getSignature().getName(), Arrays.toString(allowedRoles));
                throw new SecurityException("权限不足，无法访问此资源");
            }
        }
        
        LoggerUtil.debug("权限检查通过，用户: {}, 用户类型: {}, 方法: {}", 
                username, userType, joinPoint.getSignature().getName());
    }
    
    /**
     * 自定义安全异常
     */
    public static class SecurityException extends Exception {
        public SecurityException(String message) {
            super(message);
        }
    }
}
