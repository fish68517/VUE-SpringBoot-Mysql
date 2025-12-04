package com.shenyang.musicfestival.util;

import com.shenyang.musicfestival.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

/**
 * Utility class for authentication-related operations
 */
@Component
public class AuthUtil {

    /**
     * Extract user ID from request
     */
    public static Long getUserIdFromRequest(HttpServletRequest request) {
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj == null) {
            throw new UnauthorizedException("用户未认证");
        }
        return (Long) userIdObj;
    }

    /**
     * Extract role from request
     */
    public static String getRoleFromRequest(HttpServletRequest request) {
        Object roleObj = request.getAttribute("role");
        if (roleObj == null) {
            throw new UnauthorizedException("用户角色信息缺失");
        }
        return (String) roleObj;
    }

    /**
     * Check if user is admin
     */
    public static boolean isAdmin(HttpServletRequest request) {
        String role = getRoleFromRequest(request);
        return "admin".equalsIgnoreCase(role);
    }

    /**
     * Verify user is admin, throw exception if not
     */
    public static void requireAdmin(HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new UnauthorizedException("需要管理员权限");
        }
    }

}
