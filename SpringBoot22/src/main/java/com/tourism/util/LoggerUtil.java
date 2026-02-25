package com.tourism.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志工具类（使用简体中文日志）
 */
public class LoggerUtil {

    /**
     * 获取Logger实例
     */
    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    /**
     * 记录信息日志
     */
    public static void info(Logger logger, String message) {
        logger.info(message);
    }

    /**
     * 记录调试日志
     */
    public static void debug(Logger logger, String message) {
        logger.debug(message);
    }

    /**
     * 记录警告日志
     */
    public static void warn(Logger logger, String message) {
        logger.warn(message);
    }

    /**
     * 记录错误日志
     */
    public static void error(Logger logger, String message) {
        logger.error(message);
    }

    /**
     * 记录错误日志（带异常）
     */
    public static void error(Logger logger, String message, Throwable throwable) {
        logger.error(message, throwable);
    }

    /**
     * 记录操作日志
     */
    public static void logOperation(Logger logger, String operation, String details) {
        logger.info("操作: {} - {}", operation, details);
    }

    /**
     * 记录业务异常
     */
    public static void logBusinessException(Logger logger, String message) {
        logger.warn("业务异常: {}", message);
    }

    /**
     * 记录系统异常
     */
    public static void logSystemException(Logger logger, String message, Throwable throwable) {
        logger.error("系统异常: {}", message, throwable);
    }
}
