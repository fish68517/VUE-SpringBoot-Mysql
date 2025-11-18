package com.xingluo.petshop.dto;

import lombok.Data;

/**
 * 审核商品DTO
 */
@Data
public class AuditProductDTO {
    
    /**
     * 是否通过审核（true-通过/false-拒绝）
     */
    private Boolean approved;
    
    /**
     * 拒绝原因（可选）
     */
    private String reason;
}
