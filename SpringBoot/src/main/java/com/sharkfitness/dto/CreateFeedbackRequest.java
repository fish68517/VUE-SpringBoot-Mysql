package com.sharkfitness.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateFeedbackRequest {

    @NotNull(message = "训练计划ID不能为空")
    private Long planId;

    @NotBlank(message = "反馈内容不能为空")
    private String content;

    @Min(value = 1, message = "评分最低为1")
    @Max(value = 5, message = "评分最高为5")
    private Integer rating; // Use Integer, can be optional

    @NotNull(message = "反馈日期不能为空")
    @FutureOrPresent(message = "反馈日期不能是未来的日期")
    private LocalDate feedbackDate;

    private Long studentId;
}
