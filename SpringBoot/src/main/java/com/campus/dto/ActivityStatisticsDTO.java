package com.campus.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Activity Statistics Data Transfer Object
 * Contains aggregated statistics for organizer's activities
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivityStatisticsDTO {

    /**
     * Total number of activities
     */
    private Long totalActivities;

    /**
     * Total number of registrations across all activities
     */
    private Long totalRegistrations;

    /**
     * Average registrations per activity
     */
    private Double averageRegistrationsPerActivity;

    /**
     * Average rating across all activities
     */
    private Double averageRating;

}
