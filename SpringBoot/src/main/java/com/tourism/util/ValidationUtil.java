package com.tourism.util;

import com.tourism.exception.ValidationException;
import java.util.regex.Pattern;

/**
 * 数据验证工具类
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = 
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    private static final Pattern PHONE_PATTERN = 
            Pattern.compile("^1[3-9]\\d{9}$");
    
    private static final Pattern USERNAME_PATTERN = 
            Pattern.compile("^[a-zA-Z0-9_]{3,20}$");

    /**
     * 验证用户名
     */
    public static void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("用户名不能为空");
        }
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new ValidationException("用户名必须为3-20个字符，只能包含字母、数字和下划线");
        }
    }

    /**
     * 验证密码
     */
    public static void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new ValidationException("密码不能为空");
        }
        if (password.length() < 6) {
            throw new ValidationException("密码长度不能少于6个字符");
        }
    }

    /**
     * 验证邮箱
     */
    public static void validateEmail(String email) {
        if (email != null && !email.trim().isEmpty()) {
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                throw new ValidationException("邮箱格式不正确");
            }
        }
    }

    /**
     * 验证手机号
     */
    public static void validatePhone(String phone) {
        if (phone != null && !phone.trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(phone).matches()) {
                throw new ValidationException("手机号格式不正确");
            }
        }
    }

    /**
     * 验证非空字符串
     */
    public static void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + "不能为空");
        }
    }

    /**
     * 验证正数
     */
    public static void validatePositive(Number value, String fieldName) {
        if (value == null || value.doubleValue() <= 0) {
            throw new ValidationException(fieldName + "必须大于0");
        }
    }

    /**
     * 验证非负数
     */
    public static void validateNonNegative(Number value, String fieldName) {
        if (value == null || value.doubleValue() < 0) {
            throw new ValidationException(fieldName + "不能为负数");
        }
    }
}
