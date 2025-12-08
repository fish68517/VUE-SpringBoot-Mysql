package com.sharkfitness.repository;


import com.sharkfitness.entity.TrainingFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingFeedbackRepository extends JpaRepository<TrainingFeedback, Long> {

    /**
     * Finds all feedback entries for a given training plan ID,
     * ordered by feedback date in descending order (most recent first).
     *
     * @param planId The ID of the training plan.
     * @return A list of TrainingFeedback entities.
     */
    List<TrainingFeedback> findByPlanIdOrderByFeedbackDateDesc(Long planId);
}
