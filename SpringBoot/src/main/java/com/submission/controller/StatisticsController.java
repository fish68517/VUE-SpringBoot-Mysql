package com.submission.controller;

import com.submission.service.StatisticsService;
import com.submission.vo.ApiResponse;
import com.submission.vo.StatisticsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

/**
 * Statistics Controller - Handles statistics-related operations
 */
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * Get submission statistics
     */
    @GetMapping("/submissions")
    public ResponseEntity<ApiResponse<StatisticsVO.SubmissionStats>> getSubmissionStats(HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            StatisticsVO.SubmissionStats stats = statisticsService.getSubmissionStats();
            return ResponseEntity.ok(ApiResponse.success("Submission statistics retrieved", stats));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get approval rate statistics
     */
    @GetMapping("/approval-rate")
    public ResponseEntity<ApiResponse<StatisticsVO.ApprovalRateStats>> getApprovalRateStats(HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            StatisticsVO.ApprovalRateStats stats = statisticsService.getApprovalRateStats();
            return ResponseEntity.ok(ApiResponse.success("Approval rate statistics retrieved", stats));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get user activity statistics
     */
    @GetMapping("/user-activity")
    public ResponseEntity<ApiResponse<StatisticsVO.UserActivityStats>> getUserActivityStats(HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            StatisticsVO.UserActivityStats stats = statisticsService.getUserActivityStats();
            return ResponseEntity.ok(ApiResponse.success("User activity statistics retrieved", stats));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Generate comprehensive statistics report
     */
    @GetMapping("/report")
    public ResponseEntity<ApiResponse<StatisticsVO.ComprehensiveReport>> generateReport(HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            StatisticsVO.ComprehensiveReport report = statisticsService.generateReport();
            return ResponseEntity.ok(ApiResponse.success("Comprehensive report generated", report));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
