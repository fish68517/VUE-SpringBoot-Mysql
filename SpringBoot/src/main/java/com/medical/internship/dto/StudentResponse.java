package com.medical.internship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 学生响应（用于老师端）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponse {
    
    /**
     * 实习记录ID
     */
    private Long internshipId;
    
    /**
     * 学生ID
     */
    private Long studentId;
    
    /**
     * 学生姓名
     */
    private String studentName;
    
    /**
     * 岗位名称
     */
    private String postTitle;
    
    /**
     * 科室
     */
    private String department;
    
    /**
     * 开始日期
     */
    private LocalDate startDate;
    
    /**
     * 结束日期
     */
    private LocalDate endDate;
    
    /**
     * 实习状态
     */
    private String internshipStatus;
}
