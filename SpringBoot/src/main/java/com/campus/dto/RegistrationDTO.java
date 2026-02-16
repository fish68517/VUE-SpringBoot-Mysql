package com.campus.dto;

import com.campus.entity.Registration;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Registration Data Transfer Object
 * Used for transferring registration data between layers
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegistrationDTO {

    /**
     * Registration ID
     */
    private Long id;

    /**
     * Activity ID
     */
    @NotNull(message = "Activity ID is required")
    private Long activityId;

    /**
     * Activity information (nested)
     */
    private ActivityDTO activity;

    /**
     * User ID
     */
    private Long userId;

    /**
     * User information (nested)
     */
    private UserDTO user;

    /**
     * Participant name
     */
    @NotBlank(message = "Name is required")
    private String participantName;

    /**
     * Contact phone number
     */
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "Invalid phone number format")
    private String contactPhone;

    /**
     * Registration remarks/notes
     */
    private String remarks;

    /**
     * Registration status
     */
    private Registration.RegistrationStatus status;

    /**
     * Creation timestamp
     */
    private LocalDateTime createdAt;

    /**
     * Last update timestamp
     */
    private LocalDateTime updatedAt;

    /**
     * Convert entity to DTO
     */
    public static RegistrationDTO fromEntity(Registration registration) {
        if (registration == null) {
            return null;
        }
        return RegistrationDTO.builder()
                .id(registration.getId())
                .activityId(registration.getActivityId())
                .userId(registration.getUserId())
                .participantName(registration.getParticipantName())
                .contactPhone(registration.getContactPhone())
                .remarks(registration.getRemarks())
                .status(registration.getStatus())
                .createdAt(registration.getCreatedAt())
                .updatedAt(registration.getUpdatedAt())
                .build();
    }

    /**
     * Convert DTO to entity
     */
    public Registration toEntity() {
        Registration registration = Registration.builder()
                .activityId(this.activityId)
                .userId(this.userId)
                .status(this.status)
                .build();
        registration.setId(this.id);
        return registration;
    }

}
