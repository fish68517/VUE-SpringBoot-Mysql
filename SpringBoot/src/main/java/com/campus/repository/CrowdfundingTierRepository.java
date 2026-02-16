package com.campus.repository;

import com.campus.entity.CrowdfundingTier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Crowdfunding Tier Repository
 * Provides data access methods for CrowdfundingTier entity
 */
@Repository
public interface CrowdfundingTierRepository extends BaseRepository<CrowdfundingTier> {

    /**
     * Find crowdfunding tiers by activity ID, ordered by display order
     */
    @Query("SELECT ct FROM CrowdfundingTier ct WHERE ct.activityId = :activityId ORDER BY ct.displayOrder ASC")
    List<CrowdfundingTier> findByActivityIdOrderByDisplayOrder(@Param("activityId") Long activityId);

    /**
     * Find preset tiers by activity ID
     */
    @Query("SELECT ct FROM CrowdfundingTier ct WHERE ct.activityId = :activityId AND ct.isPreset = true ORDER BY ct.displayOrder ASC")
    List<CrowdfundingTier> findPresetTiersByActivityId(@Param("activityId") Long activityId);

    /**
     * Find custom tiers by activity ID
     */
    @Query("SELECT ct FROM CrowdfundingTier ct WHERE ct.activityId = :activityId AND ct.isPreset = false ORDER BY ct.displayOrder ASC")
    List<CrowdfundingTier> findCustomTiersByActivityId(@Param("activityId") Long activityId);

}
