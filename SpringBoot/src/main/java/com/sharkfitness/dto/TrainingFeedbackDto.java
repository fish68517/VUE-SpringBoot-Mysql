package com.sharkfitness.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder // Lombok annotation for the builder pattern, useful for mapping
public class TrainingFeedbackDto {
    private Long id;
    private Long planId;
    private String content;
    private Integer rating;
    private LocalDate feedbackDate;
}
