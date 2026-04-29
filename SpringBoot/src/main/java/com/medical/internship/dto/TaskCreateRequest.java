package com.medical.internship.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat; // 👈 1. 记得导入这个包

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
    /**
     * 截止时间
     */
    //  2. 添加这一行注解，强制指定前后端交互的时间格式
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime deadline;
}
