package com.zhuang.embroidery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员修改用户信息请求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserUpdateRequest {

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 个人简介
     */
    private String bio;

    /**
     * 角色：user/admin
     */
    private String role;

}
