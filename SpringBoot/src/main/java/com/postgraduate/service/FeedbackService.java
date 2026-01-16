package com.postgraduate.service;

import com.postgraduate.dto.FeedbackDTO;
import com.postgraduate.entity.Feedback;
import com.postgraduate.entity.User;
import com.postgraduate.exception.ResourceNotFoundException;
import com.postgraduate.repository.FeedbackRepository;
import com.postgraduate.repository.UserRepository;
import com.postgraduate.util.AuthorizationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service for handling feedback management operations.
 * Provides methods for submitting feedback, retrieving user feedback history, and admin feedback management.
 */
@Slf4j
@Service
public class FeedbackService {

    private static final int DEFAULT_PAGE_SIZE = 20;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorizationUtil authorizationUtil;

    /**
     * Submit new feedback from the current user.
     * Sets initial status to NEW.
     *
     * @param type the feedback type (BUG/SUGGESTION/DATA_ERROR)
     * @param content the feedback content
     * @return FeedbackDTO containing the submitted feedback information
     */
    @Transactional
    public FeedbackDTO submitFeedback(String type, String content) {
        log.info("Submitting feedback with type: {}", type);

        User currentUser = authorizationUtil.getCurrentUser();

        // Parse and validate feedback type
        Feedback.FeedbackType feedbackType;
        try {
            feedbackType = Feedback.FeedbackType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Invalid feedback type: {}", type);
            throw new IllegalArgumentException("Invalid feedback type: " + type);
        }

        // Create new feedback with NEW status
        Feedback feedback = Feedback.builder()
                .userId(currentUser.getId())
                .type(feedbackType)
                .content(content)
                .status(Feedback.FeedbackStatus.NEW)
                .build();

        Feedback savedFeedback = feedbackRepository.save(feedback);
        log.info("Feedback submitted successfully with id: {} by user {}", savedFeedback.getId(), currentUser.getId());

        return FeedbackDTO.fromEntityWithAuthor(savedFeedback, currentUser.getUsername());
    }

    /**
     * Get all feedback submitted by the current user with pagination.
     *
     * @param page page number (0-indexed)
     * @param size page size (default 20 if not specified or exceeds max)
     * @return Page of FeedbackDTO containing user's feedback
     */
    @Transactional(readOnly = true)
    public Page<FeedbackDTO> getUserFeedback(int page, Integer size) {
        log.info("Retrieving feedback for current user - page: {}, size: {}", page, size);

        User currentUser = authorizationUtil.getCurrentUser();

        // Validate and set page size
        int pageSize = (size == null || size <= 0 || size > 100) ? DEFAULT_PAGE_SIZE : size;
        Pageable pageable = PageRequest.of(page, pageSize);

        Page<Feedback> feedbackPage = feedbackRepository.findByUserIdAndDeletedFalse(currentUser.getId(), pageable);
        log.info("Found {} feedback entries for user {}", feedbackPage.getTotalElements(), currentUser.getId());

        return feedbackPage.map(feedback -> FeedbackDTO.fromEntityWithAuthor(feedback, currentUser.getUsername()));
    }

    /**
     * Get all feedback with optional filtering by status and type for admin management.
     * Supports pagination and filtering.
     *
     * @param status the feedback status to filter by (NEW/PROCESSING/DONE), null for no filtering
     * @param type the feedback type to filter by (BUG/SUGGESTION/DATA_ERROR), null for no filtering
     * @param page page number (0-indexed)
     * @param size page size (default 20 if not specified or exceeds max)
     * @return Page of FeedbackDTO matching the criteria
     */
    @Transactional(readOnly = true)
    public Page<FeedbackDTO> getAdminFeedback(String status, String type, int page, Integer size) {
        log.info("Retrieving feedback for admin - status: {}, type: {}, page: {}, size: {}", status, type, page, size);

        // Parse status if provided
        Feedback.FeedbackStatus feedbackStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                feedbackStatus = Feedback.FeedbackStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.error("Invalid feedback status: {}", status);
                throw new IllegalArgumentException("Invalid feedback status: " + status);
            }
        }

        // Parse type if provided
        Feedback.FeedbackType feedbackType = null;
        if (type != null && !type.isEmpty()) {
            try {
                feedbackType = Feedback.FeedbackType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.error("Invalid feedback type: {}", type);
                throw new IllegalArgumentException("Invalid feedback type: " + type);
            }
        }

        // Validate and set page size
        int pageSize = (size == null || size <= 0 || size > 100) ? DEFAULT_PAGE_SIZE : size;
        Pageable pageable = PageRequest.of(page, pageSize);

        Page<Feedback> feedbackPage = feedbackRepository.findAllWithFilters(feedbackStatus, feedbackType, pageable);
        log.info("Found {} feedback entries matching filters", feedbackPage.getTotalElements());

        return feedbackPage.map(feedback -> {
            String authorUsername = feedback.getUser() != null ? feedback.getUser().getUsername() : "Unknown";
            return FeedbackDTO.fromEntityWithAuthor(feedback, authorUsername);
        });
    }

    /**
     * Reply to feedback and update its status.
     * Sets the repliedAt timestamp to current time.
     *
     * @param feedbackId the ID of the feedback to reply to
     * @param status the new status (NEW/PROCESSING/DONE)
     * @param adminReply the admin's reply message
     * @return FeedbackDTO containing the updated feedback
     * @throws ResourceNotFoundException if feedback is not found
     */
    @Transactional
    public FeedbackDTO replyToFeedback(Long feedbackId, String status, String adminReply) {
        log.info("Admin replying to feedback id: {} with status: {}", feedbackId, status);

        Feedback feedback = feedbackRepository.findByIdAndDeletedFalse(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found with id: " + feedbackId));

        // Parse and validate status
        Feedback.FeedbackStatus feedbackStatus;
        try {
            feedbackStatus = Feedback.FeedbackStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Invalid feedback status: {}", status);
            throw new IllegalArgumentException("Invalid feedback status: " + status);
        }

        // Update feedback with admin reply and status
        feedback.setStatus(feedbackStatus);
        feedback.setAdminReply(adminReply);
        feedback.setRepliedAt(LocalDateTime.now());

        Feedback updatedFeedback = feedbackRepository.save(feedback);
        log.info("Feedback id: {} updated successfully with status: {}", feedbackId, status);

        String authorUsername = updatedFeedback.getUser() != null ? updatedFeedback.getUser().getUsername() : "Unknown";
        return FeedbackDTO.fromEntityWithAuthor(updatedFeedback, authorUsername);
    }

}
