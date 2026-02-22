package com.agricultural.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * 日期工具类
 * 提供日期时间的常用操作
 */
public class DateUtil {

    /**
     * 标准日期格式
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 标准时间格式
     */
    public static final String TIME_FORMAT = "HH:mm:ss";

    /**
     * 标准日期时间格式
     */
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 中文日期格式
     */
    public static final String CHINESE_DATE_FORMAT = "yyyy年MM月dd日";

    /**
     * 中文日期时间格式
     */
    public static final String CHINESE_DATETIME_FORMAT = "yyyy年MM月dd日 HH:mm:ss";

    /**
     * 获取当前日期
     */
    public static LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    /**
     * 获取当前时间
     */
    public static LocalTime getCurrentTime() {
        return LocalTime.now();
    }

    /**
     * 获取当前日期时间
     */
    public static LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }

    /**
     * 获取当前时间戳（毫秒）
     */
    public static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * 获取当前时间戳（秒）
     */
    public static long getCurrentTimestampSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 格式化日期
     */
    public static String formatDate(LocalDate date) {
        return formatDate(date, DATE_FORMAT);
    }

    /**
     * 格式化日期 - 自定义格式
     */
    public static String formatDate(LocalDate date, String pattern) {
        if (date == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return date.format(formatter);
    }

    /**
     * 格式化时间
     */
    public static String formatTime(LocalTime time) {
        return formatTime(time, TIME_FORMAT);
    }

    /**
     * 格式化时间 - 自定义格式
     */
    public static String formatTime(LocalTime time, String pattern) {
        if (time == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return time.format(formatter);
    }

    /**
     * 格式化日期时间
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return formatDateTime(dateTime, DATETIME_FORMAT);
    }

    /**
     * 格式化日期时间 - 自定义格式
     */
    public static String formatDateTime(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }

    /**
     * 解析日期字符串
     */
    public static LocalDate parseDate(String dateStr) {
        return parseDate(dateStr, DATE_FORMAT);
    }

    /**
     * 解析日期字符串 - 自定义格式
     */
    public static LocalDate parseDate(String dateStr, String pattern) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDate.parse(dateStr, formatter);
    }

    /**
     * 解析时间字符串
     */
    public static LocalTime parseTime(String timeStr) {
        return parseTime(timeStr, TIME_FORMAT);
    }

    /**
     * 解析时间字符串 - 自定义格式
     */
    public static LocalTime parseTime(String timeStr, String pattern) {
        if (timeStr == null || timeStr.isEmpty()) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalTime.parse(timeStr, formatter);
    }

    /**
     * 解析日期时间字符串
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return parseDateTime(dateTimeStr, DATETIME_FORMAT);
    }

    /**
     * 解析日期时间字符串 - 自定义格式
     */
    public static LocalDateTime parseDateTime(String dateTimeStr, String pattern) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.parse(dateTimeStr, formatter);
    }

    /**
     * 计算两个日期之间的天数差
     */
    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    /**
     * 计算两个日期时间之间的小时数差
     */
    public static long hoursBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return ChronoUnit.HOURS.between(startDateTime, endDateTime);
    }

    /**
     * 计算两个日期时间之间的分钟数差
     */
    public static long minutesBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return ChronoUnit.MINUTES.between(startDateTime, endDateTime);
    }

    /**
     * 计算两个日期时间之间的秒数差
     */
    public static long secondsBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return ChronoUnit.SECONDS.between(startDateTime, endDateTime);
    }

    /**
     * 日期加上指定天数
     */
    public static LocalDate addDays(LocalDate date, long days) {
        return date.plusDays(days);
    }

    /**
     * 日期时间加上指定小时数
     */
    public static LocalDateTime addHours(LocalDateTime dateTime, long hours) {
        return dateTime.plusHours(hours);
    }

    /**
     * 日期时间加上指定分钟数
     */
    public static LocalDateTime addMinutes(LocalDateTime dateTime, long minutes) {
        return dateTime.plusMinutes(minutes);
    }

    /**
     * 日期时间加上指定秒数
     */
    public static LocalDateTime addSeconds(LocalDateTime dateTime, long seconds) {
        return dateTime.plusSeconds(seconds);
    }

    /**
     * 将Date转换为LocalDateTime
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * 将LocalDateTime转换为Date
     */
    public static Date localDateTimeToDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 判断是否是今天
     */
    public static boolean isToday(LocalDate date) {
        return date.equals(LocalDate.now());
    }

    /**
     * 判断是否是昨天
     */
    public static boolean isYesterday(LocalDate date) {
        return date.equals(LocalDate.now().minusDays(1));
    }

    /**
     * 判断是否是明天
     */
    public static boolean isTomorrow(LocalDate date) {
        return date.equals(LocalDate.now().plusDays(1));
    }

    /**
     * 判断是否是同一天
     */
    public static boolean isSameDay(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        return dateTime1.toLocalDate().equals(dateTime2.toLocalDate());
    }
}
