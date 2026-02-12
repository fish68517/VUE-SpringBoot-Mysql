package com.medical.internship.common;

import com.medical.internship.dto.UserResponse;

/**
 * 会话上下文 - 用于存储当前用户信息
 */
public class SessionContext {
    
    private static final ThreadLocal<UserResponse> userThreadLocal = new ThreadLocal<>();
    
    /**
     * 设置当前用户
     */
    public static void setCurrentUser(UserResponse user) {
        userThreadLocal.set(user);
    }
    
    /**
     * 获取当前用户
     */
    public static UserResponse getCurrentUser() {
        return userThreadLocal.get();
    }
    
    /**
     * 获取当前用户ID
     */
    public static Long getCurrentUserId() {
        UserResponse user = userThreadLocal.get();
        return user != null ? user.getId() : null;
    }
    
    /**
     * 获取当前用户所属组织ID
     */
    public static Long getCurrentOrganizationId() {
        UserResponse user = userThreadLocal.get();
        return user != null && user.getOrganization() != null ? user.getOrganization().getId() : null;
    }
    
    /**
     * 获取当前用户角色
     */
    public static String getCurrentUserRole() {
        UserResponse user = userThreadLocal.get();
        return user != null ? user.getRole() : null;
    }
    
    /**
     * 清除当前用户信息
     */
    public static void clear() {
        userThreadLocal.remove();
    }
}
