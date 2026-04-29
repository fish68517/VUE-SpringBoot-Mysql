package com.medical.internship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 申请创建请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationCreateRequest {
    
    /**
     * 岗位ID
     */
    @NotNull(message = "岗位ID不能为空")
    private Long postId;
    
    /**
     * 申请理由
     */
    @NotBlank(message = "申请理由不能为空")
    private String reason;
}
