package com.agricultural.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志工具类
 * 提供统一的日志记录接口
 */
public class LoggerUtil {

    private static final Logger logger = LoggerFactory.getLogger(LoggerUtil.class);

    /**
     * 记录调试级别日志
     */
    public static void debug(String message) {
        logger.debug(message);
    }

    /**
     * 记录调试级别日志 - 带参数
     */
    public static void debug(String message, Object... args) {
        logger.debug(message, args);
    }

    /**
     * 记录调试级别日志 - 带异常
     */
    public static void debug(String message, Throwable throwable) {
        logger.debug(message, throwable);
    }

    /**
     * 记录信息级别日志
     */
    public static void info(String message) {
        logger.info(message);
    }

    /**
     * 记录信息级别日志 - 带参数
     */
    public static void info(String message, Object... args) {
        logger.info(message, args);
    }

    /**
     * 记录信息级别日志 - 带异常
     */
    public static void info(String message, Throwable throwable) {
        logger.info(message, throwable);
    }

    /**
     * 记录警告级别日志
     */
    public static void warn(String message) {
        logger.warn(message);
    }

    /**
     * 记录警告级别日志 - 带参数
     */
    public static void warn(String message, Object... args) {
        logger.warn(message, args);
    }

    /**
     * 记录警告级别日志 - 带异常
     */
    public static void warn(String message, Throwable throwable) {
        logger.warn(message, throwable);
    }

    /**
     * 记录错误级别日志
     */
    public static void error(String message) {
        logger.error(message);
    }

    /**
     * 记录错误级别日志 - 带参数
     */
    public static void error(String message, Object... args) {
        logger.error(message, args);
    }

    /**
     * 记录错误级别日志 - 带异常
     */
    public static void error(String message, Throwable throwable) {
        logger.error(message, throwable);
    }

    /**
     * 获取Logger实例
     */
    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    /**
     * 获取Logger实例 - 按名称
     */
    public static Logger getLogger(String name) {
        return LoggerFactory.getLogger(name);
    }
}
