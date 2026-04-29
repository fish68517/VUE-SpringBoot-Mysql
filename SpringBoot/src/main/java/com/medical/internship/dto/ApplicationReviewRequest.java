package com.medical.internship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 申请审批请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationReviewRequest {
    
    /**
     * 审批状态: APPROVED(已批准), REJECTED(已驳回)
     */
    @NotBlank(message = "审批状态不能为空")
    private String status;
    
    /**
     * 审批意见
     */
    private String opinion;
}
