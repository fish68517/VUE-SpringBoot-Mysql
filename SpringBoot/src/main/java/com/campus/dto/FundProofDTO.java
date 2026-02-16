package com.campus.dto;

import com.campus.entity.FundProof;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Fund Proof Data Transfer Object
 * Used for transferring fund proof data between layers
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FundProofDTO {

    /**
     * Fund Proof ID
     */
    private Long id;

    /**
     * Activity ID
     */
    @NotNull(message = "Activity ID is required")
    private Long activityId;

    /**
     * Organizer ID
     */
    private Long organizerId;

    /**
     * Proof file URL
     */
    private String fileUrl;

    /**
     * Usage description
     */
    private String description;

    /**
     * Proof status
     */
    private FundProof.ProofStatus status;

    /**
     * Rejection reason (if rejected)
     */
    private String rejectionReason;

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
    public static FundProofDTO fromEntity(FundProof fundProof) {
        if (fundProof == null) {
            return null;
        }
        return FundProofDTO.builder()
                .id(fundProof.getId())
                .activityId(fundProof.getActivityId())
                .organizerId(fundProof.getOrganizerId())
                .fileUrl(fundProof.getFileUrl())
                .description(fundProof.getDescription())
                .status(fundProof.getStatus())
                .rejectionReason(fundProof.getRejectionReason())
                .createdAt(fundProof.getCreatedAt())
                .updatedAt(fundProof.getUpdatedAt())
                .build();
    }

    /**
     * Convert DTO to entity
     */
    public FundProof toEntity() {
        FundProof fundProof = FundProof.builder()
                .activityId(this.activityId)
                .organizerId(this.organizerId)
                .fileUrl(this.fileUrl)
                .description(this.description)
                .status(this.status != null ? this.status : FundProof.ProofStatus.PENDING_AUDIT)
                .rejectionReason(this.rejectionReason)
                .build();
        fundProof.setId(this.id);
        return fundProof;
    }

}
