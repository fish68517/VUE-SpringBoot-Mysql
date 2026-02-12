package com.medical.internship.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 申请响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplicationResponse {
    
    /**
     * 申请ID
     */
    private Long id;
    
    /**
     * 学生信息
     */
    private UserResponse student;
    
    /**
     * 岗位信息
     */
    private PostResponse post;
    
    /**
     * 申请理由
     */
    private String reason;
    
    /**
     * 学校审批状态
     */
    private String schoolStatus;
    
    /**
     * 医院审批状态
     */
    private String hospitalStatus;
    
    /**
     * 学校审批意见
     */
    private String schoolOpinion;
    
    /**
     * 医院审批意见
     */
    private String hospitalOpinion;
    
    /**
     * 学生名称（便利字段）
     */
    private String studentName;
    
    /**
     * 岗位标题（便利字段）
     */
    private String postTitle;
    
    /**
     * 医院名称（便利字段）
     */
    private String hospitalName;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
