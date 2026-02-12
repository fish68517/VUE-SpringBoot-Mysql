package com.medical.internship.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 实习记录响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InternshipResponse {
    
    /**
     * 实习记录ID
     */
    private Long id;
    
    /**
     * 学生信息
     */
    private UserResponse student;
    
    /**
     * 带教老师信息
     */
    private UserResponse teacher;
    
    /**
     * 岗位信息
     */
    private PostResponse post;
    
    /**
     * 实习开始日期
     */
    private LocalDate startDate;
    
    /**
     * 实习结束日期
     */
    private LocalDate endDate;
    
    /**
     * 实习状态
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
