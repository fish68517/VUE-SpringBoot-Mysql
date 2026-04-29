package com.medical.internship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Positive;
import java.util.List;

/**
 * 岗位更新请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostUpdateRequest {
    
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
    @Positive(message = "岗位名额必须大于0")
    private Integer quota;
    
    /**
     * 实习周期（天数）
     */
    @Positive(message = "实习周期必须大于0")
    private Integer duration;
    
    /**
     * 面向学校ID列表
     */
    private List<Long> visibleSchoolIds;
}
