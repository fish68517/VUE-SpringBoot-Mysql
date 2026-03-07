package com.submission.service;

import com.submission.dto.ReviewDTO;
import com.submission.dto.ManuscriptDTO;
import com.submission.dto.UserDTO;
import com.submission.entity.Review;
import com.submission.entity.Manuscript;
import com.submission.entity.User;
import com.submission.mapper.ReviewMapper;
import com.submission.mapper.ManuscriptMapper;
import com.submission.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Reviewer Service - Business logic for reviewer operations
 */
@Service
@RequiredArgsConstructor
public class ReviewerService {

    private final UserMapper userMapper;
    private final ReviewMapper reviewMapper;
    private final ManuscriptMapper manuscriptMapper;
    private final NotificationService notificationService;

    /**
     * Get reviewer personal information
     */
    public User getReviewerInfo(Long reviewerId) {
        User reviewer = userMapper.findById(reviewerId);
        if (reviewer == null) {
            throw new RuntimeException("Reviewer not found");
        }
        if (!"REVIEWER".equals(reviewer.getRole())) {
            throw new RuntimeException("User is not a reviewer");
        }
        return reviewer;
    }

    /**
     * Update reviewer expertise areas and research directions
     */
    public User updateReviewerExpertise(Long reviewerId, UserDTO userDTO) {
        User reviewer = userMapper.findById(reviewerId);
        if (reviewer == null) {
            throw new RuntimeException("Reviewer not found");
        }
        if (!"REVIEWER".equals(reviewer.getRole())) {
            throw new RuntimeException("User is not a reviewer");
        }

        // Update expertise areas and research directions
        if (userDTO.getExpertiseAreas() != null && !userDTO.getExpertiseAreas().isEmpty()) {
            reviewer.setExpertiseAreas(userDTO.getExpertiseAreas());
        }
        if (userDTO.getResearchDirections() != null && !userDTO.getResearchDirections().isEmpty()) {
            reviewer.setResearchDirections(userDTO.getResearchDirections());
        }

        userMapper.update(reviewer);
        return reviewer;
    }

    /**
     * Update reviewer password
     */
    public void updateReviewerPassword(Long reviewerId, String oldPassword, String newPassword) {
        User reviewer = userMapper.findById(reviewerId);
        if (reviewer == null) {
            throw new RuntimeException("Reviewer not found");
        }
        if (!"REVIEWER".equals(reviewer.getRole())) {
            throw new RuntimeException("User is not a reviewer");
        }

        // Verify old password (plain text comparison as per requirements)
        if (!reviewer.getPassword().equals(oldPassword)) {
            throw new RuntimeException("Old password is incorrect");
        }

        // Update password
        reviewer.setPassword(newPassword);
        userMapper.update(reviewer);
    }

    /**
     * Get review tasks for a reviewer (PENDING status)
     */
    public List<ReviewDTO> getReviewTasks(Long reviewerId) {
        // Validate reviewer exists
        User reviewer = userMapper.findById(reviewerId);
        if (reviewer == null) {
            throw new RuntimeException("Reviewer not found");
        }
        if (!"REVIEWER".equals(reviewer.getRole())) {
            throw new RuntimeException("User is not a reviewer");
        }

        // Get all reviews assigned to this reviewer
        List<Review> reviews = reviewMapper.findByReviewerId(reviewerId);

        // Filter for pending tasks
        return reviews.stream()
                .filter(r -> "PENDING".equals(r.getStatus()))
                .map(this::convertReviewToDTO)
                .collect(Collectors.toList());
    }


    public List<ReviewDTO> getReviewTasksAccepted(Long reviewerId) {
        // Validate reviewer exists
        User reviewer = userMapper.findById(reviewerId);
        if (reviewer == null) {
            throw new RuntimeException("Reviewer not found");
        }
        if (!"REVIEWER".equals(reviewer.getRole())) {
            throw new RuntimeException("User is not a reviewer");
        }

        // Get all reviews assigned to this reviewer
        List<Review> reviews = reviewMapper.findByReviewerId(reviewerId);

        // Filter for pending tasks
        return reviews.stream()
                .filter(r -> "ACCEPTED".equals(r.getStatus()))
                .map(this::convertReviewToDTO)
                .collect(Collectors.toList());
    }

