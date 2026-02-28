package com.health.util;

import com.health.exception.BusinessException;
import java.util.regex.Pattern;

/**
 * 数据验证工具类
 * 提供通用的数据验证方法
 */
public class ValidationUtil {

    // 邮箱正则表达式
    private static final Pattern EMAIL_PATTERN = 
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    // 电话号码正则表达式（中国）
    private static final Pattern PHONE_PATTERN = 
            Pattern.compile("^1[3-9]\\d{9}$");

    // 用户名正则表达式（字母、数字、下划线，3-20位）
    private static final Pattern USERNAME_PATTERN = 
            Pattern.compile("^[a-zA-Z0-9_]{3,20}$");

    /**
     * 验证用户名格式
     */
    public static void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new BusinessException("用户名不能为空");
        }
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new BusinessException("用户名格式不正确，必须为3-20位字母、数字或下划线");
        }
    }

    /**
     * 验证密码强度
     */
    public static void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new BusinessException("密码不能为空");
        }
        if (password.length() < 6) {
            throw new BusinessException("密码长度不能少于6位");
        }
        if (password.length() > 50) {
            throw new BusinessException("密码长度不能超过50位");
        }
    }

    /**
     * 验证邮箱格式
     */
    public static void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new BusinessException("邮箱不能为空");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new BusinessException("邮箱格式不正确");
        }
    }

    /**
     * 验证电话号码格式
     */
    public static void validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new BusinessException("电话号码不能为空");
        }
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new BusinessException("电话号码格式不正确");
        }
    }

    /**
     * 验证年龄范围
     */
    public static void validateAge(Integer age) {
        if (age == null) {
            throw new BusinessException("年龄不能为空");
        }
        if (age < 0 || age > 150) {
            throw new BusinessException("年龄必须在0-150之间");
        }
    }

    /**
     * 验证身高范围（单位：cm）
     */
    public static void validateHeight(Double height) {
        if (height == null) {
            throw new BusinessException("身高不能为空");
        }
        if (height < 50 || height > 250) {
            throw new BusinessException("身高必须在50-250cm之间");
        }
    }

    /**
     * 验证体重范围（单位：kg）
     */
    public static void validateWeight(Double weight) {
        if (weight == null) {
            throw new BusinessException("体重不能为空");
        }
        if (weight < 10 || weight > 300) {
            throw new BusinessException("体重必须在10-300kg之间");
        }
    }

    /**
     * 验证心率范围（单位：次/分钟）
     */
    public static void validateHeartRate(Integer heartRate) {
        if (heartRate == null) {
            throw new BusinessException("心率不能为空");
        }
        if (heartRate < 30 || heartRate > 200) {
            throw new BusinessException("心率必须在30-200次/分钟之间");
        }
    }

    /**
     * 验证血压格式（格式：收缩压/舒张压）
     */
    public static void validateBloodPressure(String bloodPressure) {
        if (bloodPressure == null || bloodPressure.trim().isEmpty()) {
            throw new BusinessException("血压不能为空");
        }
        String[] parts = bloodPressure.split("/");
        if (parts.length != 2) {
            throw new BusinessException("血压格式不正确，应为：收缩压/舒张压");
        }
        try {
            int systolic = Integer.parseInt(parts[0].trim());
            int diastolic = Integer.parseInt(parts[1].trim());
            if (systolic < 60 || systolic > 200) {
                throw new BusinessException("收缩压必须在60-200mmHg之间");
            }
            if (diastolic < 40 || diastolic > 130) {
                throw new BusinessException("舒张压必须在40-130mmHg之间");
            }
        } catch (NumberFormatException e) {
            throw new BusinessException("血压值必须为数字");
        }
    }

    /**
     * 验证字符串不为空
     */
    public static void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new BusinessException(fieldName + "不能为空");
        }
    }

    /**
     * 验证对象不为空
     */
    public static void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new BusinessException(fieldName + "不能为空");
        }
    }

    /**
     * 验证字符串长度
     */
    public static void validateStringLength(String value, int minLength, int maxLength, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new BusinessException(fieldName + "不能为空");
        }
        if (value.length() < minLength || value.length() > maxLength) {
            throw new BusinessException(fieldName + "长度必须在" + minLength + "-" + maxLength + "之间");
        }
    }

    /**
     * 验证医师执业证号格式
     */
    public static void validateLicenseNumber(String licenseNumber) {
        if (licenseNumber == null || licenseNumber.trim().isEmpty()) {
            throw new BusinessException("执业证号不能为空");
        }
        if (licenseNumber.length() < 10 || licenseNumber.length() > 30) {
            throw new BusinessException("执业证号长度不正确");
        }
    }
}
