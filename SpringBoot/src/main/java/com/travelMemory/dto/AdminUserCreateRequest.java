package com.travelMemory.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminUserCreateRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(max = 50, message = "用户名不能超过50个字符")
    private String username;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱不能超过100个字符")
    private String email;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码至少需要6位")
    private String password;

    private String avatarUrl;

    private String bio;

    @NotBlank(message = "角色不能为空")
    private String role;
}
