package com.naxi.common;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggerUtil {

    /**
     * 记录信息级别日志
     */
    public static void info(String message) {
        log.info(message);
    }

    /**
     * 记录信息级别日志（带参数）
     */
    public static void info(String message, Object... args) {
        log.info(message, args);
    }

    /**
     * 记录调试级别日志
     */
    public static void debug(String message) {
        log.debug(message);
    }

    /**
     * 记录调试级别日志（带参数）
     */
    public static void debug(String message, Object... args) {
        log.debug(message, args);
    }

    /**
     * 记录警告级别日志
     */
    public static void warn(String message) {
        log.warn(message);
    }

    /**
     * 记录警告级别日志（带参数）
     */
    public static void warn(String message, Object... args) {
        log.warn(message, args);
    }

    /**
     * 记录错误级别日志
     */
    public static void error(String message) {
        log.error(message);
    }

    /**
     * 记录错误级别日志（带异常）
     */
    public static void error(String message, Throwable throwable) {
        log.error(message, throwable);
    }

    /**
     * 记录错误级别日志（带参数和异常）
     */
    public static void error(String message, Throwable throwable, Object... args) {
        log.error(message, args, throwable);
    }

    /**
     * 记录用户操作日志
     */
    public static void logUserOperation(String userId, String operationType, String details) {
        log.info("用户操作 - 用户ID: {}, 操作类型: {}, 详情: {}", userId, operationType, details);
    }

    /**
     * 记录管理员操作日志
     */
    public static void logAdminOperation(String adminId, String operationType, String details) {
        log.info("管理员操作 - 管理员ID: {}, 操作类型: {}, 详情: {}", adminId, operationType, details);
    }

    /**
     * 记录系统操作日志
     */
    public static void logSystemOperation(String operationType, String details) {
        log.info("系统操作 - 操作类型: {}, 详情: {}", operationType, details);
    }
}
