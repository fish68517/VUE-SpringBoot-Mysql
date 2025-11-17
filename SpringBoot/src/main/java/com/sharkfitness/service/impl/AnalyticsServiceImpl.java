package com.sharkfitness.service.impl;

import com.sharkfitness.entity.CheckIn;
import com.sharkfitness.entity.DietRecord;
import com.sharkfitness.entity.TrainingPlan;
import com.sharkfitness.entity.User;
import com.sharkfitness.exception.BusinessException;
import com.sharkfitness.repository.CheckInRepository;
import com.sharkfitness.repository.DietRecordRepository;
import com.sharkfitness.repository.TrainingPlanRepository;
import com.sharkfitness.repository.UserRepository;
import com.sharkfitness.service.AnalyticsService;
import com.sharkfitness.vo.AnalyticsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of AnalyticsService
 */
@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {
    
    private final UserRepository userRepository;
    private final CheckInRepository checkInRepository;
    private final DietRecordRepository dietRecordRepository;
    private final TrainingPlanRepository trainingPlanRepository;
    
    @Override
    public AnalyticsVO calculateStudentAnalytics(Long studentId, int days) {
        // Validate student exists
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new BusinessException(404, "Student not found"));
        
        // Calculate date range
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);
        
        // Calculate check-in metrics
        List<CheckIn> checkIns = checkInRepository.findByUserAndCheckDateBetween(student, startDate, endDate);
        long totalCheckIns = checkIns.size();
        double checkInFrequency = (totalCheckIns * 100.0) / days;
        
        // Calculate diet metrics
        List<DietRecord> dietRecords = dietRecordRepository.findByUserAndMealDateBetweenOrderByMealDateDesc(
                student, startDate, endDate);
        
        double averageCalories = 0.0;
        if (!dietRecords.isEmpty()) {
            int totalCalories = dietRecords.stream()
                    .mapToInt(record -> record.getCalories() != null ? record.getCalories() : 0)
                    .sum();
            averageCalories = (double) totalCalories / days;
        }
        
        // Calculate active days (days with check-ins or diet records)
        Set<LocalDate> activeDates = new HashSet<>();
        checkIns.forEach(checkIn -> activeDates.add(checkIn.getCheckDate()));
        dietRecords.forEach(record -> activeDates.add(record.getMealDate()));
        int activeDays = activeDates.size();
        
        // Calculate training plan completion rate
        double planCompletionRate = calculatePlanCompletionRate(student, startDate, endDate);
        
        return new AnalyticsVO(
                checkInFrequency,
                totalCheckIns,
                averageCalories,
                activeDays,
                planCompletionRate
        );
    }
    
    /**
     * Calculate training plan completion rate
     * Based on plans that have ended within the period and their status
     */
    private double calculatePlanCompletionRate(User student, LocalDate startDate, LocalDate endDate) {
        // Get all training plans for the student
        List<TrainingPlan> allPlans = trainingPlanRepository.findByStudent(student);
        
        if (allPlans.isEmpty()) {
            return 0.0;
        }
        
        // Filter plans that ended within the analysis period
        List<TrainingPlan> relevantPlans = allPlans.stream()
                .filter(plan -> plan.getEndDate() != null)
                .filter(plan -> !plan.getEndDate().isBefore(startDate) && !plan.getEndDate().isAfter(endDate))
                .toList();
        
        // If no plans ended in this period, check active plans
        if (relevantPlans.isEmpty()) {
            // Count active plans as partially complete based on time elapsed
            List<TrainingPlan> activePlans = allPlans.stream()
                    .filter(plan -> "active".equalsIgnoreCase(plan.getStatus()))
                    .filter(plan -> plan.getStartDate() != null && plan.getEndDate() != null)
                    .filter(plan -> !plan.getStartDate().isAfter(endDate))
                    .toList();
            
            if (activePlans.isEmpty()) {
                return 0.0;
            }
            
            // Calculate average progress of active plans
            double totalProgress = 0.0;
            for (TrainingPlan plan : activePlans) {
                LocalDate planStart = plan.getStartDate();
                LocalDate planEnd = plan.getEndDate();
                LocalDate currentDate = LocalDate.now();
                
                // Calculate days elapsed vs total days
                long totalDays = java.time.temporal.ChronoUnit.DAYS.between(planStart, planEnd);
                long elapsedDays = java.time.temporal.ChronoUnit.DAYS.between(planStart, currentDate);
                
                if (totalDays > 0) {
                    double progress = Math.min(100.0, (elapsedDays * 100.0) / totalDays);
                    totalProgress += progress;
                }
            }
            
            return totalProgress / activePlans.size();
        }
        
        // Calculate completion rate based on completed vs total relevant plans
        long completedPlans = relevantPlans.stream()
                .filter(plan -> "completed".equalsIgnoreCase(plan.getStatus()))
                .count();
        
        return (completedPlans * 100.0) / relevantPlans.size();
    }
}
