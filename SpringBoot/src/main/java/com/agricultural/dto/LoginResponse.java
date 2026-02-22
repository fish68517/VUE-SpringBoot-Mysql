package com.agricultural.dto;

import com.agricultural.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应DTO
 * 包含用户信息和JWT令牌
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    
    /**
     * JWT访问令牌
     */
    private String token;
    
    /**
     * 令牌类型
     */
    private String tokenType;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 用户类型
     */
    private User.UserType userType;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 地区
     */
    private String region;
}