    public List<ReviewDTO> getReviewTasksAll(Long reviewerId) {
        // Validate reviewer exists
        User reviewer = userMapper.findById(reviewerId);
        if (reviewer == null) {
            throw new RuntimeException("Reviewer not found");
        }
        if (!"REVIEWER".equals(reviewer.getRole())) {
            throw new RuntimeException("User is not a reviewer");
        }

        // Get all reviews assigned to this reviewer
        List<Review> reviews = reviewMapper.findByReviewerId(reviewerId);

        // Filter for pending tasks
        return reviews.stream()
                .map(this::convertReviewToDTO)
                .collect(Collectors.toList());
    }

    public List<ReviewDTO> getReviewTasksCommented(Long reviewerId) {
        // Validate reviewer exists
        User reviewer = userMapper.findById(reviewerId);
        if (reviewer == null) {
            throw new RuntimeException("Reviewer not found");
        }
        if (!"REVIEWER".equals(reviewer.getRole())) {
            throw new RuntimeException("User is not a reviewer");
        }

        // Get all reviews assigned to this reviewer
        List<Review> reviews = reviewMapper.findByReviewerId(reviewerId);

        // Filter for 非 pending 和 ACCEPTED  tasks
        Iterator iterator = reviews.iterator();
        while (iterator.hasNext()) {
            Review review = (Review) iterator.next();
            if ("PENDING".equals(review.getStatus()) || "ACCEPTED".equals(review.getStatus())) {
                iterator.remove();
            }
        }
        System.out.println(reviews);
        return reviews.stream().map(this::convertReviewToDTO).collect(Collectors.toList());
    /*    return reviews.stream()
                .filter(r -> "ACCEPTED".equals(r.getStatus()))
                .map(this::convertReviewToDTO)
                .collect(Collectors.toList());*/
    }

    /**
     * Accept a review task
     */
    public Review acceptReviewTask(Long reviewId, Long reviewerId) {
        // Validate review exists
        Review review = reviewMapper.findById(reviewId);
        if (review == null) {
            throw new RuntimeException("Review task not found");
        }

        // Validate reviewer owns this task
        if (!review.getReviewerId().equals(reviewerId)) {
            throw new RuntimeException("This review task does not belong to you");
        }

        // Validate task is in PENDING status
        if (!"PENDING".equals(review.getStatus())) {
            throw new RuntimeException("Review task is not in pending status");
        }

        // Update status to ACCEPTED
        review.setStatus("ACCEPTED");
        reviewMapper.update(review);

        return review;
    }

    /**
     * Reject a review task
     */
    public Review rejectReviewTask(Long reviewId, Long reviewerId) {
        // Validate review exists
        Review review = reviewMapper.findById(reviewId);
        if (review == null) {
            throw new RuntimeException("Review task not found");
        }

        // Validate reviewer owns this task
        if (!review.getReviewerId().equals(reviewerId)) {
            throw new RuntimeException("This review task does not belong to you");
        }

        // Validate task is in PENDING status
        if (!"PENDING".equals(review.getStatus())) {
            throw new RuntimeException("Review task is not in pending status");
        }

        // Update status to REJECTED
        review.setStatus("REJECTED");
        reviewMapper.update(review);

        return review;
    }

    /**
     * Get manuscript content for review
     */
    public ManuscriptDTO getManuscriptForReview(Long manuscriptId, Long reviewerId) {
        // Validate manuscript exists
        Manuscript manuscript = manuscriptMapper.findById(manuscriptId);
        if (manuscript == null) {
            throw new RuntimeException("Manuscript not found");
        }

        // Validate reviewer has a review task for this manuscript
        List<Review> reviews = reviewMapper.findByManuscriptId(manuscriptId);
        boolean hasReviewTask = reviews.stream()
                .anyMatch(r -> r.getReviewerId().equals(reviewerId));

        if (!hasReviewTask) {
            throw new RuntimeException("You do not have a review task for this manuscript");
        }

        // Get author information
        User author = userMapper.findById(manuscript.getAuthorId());
        String authorName = author != null ? author.getUsername() : "Unknown";

        return ManuscriptDTO.builder()
                .id(manuscript.getId())
                .authorId(manuscript.getAuthorId())
                .authorName(authorName)
                .categoryId(manuscript.getCategoryId())
                .title(manuscript.getTitle())
                .abstractText(manuscript.getAbstractText())
                .content(manuscript.getContent())
                .filePath(manuscript.getFilePath())
                .status(manuscript.getStatus())
                .submissionDate(manuscript.getSubmissionDate())
                .createdAt(manuscript.getCreatedAt())
                .updatedAt(manuscript.getUpdatedAt())
                .build();
    }

