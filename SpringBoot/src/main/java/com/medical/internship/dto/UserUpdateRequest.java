package com.medical.internship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

/**
 * 用户更新请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {
    
    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;
}
