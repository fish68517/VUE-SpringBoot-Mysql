package com.medical.internship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 岗位状态更新请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostStatusUpdateRequest {
    
    /**
     * 岗位状态: PUBLISHED(已发布), ARCHIVED(已下架)
     */
    @NotBlank(message = "岗位状态不能为空")
    private String status;
}
