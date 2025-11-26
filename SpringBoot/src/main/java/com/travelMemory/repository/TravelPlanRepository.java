package com.travelMemory.repository;

import com.travelMemory.entity.TravelPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TravelPlanRepository extends JpaRepository<TravelPlan, Long> {

    /**
     * Find travel plans by user ID with pagination
     * @param userId the user ID
     * @param pageable pagination information
     * @return Page of travel plans
     */
    Page<TravelPlan> findByUserId(Long userId, Pageable pageable);

    /**
     * Find a travel plan by ID and user ID
     * @param id the plan ID
     * @param userId the user ID
     * @return Optional containing the travel plan if found
     */
    Optional<TravelPlan> findByIdAndUserId(Long id, Long userId);

    /**
     * Count travel plans by user ID
     * @param userId the user ID
     * @return the number of travel plans
     */
    long countByUserId(Long userId);
}
