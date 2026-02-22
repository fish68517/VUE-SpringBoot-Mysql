package com.agricultural.util;

import com.agricultural.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * 权限工具类
 * 用于检查用户权限和获取当前用户信息
 * 
 * @author Agricultural Platform Team
 * @version 1.0.0
 */
@Slf4j
public class PermissionUtil {
    
    /**
     * 获取当前用户ID
     * 
     * @return 用户ID，如果未认证则返回null
     */
    public static Long getCurrentUserId() {
        HttpServletRequest request = getHttpServletRequest();
        if (request == null) {
            return null;
        }
        return (Long) request.getAttribute("userId");
    }
    
    /**
     * 获取当前用户名
     * 
     * @return 用户名，如果未认证则返回null
     */
    public static String getCurrentUsername() {
        HttpServletRequest request = getHttpServletRequest();
        if (request == null) {
            return null;
        }
        return (String) request.getAttribute("username");
    }
    
    /**
     * 获取当前用户类型
     * 
     * @return 用户类型，如果未认证则返回null
     */
    public static String getCurrentUserType() {
        HttpServletRequest request = getHttpServletRequest();
        if (request == null) {
            return null;
        }
        return (String) request.getAttribute("userType");
    }
    
    /**
     * 检查当前用户是否具有指定角色
     * 
     * @param roles 角色数组
     * @return 是否具有指定角色
     */
    public static boolean hasRole(String... roles) {
        String userType = getCurrentUserType();
        if (userType == null) {
            LoggerUtil.warn("用户未认证，无法检查角色");
            return false;
        }
        
        boolean hasRole = Arrays.asList(roles).contains(userType);
        LoggerUtil.debug("角色检查: 用户类型={}, 所需角色={}, 结果={}", userType, Arrays.toString(roles), hasRole);
        return hasRole;
    }
    
    /**
     * 检查当前用户是否是管理员
     * 
     * @return 是否是管理员
     */
    public static boolean isAdmin() {
        return hasRole(User.UserType.ADMIN.toString());
    }
    
    /**
     * 检查当前用户是否是商家
     * 
     * @return 是否是商家
     */
    public static boolean isMerchant() {
        return hasRole(User.UserType.MERCHANT.toString());
    }
    
    /**
     * 检查当前用户是否是农户
     * 
     * @return 是否是农户
     */
    public static boolean isFarmer() {
        return hasRole(User.UserType.FARMER.toString());
    }
    
    /**
     * 检查当前用户是否已认证
     * 
     * @return 是否已认证
     */
    public static boolean isAuthenticated() {
        return getCurrentUserId() != null && getCurrentUsername() != null;
    }
    
    /**
     * 检查当前用户是否可以访问指定用户的资源
     * 管理员可以访问所有用户的资源
     * 其他用户只能访问自己的资源
     * 
     * @param targetUserId 目标用户ID
     * @return 是否可以访问
     */
    public static boolean canAccessUser(Long targetUserId) {
        if (!isAuthenticated()) {
            LoggerUtil.warn("用户未认证，无法访问用户资源");
            return false;
        }
        
        Long currentUserId = getCurrentUserId();
        boolean isAdmin = isAdmin();
        
        boolean canAccess = isAdmin || currentUserId.equals(targetUserId);
        LoggerUtil.debug("用户资源访问检查: 当前用户ID={}, 目标用户ID={}, 是否管理员={}, 结果={}", 
                currentUserId, targetUserId, isAdmin, canAccess);
        
        return canAccess;
    }
    
    /**
     * 获取HTTP请求对象
     * 
     * @return HTTP请求对象，如果无法获取则返回null
     */
    private static HttpServletRequest getHttpServletRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return null;
            }
            return attributes.getRequest();
        } catch (Exception e) {
            LoggerUtil.error("获取HTTP请求对象异常: {}", e.getMessage());
            return null;
        }
    }
}
