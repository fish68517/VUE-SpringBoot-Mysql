package com.medical.internship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 评价创建请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationCreateRequest {
    
    /**
     * 评价人类型: TEACHER(老师), STUDENT(学生)
     */
    @NotNull(message = "评价人类型不能为空")
    private String evaluatorType;
    
    /**
     * 评分
     */
    @NotNull(message = "评分不能为空")
    private Integer score;
    
    /**
     * 评价内容
     */
    private String comment;
}
