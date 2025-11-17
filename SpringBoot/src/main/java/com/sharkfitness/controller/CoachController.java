package com.sharkfitness.controller;

import com.sharkfitness.entity.User;
import com.sharkfitness.exception.BusinessException;
import com.sharkfitness.repository.CoachStudentRepository;
import com.sharkfitness.service.AnalyticsService;
import com.sharkfitness.service.CheckInService;
import com.sharkfitness.service.DietRecordService;
import com.sharkfitness.vo.AnalyticsVO;
import com.sharkfitness.vo.ApiResponse;
import com.sharkfitness.vo.CheckInVO;
import com.sharkfitness.vo.DietRecordVO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for coach-specific operations
 */
@RestController
@RequestMapping("/api/coaches")
@RequiredArgsConstructor
public class CoachController {
    
    private final CheckInService checkInService;
    private final DietRecordService dietRecordService;
    private final AnalyticsService analyticsService;
    private final CoachStudentRepository coachStudentRepository;
    
    /**
     * Get check-in history for a specific student
     * @param currentUser Current authenticated user (coach)
     * @param studentId Student's user ID
     * @return List of check-ins
     */
    @GetMapping("/students/{studentId}/checkins")
    public ApiResponse<List<CheckInVO>> getStudentCheckIns(
            @RequestAttribute("currentUser") User currentUser,
            @PathVariable Long studentId) {
        
        List<CheckInVO> checkIns = checkInService.getStudentCheckIns(currentUser.getId(), studentId);
        return ApiResponse.success(checkIns);
    }
    
    /**
     * Get diet records for a specific student
     * @param currentUser Current authenticated user (coach)
     * @param studentId Student's user ID
     * @param startDate Start date for filtering (optional)
     * @param endDate End date for filtering (optional)
     * @return List of diet records
     */
    @GetMapping("/students/{studentId}/diet-records")
    public ApiResponse<List<DietRecordVO>> getStudentDietRecords(
            @RequestAttribute("currentUser") User currentUser,
            @PathVariable Long studentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<DietRecordVO> dietRecords = dietRecordService.getStudentDietRecords(
                currentUser.getId(), studentId, startDate, endDate);
        return ApiResponse.success(dietRecords);
    }
    
    /**
     * Get training progress analytics for a specific student
     * @param currentUser Current authenticated user (coach)
     * @param studentId Student's user ID
     * @param days Number of days to analyze (default 30)
     * @return Analytics data
     */
    @GetMapping("/analytics/{studentId}")
    public ApiResponse<AnalyticsVO> getStudentAnalytics(
            @RequestAttribute("currentUser") User currentUser,
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "30") int days) {
        
        // Validate that the coach has access to this student's data
        if (!coachStudentRepository.existsByCoachIdAndStudentId(currentUser.getId(), studentId)) {
            throw new BusinessException(403, "You do not have access to this student's data");
        }
        
        // Validate days parameter
        if (days <= 0 || days > 365) {
            throw new BusinessException(400, "Days parameter must be between 1 and 365");
        }
        
        AnalyticsVO analytics = analyticsService.calculateStudentAnalytics(studentId, days);
        return ApiResponse.success(analytics);
    }
}
