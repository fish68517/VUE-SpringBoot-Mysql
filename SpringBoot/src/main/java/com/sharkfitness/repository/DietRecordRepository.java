package com.sharkfitness.repository;

import com.sharkfitness.entity.DietRecord;
import com.sharkfitness.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DietRecordRepository extends JpaRepository<DietRecord, Long> {
    
    List<DietRecord> findByUserAndMealDateBetweenOrderByMealDateDesc(User user, LocalDate startDate, LocalDate endDate);
    
    List<DietRecord> findByUserAndMealDateOrderByCreatedAtAsc(User user, LocalDate mealDate);
    
    List<DietRecord> findByUserOrderByMealDateDesc(User user);
    
    /**
     * Delete all diet records for a specific user
     */
    void deleteByUserId(Long userId);
}
