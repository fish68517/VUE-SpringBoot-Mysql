package com.campus.controller;

import com.campus.annotation.RequireRole;
import com.campus.dto.ActivityStatisticsDTO;
import com.campus.dto.AdminDashboardDTO;
import com.campus.dto.ApiResponse;
import com.campus.dto.CrowdfundingStatisticsDTO;
import com.campus.dto.FeedbackStatisticsDTO;
import com.campus.entity.User;
import com.campus.service.StatisticsService;
import com.campus.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Statistics Controller
 * Handles statistics and data aggregation endpoints for organizers and admins
 */
@Slf4j
@RestController
@RequestMapping("/api/statistics")
@Tag(name = "Statistics", description = "Statistics and data aggregation endpoints")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Get activity statistics for an organizer
     * Calculates registration count, crowdfunding completion rate, and average rating
     * 
     * @param authHeader Authorization header containing JWT token
     * @return ApiResponse with activity statistics
     */
    @GetMapping("/activities")
    @RequireRole({User.UserRole.ORGANIZER, User.UserRole.ADMIN})
    @Operation(summary = "Get activity statistics", description = "Get aggregated activity statistics for the organizer")
    public ResponseEntity<ApiResponse<ActivityStatisticsDTO>> getActivityStatistics(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        log.info("Get activity statistics endpoint called");

        // Extract username from JWT token
        String username = extractUsernameFromToken(authHeader);
        if (username == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Invalid or missing token"));
        }

        ActivityStatisticsDTO statistics = statisticsService.getActivityStatistics(username);

        return ResponseEntity.ok(ApiResponse.success("Activity statistics retrieved successfully", statistics));
    }

    /**
     * Get crowdfunding statistics for an organizer
     * Calculates total crowdfunding amount, completion rate, and supporter count
     * 
     * @param authHeader Authorization header containing JWT token
     * @return ApiResponse with crowdfunding statistics
     */
    @GetMapping("/crowdfunding")
    @RequireRole({User.UserRole.ORGANIZER, User.UserRole.ADMIN})
    @Operation(summary = "Get crowdfunding statistics", description = "Get aggregated crowdfunding statistics for the organizer")
    public ResponseEntity<ApiResponse<CrowdfundingStatisticsDTO>> getCrowdfundingStatistics(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        log.info("Get crowdfunding statistics endpoint called");

        // Extract username from JWT token
        String username = extractUsernameFromToken(authHeader);
        if (username == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Invalid or missing token"));
        }

        CrowdfundingStatisticsDTO statistics = statisticsService.getCrowdfundingStatistics(username);

        return ResponseEntity.ok(ApiResponse.success("Crowdfunding statistics retrieved successfully", statistics));
    }

    /**
     * Get feedback statistics for an organizer
     * Calculates total feedback count, average rating, and rating distribution
     * 
     * @param authHeader Authorization header containing JWT token
     * @return ApiResponse with feedback statistics
     */
    @GetMapping("/feedback")
    @RequireRole({User.UserRole.ORGANIZER, User.UserRole.ADMIN})
    @Operation(summary = "Get feedback statistics", description = "Get aggregated feedback statistics for the organizer")
    public ResponseEntity<ApiResponse<FeedbackStatisticsDTO>> getFeedbackStatistics(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        log.info("Get feedback statistics endpoint called");

        // Extract username from JWT token
        String username = extractUsernameFromToken(authHeader);
        if (username == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Invalid or missing token"));
        }

        FeedbackStatisticsDTO statistics = statisticsService.getFeedbackStatistics(username);

        return ResponseEntity.ok(ApiResponse.success("Feedback statistics retrieved successfully", statistics));
    }

    /**
     * Get admin dashboard data
     * Calculates key metrics for admin dashboard
     * 
     * @return ApiResponse with admin dashboard data
     */
    @GetMapping("/dashboard")
    @RequireRole(User.UserRole.ADMIN)
    @Operation(summary = "Get admin dashboard", description = "Get key metrics for admin dashboard")
    public ResponseEntity<ApiResponse<AdminDashboardDTO>> getAdminDashboard() {
        log.info("Get admin dashboard endpoint called");

        AdminDashboardDTO dashboard = statisticsService.getAdminDashboard();

        return ResponseEntity.ok(ApiResponse.success("Admin dashboard retrieved successfully", dashboard));
    }

    /**
     * Export statistics to Excel
     * Generates an Excel file containing user, activity, and crowdfunding data
     * 
     * @return Excel file as byte array
     */
    @GetMapping("/export")
    @RequireRole(User.UserRole.ADMIN)
    @Operation(summary = "Export statistics", description = "Export statistics data to Excel file")
    public ResponseEntity<byte[]> exportStatistics() {
        log.info("Export statistics endpoint called");

        byte[] excelData = statisticsService.exportStatisticsToExcel();

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=statistics.xlsx")
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(excelData);
    }

    /**
     * Extract username from JWT token in Authorization header
     * 
     * @param authHeader Authorization header
     * @return Username extracted from token, or null if invalid
     */
    private String extractUsernameFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        String token = authHeader.substring(7);

        // Validate token
        if (!jwtUtil.validateToken(token)) {
            return null;
        }

        return jwtUtil.extractUsername(token);
    }

}
