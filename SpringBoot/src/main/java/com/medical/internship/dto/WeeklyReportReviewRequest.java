package com.medical.internship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 周记批阅请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyReportReviewRequest {
    
    /**
     * 批阅状态: REVIEWED(已批阅), REJECTED(已打回)
     */
    @NotBlank(message = "批阅状态不能为空")
    private String status;
    
    /**
     * 老师评语
     */
    private String teacherComment;
    
    /**
     * 老师评分
     */
    @NotNull(message = "评分不能为空")
    private Integer teacherScore;
}
