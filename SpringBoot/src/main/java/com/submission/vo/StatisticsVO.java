package com.submission.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

/**
 * Statistics View Objects - Data structures for statistics responses
 */
public class StatisticsVO {

    /**
     * Submission statistics
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SubmissionStats {
        private long totalSubmissions;
        private long submitted;
        private long underReview;
        private long revisionRequired;
        private long accepted;
        private long rejected;
        private Map<String, Long> submissionsByCategory;
    }

    /**
     * Approval rate statistics
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApprovalRateStats {
        private long totalReviewed;
        private long acceptedCount;
        private long rejectedCount;
        private double approvalRate;
        private Map<String, Double> approvalRateByCategory;
    }

    /**
     * User activity statistics
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserActivityStats {
        private long authorCount;
        private long editorCount;
        private long reviewerCount;
        private long adminCount;
        private long totalUsers;
        private long activeUsers;
        private long pendingUsers;
        private double reviewerAcceptanceRate;
    }

    /**
     * Comprehensive statistics report
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ComprehensiveReport {
        private Date generatedAt;
        private SubmissionStats submissionStats;
        private ApprovalRateStats approvalRateStats;
        private UserActivityStats userActivityStats;
    }
}
