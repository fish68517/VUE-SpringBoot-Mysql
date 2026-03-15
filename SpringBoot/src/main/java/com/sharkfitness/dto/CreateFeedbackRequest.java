package com.sharkfitness.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateFeedbackRequest {

    @NotNull(message = "训练计划ID不能为空")
    private Long planId;

    private String content;

    @Min(value = 1, message = "评分最低为1")
    @Max(value = 5, message = "评分最高为5")
    private Integer rating;

    @NotNull(message = "反馈日期不能为空")
    @FutureOrPresent(message = "反馈日期不能是未来日期")
    private LocalDate feedbackDate;

    private Long studentId;
    private Long coachId;
    private String feeling;
    private String imageUrls;
    private String videoUrls;
    private String documentUrls;
}
