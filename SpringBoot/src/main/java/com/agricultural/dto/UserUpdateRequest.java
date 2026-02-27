package com.agricultural.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {
    // 允许修改的字段
    private String email;
    private String phone;
    private String region;

    // 前端传过来了，虽然业务层不需要更新它们，但我们用实体接收以防报错
    private String username;
    private String userType;
}