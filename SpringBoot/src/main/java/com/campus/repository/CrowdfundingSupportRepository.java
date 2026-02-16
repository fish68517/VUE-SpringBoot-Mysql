package com.campus.repository;

import com.campus.entity.CrowdfundingSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

/**
 * Crowdfunding Support Repository
 * Provides data access methods for CrowdfundingSupport entity
 */
@Repository
public interface CrowdfundingSupportRepository extends BaseRepository<CrowdfundingSupport> {

    /**
     * Find crowdfunding supports by activity ID
     */
    Page<CrowdfundingSupport> findByActivityId(Long activityId, Pageable pageable);

    /**
     * Find crowdfunding supports by user ID
     */
    Page<CrowdfundingSupport> findByUserId(Long userId, Pageable pageable);

    /**
     * Get total crowdfunding amount for an activity
     */
    @Query("SELECT COALESCE(SUM(cs.amount), 0) FROM CrowdfundingSupport cs WHERE cs.activityId = :activityId AND cs.paymentStatus = 'COMPLETED'")
    BigDecimal getTotalAmountByActivityId(@Param("activityId") Long activityId);

    /**
     * Count crowdfunding supporters for an activity
     */
    long countByActivityIdAndPaymentStatus(Long activityId, CrowdfundingSupport.PaymentStatus paymentStatus);

    /**
     * Get total crowdfunding amount across all activities
     */
    @Query("SELECT COALESCE(SUM(cs.amount), 0) FROM CrowdfundingSupport cs WHERE cs.paymentStatus = 'COMPLETED'")
    BigDecimal getTotalCrowdfundingAmount();

}
