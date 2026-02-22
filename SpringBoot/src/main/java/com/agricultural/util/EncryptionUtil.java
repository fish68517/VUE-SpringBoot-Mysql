package com.agricultural.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * 加密工具类
 * 提供密码加密、哈希、Base64编码等功能
 */
public class EncryptionUtil {

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * BCrypt密码加密
     * 用于用户密码存储
     */
    public static String encryptPassword(String password) {
        if (StringUtil.isEmpty(password)) {
            throw new IllegalArgumentException("密码不能为空");
        }
        return passwordEncoder.encode(password);
    }

    /**
     * BCrypt密码验证
     * 用于用户登录验证
     */
    public static boolean verifyPassword(String rawPassword, String encodedPassword) {
        if (StringUtil.isEmpty(rawPassword) || StringUtil.isEmpty(encodedPassword)) {
            return false;
        }
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * MD5哈希
     * 注意：MD5已不安全，仅用于非安全敏感场景
     */
    public static String md5(String input) {
        if (StringUtil.isEmpty(input)) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(messageDigest);
        } catch (NoSuchAlgorithmException e) {
            LoggerUtil.error("MD5加密失败: " + e.getMessage(), e);
            throw new RuntimeException("MD5加密失败", e);
        }
    }

    /**
     * SHA-256哈希
     */
    public static String sha256(String input) {
        if (StringUtil.isEmpty(input)) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] messageDigest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(messageDigest);
        } catch (NoSuchAlgorithmException e) {
            LoggerUtil.error("SHA-256加密失败: " + e.getMessage(), e);
            throw new RuntimeException("SHA-256加密失败", e);
        }
    }

    /**
     * SHA-512哈希
     */
    public static String sha512(String input) {
        if (StringUtil.isEmpty(input)) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] messageDigest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(messageDigest);
        } catch (NoSuchAlgorithmException e) {
            LoggerUtil.error("SHA-512加密失败: " + e.getMessage(), e);
            throw new RuntimeException("SHA-512加密失败", e);
        }
    }

    /**
     * Base64编码
     */
    public static String base64Encode(String input) {
        if (StringUtil.isEmpty(input)) {
            return null;
        }
        return Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Base64编码 - 字节数组
     */
    public static String base64Encode(byte[] input) {
        if (input == null || input.length == 0) {
            return null;
        }
        return Base64.getEncoder().encodeToString(input);
    }

    /**
     * Base64解码
     */
    public static String base64Decode(String input) {
        if (StringUtil.isEmpty(input)) {
            return null;
        }
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(input);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            LoggerUtil.error("Base64解码失败: " + e.getMessage(), e);
            throw new RuntimeException("Base64解码失败", e);
        }
    }

    /**
     * Base64解码 - 返回字节数组
     */
    public static byte[] base64DecodeToBytes(String input) {
        if (StringUtil.isEmpty(input)) {
            return null;
        }
        try {
            return Base64.getDecoder().decode(input);
        } catch (IllegalArgumentException e) {
            LoggerUtil.error("Base64解码失败: " + e.getMessage(), e);
            throw new RuntimeException("Base64解码失败", e);
        }
    }

    /**
     * 字节数组转十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * 十六进制字符串转字节数组
     */
    private static byte[] hexToBytes(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * 生成随机盐值
     */
    public static String generateSalt() {
        return Base64.getEncoder().encodeToString(
                new byte[]{
                        (byte) (Math.random() * 256),
                        (byte) (Math.random() * 256),
                        (byte) (Math.random() * 256),
                        (byte) (Math.random() * 256),
                        (byte) (Math.random() * 256),
                        (byte) (Math.random() * 256),
                        (byte) (Math.random() * 256),
                        (byte) (Math.random() * 256)
                }
        );
    }

    /**
     * 使用盐值进行SHA-256哈希
     */
    public static String sha256WithSalt(String input, String salt) {
        if (StringUtil.isEmpty(input) || StringUtil.isEmpty(salt)) {
            return null;
        }
        return sha256(input + salt);
    }
}
