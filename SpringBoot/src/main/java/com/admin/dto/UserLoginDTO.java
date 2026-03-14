package com.admin.dto;

import lombok.Data;

@Data
public class UserLoginDTO {
    private String account; // 对应前端传的 account
    private String password;
}