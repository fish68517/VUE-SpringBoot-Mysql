package com.medical.internship.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 组织响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrganizationResponse {
    
    /**
     * 组织ID
     */
    private Long id;
    
    /**
     * 组织名称
     */
    private String name;
    
    /**
     * 组织类型
     */
    private String type;
    
    /**
     * 组织代码
     */
    private String code;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
