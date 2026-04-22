package com.powerinspection.dto;

import com.powerinspection.entity.UserRole;
import com.powerinspection.entity.UserStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "姓名不能为空")
    private String realName;

    @NotNull(message = "角色不能为空")
    private UserRole role;

    private String phone;
    private String email;
    private UserStatus status = UserStatus.ENABLED;
}
