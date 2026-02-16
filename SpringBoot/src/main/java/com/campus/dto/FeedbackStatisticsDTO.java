package com.campus.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Feedback Statistics Data Transfer Object
 * Contains aggregated statistics for organizer's feedback
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeedbackStatisticsDTO {

    /**
     * Total number of feedback submissions
     */
    private Long totalFeedback;

    /**
     * Average rating across all feedback
     */
    private Double averageRating;

    /**
     * Number of activities with feedback
     */
    private Long activitiesWithFeedback;

}
