package com.medical.internship.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 周记响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeeklyReportResponse {
    
    /**
     * 周记ID
     */
    private Long id;
    
    /**
     * 实习记录ID
     */
    private Long internshipId;
    
    /**
     * 学生姓名
     */
    private String studentName;
    
    /**
     * 周次
     */
    private Integer weekNumber;
    
    /**
     * 周记内容
     */
    private String content;
    
    /**
     * 老师评语
     */
    private String teacherComment;
    
    /**
     * 老师评分
     */
    private Integer teacherScore;
    
    /**
     * 周记状态
     */
    private String status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
