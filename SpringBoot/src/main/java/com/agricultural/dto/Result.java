package com.agricultural.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 统一响应结果类
 * 用于包装所有API响应数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应状态码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 响应时间戳
     */
    private LocalDateTime timestamp;

    /**
     * 请求路径
     */
    private String path;

    /**
     * 成功响应 - 无数据
     */
    public static <T> Result<T> success() {
        return Result.<T>builder()
                .code(200)
                .message("操作成功")
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 成功响应 - 有数据
     */
    public static <T> Result<T> success(T data) {
        return Result.<T>builder()
                .code(200)
                .message("操作成功")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 成功响应 - 自定义消息
     */
    public static <T> Result<T> success(String message, T data) {
        return Result.<T>builder()
                .code(200)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 失败响应 - 自定义状态码和消息
     */
    public static <T> Result<T> error(Integer code, String message) {
        return Result.<T>builder()
                .code(code)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 失败响应 - 默认状态码
     */
    public static <T> Result<T> error(String message) {
        return Result.<T>builder()
                .code(500)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 参数验证失败
     */
    public static <T> Result<T> validationError(String message) {
        return Result.<T>builder()
                .code(400)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 未授权
     */
    public static <T> Result<T> unauthorized(String message) {
        return Result.<T>builder()
                .code(401)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 禁止访问
     */
    public static <T> Result<T> forbidden(String message) {
        return Result.<T>builder()
                .code(403)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 资源不存在
     */
    public static <T> Result<T> notFound(String message) {
        return Result.<T>builder()
                .code(404)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