    /**
     * Submit review opinion
     */
    public Review submitReviewOpinion(Long reviewId, Long reviewerId, String opinion, Integer score, String recommendation) {
        // Validate review exists
        Review review = reviewMapper.findById(reviewId);
        if (review == null) {
            throw new RuntimeException("Review task not found");
        }

        // Validate reviewer owns this task
        if (!review.getReviewerId().equals(reviewerId)) {
            throw new RuntimeException("This review task does not belong to you");
        }

        // Validate review is in ACCEPTED status
        if (!"ACCEPTED".equals(review.getStatus())) {
            throw new RuntimeException("Review task must be accepted before submitting opinion");
        }

        // Validate recommendation
        if (!isValidRecommendation(recommendation)) {
            throw new RuntimeException("Invalid recommendation. Must be ACCEPT, ACCEPT_WITH_REVISION, or REJECT");
        }

        // Validate score
        if (score == null || score < 0 || score > 100) {
            throw new RuntimeException("Score must be between 0 and 100");
        }

        // Update review with opinion
        review.setOpinion(opinion);
        review.setScore(score);
        review.setRecommendation(recommendation);
        review.setStatus("SUBMITTED");
        review.setSubmittedDate(LocalDateTime.now());

        reviewMapper.update(review);

        // Send notification to editor about review submission
        notificationService.sendReviewSubmissionNotification(reviewId, review.getManuscriptId(), reviewerId, review.getEditorId());

        return review;
    }

    /**
     * Validate recommendation value
     */
    private boolean isValidRecommendation(String recommendation) {
        return "ACCEPT".equals(recommendation) || 
               "ACCEPT_WITH_REVISION".equals(recommendation) || 
               "REJECT".equals(recommendation);
    }

    /**
     * Get review history for a reviewer (completed reviews)
     */
    public List<ReviewDTO> getReviewHistory(Long reviewerId) {
        // Validate reviewer exists
        User reviewer = userMapper.findById(reviewerId);
        if (reviewer == null) {
            throw new RuntimeException("Reviewer not found");
        }
        if (!"REVIEWER".equals(reviewer.getRole())) {
            throw new RuntimeException("User is not a reviewer");
        }

        // Get all reviews assigned to this reviewer
        List<Review> reviews = reviewMapper.findByReviewerId(reviewerId);

        // Filter for submitted reviews
        return reviews.stream()
                .filter(r -> "SUBMITTED".equals(r.getStatus()))
                .map(this::convertReviewToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get review opinion details
     */
    public ReviewDTO getReviewOpinionDetails(Long reviewId, Long reviewerId) {
        // Validate review exists
        Review review = reviewMapper.findById(reviewId);
        if (review == null) {
            throw new RuntimeException("Review not found");
        }

        // Validate reviewer owns this review
        if (!review.getReviewerId().equals(reviewerId)) {
            throw new RuntimeException("This review does not belong to you");
        }

        return convertReviewToDTO(review);
    }

    /**
     * Convert Review entity to ReviewDTO
     */
    private ReviewDTO convertReviewToDTO(Review review) {
        User reviewer = userMapper.findById(review.getReviewerId());
        String reviewerName = reviewer != null ? reviewer.getUsername() : "Unknown";

        Manuscript manuscript = manuscriptMapper.findById(review.getManuscriptId());
        String manuscriptTitle = manuscript != null ? manuscript.getTitle() : "Unknown";

        return ReviewDTO.builder()
                .id(review.getId())
                .manuscriptId(review.getManuscriptId())
                .reviewerId(review.getReviewerId())
                .reviewerName(reviewerName)
                .editorId(review.getEditorId())
                .status(review.getStatus())
                .opinion(review.getOpinion())
                .score(review.getScore())
                .recommendation(review.getRecommendation())
                .submittedDate(review.getSubmittedDate())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
