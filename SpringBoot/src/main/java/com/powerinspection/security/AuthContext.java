package com.powerinspection.security;

import com.powerinspection.common.BusinessException;
import com.powerinspection.entity.User;
import com.powerinspection.entity.UserRole;

public final class AuthContext {

    private static final ThreadLocal<User> USER_THREAD_LOCAL = new ThreadLocal<>();

    private AuthContext() {
    }

    public static void setUser(User user) {
        USER_THREAD_LOCAL.set(user);
    }

    public static User getUser() {
        User user = USER_THREAD_LOCAL.get();
        if (user == null) {
            throw new BusinessException("未登录或登录已失效");
        }
        return user;
    }

    public static void requireRole(UserRole... roles) {
        User currentUser = getUser();
        for (UserRole role : roles) {
            if (currentUser.getRole() == role) {
                return;
            }
        }
        throw new BusinessException("无权限执行该操作");
    }

    public static void clear() {
        USER_THREAD_LOCAL.remove();
    }
}
