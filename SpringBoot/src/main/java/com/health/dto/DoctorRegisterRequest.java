package com.health.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 医师注册请求DTO
 * 用于接收医师注册请求的信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorRegisterRequest {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 姓名
     */
    private String name;

    /**
     * 执业证号
     */
    private String licenseNumber;

    /**
     * 专科
     */
    private String specialization;

    /**
     * 医院
     */
    private String hospital;
}
