package com.travelMemory.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminUserUpdateRequest {

    @Size(max = 50, message = "用户名不能超过50个字符")
    private String username;

    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱不能超过100个字符")
    private String email;

    @Size(min = 6, message = "密码至少需要6位")
    private String password;

    private String avatarUrl;

    private String bio;

    private String role;
}
