package com.medical.internship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 任务响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponse {
    
    /**
     * 任务ID
     */
    private Long id;
    
    /**
     * 实习记录ID
     */
    private Long internshipId;
    
    /**
     * 任务标题
     */
    private String title;
    
    /**
     * 任务内容
     */
    private String content;
    
    /**
     * 截止时间
     */
    private LocalDateTime deadline;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
