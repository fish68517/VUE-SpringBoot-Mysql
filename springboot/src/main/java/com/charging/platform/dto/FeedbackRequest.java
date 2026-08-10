package com.charging.platform.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FeedbackRequest {
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    @NotNull(message = "请选择反馈类型")
    @Min(value = 0, message = "反馈类型不正确")
    @Max(value = 2, message = "反馈类型不正确")
    private Integer feedbackType;
    @NotBlank(message = "请输入反馈标题")
    @Size(max = 150, message = "反馈标题不能超过150个字符")
    private String title;
    @NotBlank(message = "请输入反馈内容")
    @Size(max = 2000, message = "反馈内容不能超过2000个字符")
    private String content;
    @Size(max = 100, message = "联系方式不能超过100个字符")
    private String contact;
}
