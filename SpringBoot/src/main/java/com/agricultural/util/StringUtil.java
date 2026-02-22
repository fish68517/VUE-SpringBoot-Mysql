package com.agricultural.util;

import java.util.regex.Pattern;

/**
 * 字符串工具类
 * 提供字符串的常用操作
 */
public class StringUtil {

    /**
     * 邮箱正则表达式
     */
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";

    /**
     * 手机号正则表达式
     */
    private static final String PHONE_PATTERN = "^1[3-9]\\d{9}$";

    /**
     * 身份证号正则表达式
     */
    private static final String ID_CARD_PATTERN = "^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$";

    /**
     * 判断字符串是否为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * 判断字符串是否不为空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 判断字符串是否为空或仅包含空白字符
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().length() == 0;
    }

    /**
     * 判断字符串是否不为空且不仅包含空白字符
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 获取字符串长度
     */
    public static int length(String str) {
        return str == null ? 0 : str.length();
    }

    /**
     * 字符串首字母大写
     */
    public static String capitalize(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * 字符串首字母小写
     */
    public static String uncapitalize(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    /**
     * 字符串转大写
     */
    public static String toUpperCase(String str) {
        return str == null ? null : str.toUpperCase();
    }

    /**
     * 字符串转小写
     */
    public static String toLowerCase(String str) {
        return str == null ? null : str.toLowerCase();
    }

    /**
     * 去除字符串前后空白
     */
    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    /**
     * 字符串反转
     */
    public static String reverse(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return new StringBuilder(str).reverse().toString();
    }

    /**
     * 判断字符串是否包含指定字符串
     */
    public static boolean contains(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        return str.contains(searchStr);
    }

    /**
     * 判断字符串是否以指定字符串开头
     */
    public static boolean startsWith(String str, String prefix) {
        if (str == null || prefix == null) {
            return false;
        }
        return str.startsWith(prefix);
    }

    /**
     * 判断字符串是否以指定字符串结尾
     */
    public static boolean endsWith(String str, String suffix) {
        if (str == null || suffix == null) {
            return false;
        }
        return str.endsWith(suffix);
    }

    /**
     * 字符串替换
     */
    public static String replace(String str, String searchStr, String replacement) {
        if (str == null || searchStr == null || replacement == null) {
            return str;
        }
        return str.replace(searchStr, replacement);
    }

    /**
     * 字符串分割
     */
    public static String[] split(String str, String separator) {
        if (str == null || separator == null) {
            return new String[]{};
        }
        return str.split(separator);
    }

    /**
     * 字符串连接
     */
    public static String join(String separator, String... strings) {
        if (strings == null || strings.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            if (i > 0) {
                sb.append(separator);
            }
            sb.append(strings[i]);
        }
        return sb.toString();
    }

    /**
     * 获取字符串子串
     */
    public static String substring(String str, int start) {
        if (str == null) {
            return null;
        }
        return str.substring(start);
    }

    /**
     * 获取字符串子串
     */
    public static String substring(String str, int start, int end) {
        if (str == null) {
            return null;
        }
        return str.substring(start, end);
    }

    /**
     * 验证邮箱格式
     */
    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) {
            return false;
        }
        return Pattern.matches(EMAIL_PATTERN, email);
    }

    /**
     * 验证手机号格式
     */
    public static boolean isValidPhone(String phone) {
        if (isEmpty(phone)) {
            return false;
        }
        return Pattern.matches(PHONE_PATTERN, phone);
    }

    /**
     * 验证身份证号格式
     */
    public static boolean isValidIdCard(String idCard) {
        if (isEmpty(idCard)) {
            return false;
        }
        return Pattern.matches(ID_CARD_PATTERN, idCard);
    }

    /**
     * 验证是否为数字
     */
    public static boolean isNumeric(String str) {
        if (isEmpty(str)) {
            return false;
        }
        return str.matches("^[0-9]+$");
    }

    /**
     * 验证是否为字母
     */
    public static boolean isAlpha(String str) {
        if (isEmpty(str)) {
            return false;
        }
        return str.matches("^[a-zA-Z]+$");
    }

    /**
     * 验证是否为字母数字
     */
    public static boolean isAlphaNumeric(String str) {
        if (isEmpty(str)) {
            return false;
        }
        return str.matches("^[a-zA-Z0-9]+$");
    }

    /**
     * 获取字符串的字节长度
     */
    public static int getByteLength(String str) {
        if (isEmpty(str)) {
            return 0;
        }
        return str.getBytes().length;
    }

    /**
     * 截断字符串到指定长度
     */
    public static String truncate(String str, int length) {
        if (isEmpty(str) || str.length() <= length) {
            return str;
        }
        return str.substring(0, length);
    }

    /**
     * 截断字符串到指定长度并添加后缀
     */
    public static String truncate(String str, int length, String suffix) {
        if (isEmpty(str) || str.length() <= length) {
            return str;
        }
        return str.substring(0, length) + suffix;
    }

    /**
     * 重复字符串
     */
    public static String repeat(String str, int count) {
        if (isEmpty(str) || count <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    /**
     * 左填充字符串
     */
    public static String leftPad(String str, int size, char padChar) {
        if (str == null) {
            return null;
        }
        int pads = size - str.length();
        if (pads <= 0) {
            return str;
        }
        return repeat(String.valueOf(padChar), pads) + str;
    }

    /**
     * 右填充字符串
     */
    public static String rightPad(String str, int size, char padChar) {
        if (str == null) {
            return null;
        }
        int pads = size - str.length();
        if (pads <= 0) {
            return str;
        }
        return str + repeat(String.valueOf(padChar), pads);
    }
}
