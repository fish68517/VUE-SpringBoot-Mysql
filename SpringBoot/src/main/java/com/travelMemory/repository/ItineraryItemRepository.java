package com.travelMemory.repository;

import com.travelMemory.entity.ItineraryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ItineraryItemRepository extends JpaRepository<ItineraryItem, Long> {

    /**
     * Find all itinerary items for a specific plan
     * @param planId the plan ID
     * @return list of itinerary items
     */
    List<ItineraryItem> findByPlanIdOrderByItemDate(Long planId);

    /**
     * Find an itinerary item by ID and plan ID
     * @param id the item ID
     * @param planId the plan ID
     * @return optional containing the item if found
     */
    Optional<ItineraryItem> findByIdAndPlanId(Long id, Long planId);

    /**
     * Find itinerary items for a plan within a date range
     * @param planId the plan ID
     * @param startDate the start date
     * @param endDate the end date
     * @return list of itinerary items
     */
    List<ItineraryItem> findByPlanIdAndItemDateBetweenOrderByItemDate(Long planId, LocalDate startDate, LocalDate endDate);

    /**
     * Delete all itinerary items for a specific plan
     * @param planId the plan ID
     */
    void deleteByPlanId(Long planId);
}
