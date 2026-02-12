package com.medical.internship.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * 创建任务请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreateRequest {
    
    /**
     * 任务标题
     */
    @NotBlank(message = "任务标题不能为空")
    private String title;
    
    /**
     * 任务内容
     */
    @NotBlank(message = "任务内容不能为空")
    private String content;
    
    /**
     * 截止时间
     */
    private LocalDateTime deadline;
}
