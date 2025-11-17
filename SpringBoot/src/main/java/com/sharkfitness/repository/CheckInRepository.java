package com.sharkfitness.repository;

import com.sharkfitness.entity.CheckIn;
import com.sharkfitness.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for CheckIn entity
 */
@Repository
public interface CheckInRepository extends JpaRepository<CheckIn, Long> {
    
    /**
     * Find check-in by user and date
     */
    Optional<CheckIn> findByUserAndCheckDate(User user, LocalDate checkDate);
    
    /**
     * Find all check-ins for a user ordered by date descending
     */
    List<CheckIn> findByUserOrderByCheckDateDesc(User user);
    
    /**
     * Find all check-ins for a user ordered by date ascending
     */
    List<CheckIn> findByUserOrderByCheckDateAsc(User user);
    
    /**
     * Count total check-ins for a user
     */
    long countByUser(User user);
    
    /**
     * Find check-ins for a user within a date range
     */
    @Query("SELECT c FROM CheckIn c WHERE c.user = :user AND c.checkDate BETWEEN :startDate AND :endDate ORDER BY c.checkDate ASC")
    List<CheckIn> findByUserAndDateRange(@Param("user") User user, 
                                         @Param("startDate") LocalDate startDate, 
                                         @Param("endDate") LocalDate endDate);
    
    /**
     * Find check-ins for a user between two dates
     */
    List<CheckIn> findByUserAndCheckDateBetween(User user, LocalDate startDate, LocalDate endDate);
    
    /**
     * Delete all check-ins for a specific user
     */
    void deleteByUserId(Long userId);
}
