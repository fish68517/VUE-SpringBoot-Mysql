package com.medical.internship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 更新组织请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationUpdateRequest {
    
    /**
     * 组织名称
     */
    @NotBlank(message = "组织名称不能为空")
    private String name;
}
