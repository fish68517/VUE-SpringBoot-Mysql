package com.sharkfitness.controller;

import com.sharkfitness.entity.User;
import com.sharkfitness.service.CheckInService;
import com.sharkfitness.vo.ApiResponse;
import com.sharkfitness.vo.CheckInStatsVO;
import com.sharkfitness.vo.CheckInVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.List;

/**
 * Controller for check-in operations
 */
@RestController
@RequestMapping("/api/checkins")
@RequiredArgsConstructor
public class CheckInController {
    
    private final CheckInService checkInService;
    
    /**
     * Perform a check-in
     * POST /api/checkins
     */
    @PostMapping
    public ApiResponse<CheckInVO> performCheckIn(HttpServletRequest request) {
        User currentUser = (User) request.getAttribute("currentUser");
        CheckInVO checkIn = checkInService.performCheckIn(currentUser);
        return ApiResponse.success(checkIn);
    }
    
    /**
     * Get check-in history
     * GET /api/checkins
     */
    @GetMapping
    public ApiResponse<List<CheckInVO>> getHistory(HttpServletRequest request) {
        User currentUser = (User) request.getAttribute("currentUser");
        List<CheckInVO> history = checkInService.getHistory(currentUser);
        return ApiResponse.success(history);
    }
    
    /**
     * Get check-in statistics
     * GET /api/checkins/stats
     */
    @GetMapping("/stats")
    public ApiResponse<CheckInStatsVO> getStats(HttpServletRequest request) {
        User currentUser = (User) request.getAttribute("currentUser");
        CheckInStatsVO stats = checkInService.calculateStats(currentUser);
        return ApiResponse.success(stats);
    }
}
