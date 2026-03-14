package com.admin.dto;

import lombok.Data;

// 在 com.admin.dto 包下新建 LoginDTO
@Data
public class LoginDTO {
    private String username;
    private String password;
}
