package com.medical.internship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 周记创建请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyReportCreateRequest {
    
    /**
     * 周次
     */
    @NotNull(message = "周次不能为空")
    private Integer weekNumber;
    
    /**
     * 周记内容
     */
    @NotBlank(message = "周记内容不能为空")
    private String content;
}
