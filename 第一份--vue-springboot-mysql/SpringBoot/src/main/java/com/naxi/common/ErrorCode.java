package com.naxi.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 成功
    SUCCESS(200, "操作成功"),
    
    // 客户端错误
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    
    // 服务器错误
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    
    // 用户相关错误
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_ALREADY_EXISTS(1002, "用户名已存在"),
    PASSWORD_ERROR(1003, "密码错误"),
    EMAIL_ALREADY_EXISTS(1004, "邮箱已存在"),
    INVALID_USERNAME_FORMAT(1005, "用户名格式不正确"),
    INVALID_EMAIL_FORMAT(1006, "邮箱格式不正确"),
    
    // 纹样相关错误
    PATTERN_NOT_FOUND(2001, "纹样不存在"),
    PATTERN_ALREADY_EXISTS(2002, "纹样已存在"),
    
    // 评论相关错误
    COMMENT_NOT_FOUND(3001, "评论不存在"),
    COMMENT_DELETE_FORBIDDEN(3002, "无权删除评论"),
    
    // 提问相关错误
    QUESTION_NOT_FOUND(3101, "提问不存在"),
    QUESTION_DELETE_FORBIDDEN(3102, "无权删除提问"),
    
    // 收藏相关错误
    COLLECTION_NOT_FOUND(3201, "收藏不存在"),
    COLLECTION_ALREADY_EXISTS(3202, "已收藏该纹样"),
    
    // 作品相关错误
    WORK_NOT_FOUND(3301, "作品不存在"),
    WORK_DELETE_FORBIDDEN(3302, "无权删除作品"),
    
    // 权限相关错误
    PERMISSION_DENIED(4001, "权限不足"),
    ROLE_NOT_FOUND(4002, "角色不存在");

    private final int code;
    private final String message;
}
