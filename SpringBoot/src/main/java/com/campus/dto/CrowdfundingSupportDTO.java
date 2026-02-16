package com.campus.dto;

import com.campus.entity.CrowdfundingSupport;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Crowdfunding Support Data Transfer Object
 * Used for transferring crowdfunding support data between layers
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CrowdfundingSupportDTO {

    /**
     * Support ID
     */
    private Long id;

    /**
     * Activity ID
     */
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
     * Support amount
     */
    private BigDecimal amount;

    /**
     * Crowdfunding tier ID
     */
    private Long tierId;

    /**
     * Payment status
     */
    private CrowdfundingSupport.PaymentStatus paymentStatus;

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
    public static CrowdfundingSupportDTO fromEntity(CrowdfundingSupport support) {
        if (support == null) {
            return null;
        }
        return CrowdfundingSupportDTO.builder()
                .id(support.getId())
                .activityId(support.getActivityId())
                .userId(support.getUserId())
                .amount(support.getAmount())
                .tierId(support.getTierId())
                .paymentStatus(support.getPaymentStatus())
                .createdAt(support.getCreatedAt())
                .updatedAt(support.getUpdatedAt())
                .build();
    }

    /**
     * Convert DTO to entity
     */
    public CrowdfundingSupport toEntity() {
        CrowdfundingSupport support = CrowdfundingSupport.builder()
                .activityId(this.activityId)
                .userId(this.userId)
                .amount(this.amount)
                .tierId(this.tierId)
                .paymentStatus(this.paymentStatus)
                .build();
        support.setId(this.id);
        return support;
    }

}
