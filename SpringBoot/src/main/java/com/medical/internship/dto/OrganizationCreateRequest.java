package com.medical.internship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 创建组织请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationCreateRequest {
    
    /**
     * 组织名称
     */
    @NotBlank(message = "组织名称不能为空")
    private String name;
    
    /**
     * 组织类型: SCHOOL(学校) 或 HOSPITAL(医院)
     */
    @NotBlank(message = "组织类型不能为空")
    @Pattern(regexp = "^(SCHOOL|HOSPITAL)$", message = "组织类型必须为SCHOOL或HOSPITAL")
    private String type;
    
    /**
     * 组织代码
     */
    @NotBlank(message = "组织代码不能为空")
    private String code;
}
