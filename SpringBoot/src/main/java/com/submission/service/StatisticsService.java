package com.submission.service;

import com.submission.mapper.ManuscriptMapper;
import com.submission.mapper.UserMapper;
import com.submission.mapper.ReviewMapper;
import com.submission.vo.StatisticsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Statistics Service - Business logic for statistics operations
 */
@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final ManuscriptMapper manuscriptMapper;
    private final UserMapper userMapper;
    private final ReviewMapper reviewMapper;

    /**
     * Get submission statistics by category
     */
    public StatisticsVO.SubmissionStats getSubmissionStats() {
        StatisticsVO.SubmissionStats stats = new StatisticsVO.SubmissionStats();
        
        // Get total submissions
        long totalSubmissions = manuscriptMapper.countTotalManuscripts();
        stats.setTotalSubmissions(totalSubmissions);
        
        // Get submissions by status
        long submitted = manuscriptMapper.countManuscriptsByStatus("SUBMITTED");
        long underReview = manuscriptMapper.countManuscriptsByStatus("UNDER_REVIEW");
        long revisionRequired = manuscriptMapper.countManuscriptsByStatus("REVISION_REQUIRED");
        long accepted = manuscriptMapper.countManuscriptsByStatus("ACCEPTED");
        long rejected = manuscriptMapper.countManuscriptsByStatus("REJECTED");
        
        stats.setSubmitted(submitted);
        stats.setUnderReview(underReview);
        stats.setRevisionRequired(revisionRequired);
        stats.setAccepted(accepted);
        stats.setRejected(rejected);
        
        // Get submissions by category
        Map<String, Long> submissionsByCategory = manuscriptMapper.countSubmissionsByCategory();
        stats.setSubmissionsByCategory(submissionsByCategory);
        
        return stats;
    }

    /**
     * Get approval rate statistics
     */
    public StatisticsVO.ApprovalRateStats getApprovalRateStats() {
        StatisticsVO.ApprovalRateStats stats = new StatisticsVO.ApprovalRateStats();
        
        // Get total manuscripts that have completed review
        long totalReviewed = manuscriptMapper.countManuscriptsByStatus("ACCEPTED") + 
                            manuscriptMapper.countManuscriptsByStatus("REJECTED");
        
        // Get accepted manuscripts
        long acceptedCount = manuscriptMapper.countManuscriptsByStatus("ACCEPTED");
        
        // Calculate approval rate
        double approvalRate = totalReviewed > 0 ? (double) acceptedCount / totalReviewed * 100 : 0;
        
        stats.setTotalReviewed(totalReviewed);
        stats.setAcceptedCount(acceptedCount);
        stats.setRejectedCount(totalReviewed - acceptedCount);
        stats.setApprovalRate(Math.round(approvalRate * 100.0) / 100.0); // Round to 2 decimal places
        
        // Get approval rate by category
        Map<String, Double> approvalRateByCategory = manuscriptMapper.getApprovalRateByCategory();
        stats.setApprovalRateByCategory(approvalRateByCategory);
        
        return stats;
    }

    /**
     * Get user activity statistics
     */
    public StatisticsVO.UserActivityStats getUserActivityStats() {
        StatisticsVO.UserActivityStats stats = new StatisticsVO.UserActivityStats();
        
        // Get user counts by role
        long authorCount = userMapper.countUsersByRole("AUTHOR");
        long editorCount = userMapper.countUsersByRole("EDITOR");
        long reviewerCount = userMapper.countUsersByRole("REVIEWER");
        long adminCount = userMapper.countUsersByRole("ADMIN");
        
        stats.setAuthorCount(authorCount);
        stats.setEditorCount(editorCount);
        stats.setReviewerCount(reviewerCount);
        stats.setAdminCount(adminCount);
        stats.setTotalUsers(authorCount + editorCount + reviewerCount + adminCount);
        
        // Get active users (those with ACTIVE status)
        long activeUsers = userMapper.countUsersByStatus("ACTIVE");
        stats.setActiveUsers(activeUsers);
        
        // Get pending users
        long pendingUsers = userMapper.countUsersByStatus("PENDING");
        stats.setPendingUsers(pendingUsers);
        
        // Get reviewer acceptance rate (reviewers who accepted tasks vs total assigned)
        long totalReviewTasks = reviewMapper.countTotalReviewTasks();
        long acceptedReviewTasks = reviewMapper.countReviewTasksByStatus("ACCEPTED");
        double reviewerAcceptanceRate = totalReviewTasks > 0 ? 
            (double) acceptedReviewTasks / totalReviewTasks * 100 : 0;
        stats.setReviewerAcceptanceRate(Math.round(reviewerAcceptanceRate * 100.0) / 100.0);
        
        return stats;
    }

    /**
     * Generate comprehensive statistics report
     */
    public StatisticsVO.ComprehensiveReport generateReport() {
        StatisticsVO.ComprehensiveReport report = new StatisticsVO.ComprehensiveReport();
        
        report.setGeneratedAt(new Date());
        report.setSubmissionStats(getSubmissionStats());
        report.setApprovalRateStats(getApprovalRateStats());
        report.setUserActivityStats(getUserActivityStats());
        
        return report;
    }
}
