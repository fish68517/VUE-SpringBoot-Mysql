package com.zhuang.embroidery.util;

/**
 * 异常码常量类
 */
public class ErrorCode {

    // 成功
    public static final Integer SUCCESS = 200;

    // 请求参数错误
    public static final Integer BAD_REQUEST = 400;

    // 未授权
    public static final Integer UNAUTHORIZED = 401;

    // 禁止访问
    public static final Integer FORBIDDEN = 403;

    // 资源不存在
    public static final Integer NOT_FOUND = 404;

    // 服务器内部错误
    public static final Integer INTERNAL_SERVER_ERROR = 500;

    // 业务异常
    public static final Integer BUSINESS_ERROR = 1001;

    // 数据验证失败
    public static final Integer VALIDATION_ERROR = 1002;

    // 资源已存在
    public static final Integer RESOURCE_ALREADY_EXISTS = 1003;

    // 资源不存在
    public static final Integer RESOURCE_NOT_FOUND = 1004;

}
