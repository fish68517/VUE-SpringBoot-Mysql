package com.medical.internship.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 手动创建实习记录的请求体
 */
@Data
public class InternshipCreateRequest {

    @NotNull(message = "关联申请ID不能为空")
    private Long applicationId;

    @NotNull(message = "关联岗位ID不能为空")
    private Long postId;

    @NotNull(message = "带教老师ID不能为空")
    private Long teacherId;

    @NotNull(message = "开始日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate startDate;

    @NotNull(message = "结束日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate endDate;

    @NotBlank(message = "实习状态不能为空")
    private String status;
}