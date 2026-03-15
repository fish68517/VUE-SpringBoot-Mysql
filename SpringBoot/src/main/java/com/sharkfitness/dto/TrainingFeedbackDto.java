package com.sharkfitness.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class TrainingFeedbackDto {
    private Long id;
    private Long planId;
    private String content;
    private Integer rating;
    private LocalDate feedbackDate;
    private String feeling;
    private String imageUrls;
    private String videoUrls;
    private String documentUrls;
}
