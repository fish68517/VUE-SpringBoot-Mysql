package com.medical.internship.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 岗位响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostResponse {
    
    /**
     * 岗位ID
     */
    private Long id;
    
    /**
     * 所属医院信息
     */
    private OrganizationResponse hospital;
    
    /**
     * 岗位名称
     */
    private String title;
    
    /**
     * 科室
     */
    private String department;
    
    /**
     * 岗位描述
     */
    private String description;
    
    /**
     * 岗位名额
     */
    private Integer quota;
    
    /**
     * 实习周期（天数）
     */
    private Integer duration;
    
    /**
     * 面向学校ID列表
     */
    private List<Long> visibleSchoolIds;
    
    /**
     * 岗位状态
     */
    private String status;
    
    /**
     * 已申请人数
     */
    private Integer applicationCount;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
