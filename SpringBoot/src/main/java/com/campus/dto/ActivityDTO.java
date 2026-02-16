package com.campus.dto;

import com.campus.entity.Activity;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Activity Data Transfer Object
 * Used for transferring activity data between layers
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivityDTO {

    /**
     * Activity ID
     */
    private Long id;

    /**
     * Organizer ID
     */
    private Long organizerId;

    /**
     * Organizer information (nested)
     */
    private UserDTO organizer;

    /**
     * Activity title
     */
    @NotBlank(message = "Activity title is required")
    private String title;

    /**
     * Activity description
     */
    private String description;

    /**
     * Activity type
     */
    private String type;

    /**
     * Cover image URL
     */
    private String coverUrl;

    /**
     * Activity start time
     */
    @NotNull(message = "Activity start time is required")
    private LocalDateTime startTime;

    /**
     * Activity end time
     */
    @NotNull(message = "Activity end time is required")
    private LocalDateTime endTime;

    /**
     * Activity location
     */
    private String location;

    /**
     * Maximum number of participants
     */
    private Integer maxParticipants;

    /**
     * Registration deadline
     */
    private LocalDateTime registrationDeadline;

    /**
     * Whether crowdfunding is enabled
     */
    private Boolean enableCrowdfunding;

    /**
     * Crowdfunding target amount
     */
    private BigDecimal crowdfundingTarget;

    /**
     * Activity status
     */
    private Activity.ActivityStatus status;

    /**
     * Creation timestamp
     */
    private LocalDateTime createdAt;

    /**
     * Last update timestamp
     */
    private LocalDateTime updatedAt;

    /**
     * Registration count
     */
    private Long registrationCount;

    /**
     * Crowdfunding amount
     */
    private BigDecimal crowdfundingAmount;

    /**
     * Convert entity to DTO
     */
    public static ActivityDTO fromEntity(Activity activity) {
        if (activity == null) {
            return null;
        }
        return ActivityDTO.builder()
                .id(activity.getId())
                .organizerId(activity.getOrganizerId())
                .title(activity.getTitle())
                .description(activity.getDescription())
                .type(activity.getType())
                .coverUrl(activity.getCoverUrl())
                .startTime(activity.getStartTime())
                .endTime(activity.getEndTime())
                .location(activity.getLocation())
                .maxParticipants(activity.getMaxParticipants())
                .registrationDeadline(activity.getRegistrationDeadline())
                .enableCrowdfunding(activity.getEnableCrowdfunding())
                .crowdfundingTarget(activity.getCrowdfundingTarget())
                .status(activity.getStatus())
                .createdAt(activity.getCreatedAt())
                .updatedAt(activity.getUpdatedAt())
                .build();
    }

    /**
     * Convert DTO to entity
     */
    public Activity toEntity() {
        Activity activity = Activity.builder()
                .organizerId(this.organizerId)
                .title(this.title)
                .description(this.description)
                .type(this.type)
                .coverUrl(this.coverUrl)
                .startTime(this.startTime)
                .endTime(this.endTime)
                .location(this.location)
                .maxParticipants(this.maxParticipants)
                .registrationDeadline(this.registrationDeadline)
                .enableCrowdfunding(this.enableCrowdfunding)
                .crowdfundingTarget(this.crowdfundingTarget)
                .status(this.status)
                .build();
        activity.setId(this.id);
        return activity;
    }

}
