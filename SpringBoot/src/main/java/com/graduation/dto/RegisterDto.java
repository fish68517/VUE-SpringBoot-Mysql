package com.graduation.dto;

import lombok.Data;

@Data
public class RegisterDto {


    //  // {"username":"admin123","passwordHash":"123456","fullName":"张管理","departmentId":null,"roleId":3,"isActive":true}
    private String username;
    private String passwordHash;
    private String fullName;
    private Integer departmentId;
    private Integer roleId;
    private Boolean isActive;
    private String email;
}
