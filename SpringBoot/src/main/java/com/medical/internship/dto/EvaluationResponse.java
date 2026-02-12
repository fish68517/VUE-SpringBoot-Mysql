package com.medical.internship.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 评价响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EvaluationResponse {
    
    /**
     * 评价ID
     */
    private Long id;
    
    /**
     * 实习记录ID
     */
    private Long internshipId;
    
    /**
     * 评价人ID
     */
    private Long evaluatorId;
    
    /**
     * 评价人姓名
     */
    private String evaluatorName;
    
    /**
     * 评价人信息
     */
    private UserResponse evaluator;
    
    /**
     * 评价人类型
     */
    private String evaluatorType;
    
    /**
     * 评分
     */
    private Integer score;
    
    /**
     * 评价内容
     */
    private String comment;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
