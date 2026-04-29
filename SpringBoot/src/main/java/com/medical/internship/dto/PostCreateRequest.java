package com.medical.internship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

/**
 * 岗位创建请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCreateRequest {
    
    /**
     * 岗位名称
     */
    @NotBlank(message = "岗位名称不能为空")
    private String title;
    
    /**
     * 科室
     */
    @NotBlank(message = "科室不能为空")
    private String department;
    
    /**
     * 岗位描述
     */
    private String description;
    
    /**
     * 岗位名额
     */
    @NotNull(message = "岗位名额不能为空")
    @Positive(message = "岗位名额必须大于0")
    private Integer quota;
    
    /**
     * 实习周期（天数）
     */
    @NotNull(message = "实习周期不能为空")
    @Positive(message = "实习周期必须大于0")
    private Integer duration;
    
    /**
     * 面向学校ID列表
     */
    private List<Long> visibleSchoolIds;
}
