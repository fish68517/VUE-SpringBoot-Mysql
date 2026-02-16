package com.campus.dto;

import com.campus.entity.CrowdfundingTier;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Crowdfunding Tier Data Transfer Object
 * Used for transferring crowdfunding tier data between layers
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CrowdfundingTierDTO {

    /**
     * Tier ID
     */
    private Long id;

    /**
     * Activity ID
     */
    private Long activityId;

    /**
     * Tier name
     */
    private String name;

    /**
     * Tier amount
     */
    private BigDecimal amount;

    /**
     * Tier description
     */
    private String description;

    /**
     * Whether this is a preset tier
     */
    private Boolean isPreset;

    /**
     * Display order
     */
    private Integer displayOrder;

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
    public static CrowdfundingTierDTO fromEntity(CrowdfundingTier tier) {
        if (tier == null) {
            return null;
        }
        return CrowdfundingTierDTO.builder()
                .id(tier.getId())
                .activityId(tier.getActivityId())
                .name(tier.getName())
                .amount(tier.getAmount())
                .description(tier.getDescription())
                .isPreset(tier.getIsPreset())
                .displayOrder(tier.getDisplayOrder())
                .createdAt(tier.getCreatedAt())
                .updatedAt(tier.getUpdatedAt())
                .build();
    }

    /**
     * Convert DTO to entity
     */
    public CrowdfundingTier toEntity() {
        CrowdfundingTier tier = CrowdfundingTier.builder()
                .activityId(this.activityId)
                .name(this.name)
                .amount(this.amount)
                .description(this.description)
                .isPreset(this.isPreset)
                .displayOrder(this.displayOrder)
                .build();
        tier.setId(this.id);
        return tier;
    }

}
