package com.agricultural.dto;

import com.agricultural.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data // 如果你用了Lombok。如果没有，请手动生成 getter/setter
public class RegisterRequest {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    private String email;

    private String phone;
    
    private User.UserType userType;

    private String region;
}