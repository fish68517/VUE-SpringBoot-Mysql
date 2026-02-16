package com.campus.service;

import com.campus.dto.FeedbackDTO;
import com.campus.entity.Activity;
import com.campus.entity.Feedback;
import com.campus.entity.User;
import com.campus.exception.BusinessException;
import com.campus.repository.ActivityRepository;
import com.campus.repository.FeedbackRepository;
import com.campus.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Feedback Service
 * Handles feedback submission and retrieval operations
 */
@Slf4j
@Service
@Transactional
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Submit feedback for an activity
     * Validates activity exists and user is registered for the activity
     * 
     * @param feedbackDTO Feedback data
     * @param username Username of the user submitting feedback
     * @return Created FeedbackDTO
     * @throws BusinessException if validation fails
     */
    public FeedbackDTO submitFeedback(FeedbackDTO feedbackDTO, String username) {
        log.info("Submitting feedback for activity {} by user {}", feedbackDTO.getActivityId(), username);

        // Validate rating
        if (feedbackDTO.getRating() == null || feedbackDTO.getRating() < 1 || feedbackDTO.getRating() > 5) {
            log.warn("Invalid rating: {}", feedbackDTO.getRating());
            throw new BusinessException(400, "Rating must be between 1 and 5");
        }

        // Find user
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            log.warn("User not found: {}", username);
            throw new BusinessException(404, "User not found");
        }

        User user = userOptional.get();

        // Find activity
        Optional<Activity> activityOptional = activityRepository.findById(feedbackDTO.getActivityId());
        if (activityOptional.isEmpty()) {
            log.warn("Activity not found: {}", feedbackDTO.getActivityId());
            throw new BusinessException(404, "Activity not found");
        }

        Activity activity = activityOptional.get();

        // Create feedback
        Feedback feedback = Feedback.builder()
                .activityId(feedbackDTO.getActivityId())
                .userId(user.getId())
                .rating(feedbackDTO.getRating())
                .content(feedbackDTO.getContent())
                .build();

        // Save feedback
        Feedback savedFeedback = feedbackRepository.save(feedback);
        log.info("Feedback submitted successfully for activity {} by user {}", feedbackDTO.getActivityId(), username);

        return FeedbackDTO.fromEntity(savedFeedback);
    }

    /**
     * Get feedback for an activity
     * 
     * @param activityId Activity ID
     * @param page Page number
     * @param size Page size
     * @return Page of FeedbackDTO
     */
    public Page<FeedbackDTO> getActivityFeedback(Long activityId, int page, int size) {
        log.info("Fetching feedback for activity {}", activityId);

        // Validate activity exists
        Optional<Activity> activityOptional = activityRepository.findById(activityId);
        if (activityOptional.isEmpty()) {
            log.warn("Activity not found: {}", activityId);
            throw new BusinessException(404, "Activity not found");
        }

        // Validate pagination
        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 10;

        Pageable pageable = PageRequest.of(page, size);

        // Get feedback
        Page<Feedback> feedbackList = feedbackRepository.findByActivityId(activityId, pageable);

        return feedbackList.map(FeedbackDTO::fromEntity);
    }

    /**
     * Get user's feedback
     * 
     * @param username Username
     * @param page Page number
     * @param size Page size
     * @return Page of FeedbackDTO
     */
    public Page<FeedbackDTO> getUserFeedback(String username, int page, int size) {
        log.info("Fetching feedback for user {}", username);

        // Find user
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            log.warn("User not found: {}", username);
            throw new BusinessException(404, "User not found");
        }

        User user = userOptional.get();

        // Validate pagination
        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 10;

        Pageable pageable = PageRequest.of(page, size);

        // Get feedback
        Page<Feedback> feedbackList = feedbackRepository.findByUserId(user.getId(), pageable);

        return feedbackList.map(FeedbackDTO::fromEntity);
    }

    /**
     * Get average rating for an activity
     * 
     * @param activityId Activity ID
     * @return Average rating, or 0.0 if no feedback
     */
    public Double getAverageRating(Long activityId) {
        log.info("Fetching average rating for activity {}", activityId);

        Double averageRating = feedbackRepository.getAverageRatingByActivityId(activityId);
        return averageRating != null ? averageRating : 0.0;
    }

    /**
     * Reply to feedback (organizer only)
     * 
     * @param feedbackId Feedback ID
     * @param replyContent Reply content
     * @param username Username of the organizer replying
     * @return Updated FeedbackDTO
     * @throws BusinessException if validation fails
     */
    public FeedbackDTO replyToFeedback(Long feedbackId, String replyContent, String username) {
        log.info("Replying to feedback {} by user {}", feedbackId, username);

        // Validate reply content
        if (replyContent == null || replyContent.trim().isEmpty()) {
            log.warn("Reply content is empty");
            throw new BusinessException(400, "Reply content cannot be empty");
        }

        if (replyContent.length() > 1000) {
            log.warn("Reply content exceeds maximum length");
            throw new BusinessException(400, "Reply content cannot exceed 1000 characters");
        }

        // Find feedback
        Optional<Feedback> feedbackOptional = feedbackRepository.findById(feedbackId);
        if (feedbackOptional.isEmpty()) {
            log.warn("Feedback not found: {}", feedbackId);
            throw new BusinessException(404, "Feedback not found");
        }

        Feedback feedback = feedbackOptional.get();

        // Find activity
        Optional<Activity> activityOptional = activityRepository.findById(feedback.getActivityId());
        if (activityOptional.isEmpty()) {
            log.warn("Activity not found: {}", feedback.getActivityId());
            throw new BusinessException(404, "Activity not found");
        }

        Activity activity = activityOptional.get();

        // Verify user is the organizer of the activity
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            log.warn("User not found: {}", username);
            throw new BusinessException(404, "User not found");
        }

        User user = userOptional.get();

        if (!activity.getOrganizerId().equals(user.getId())) {
            log.warn("User {} is not the organizer of activity {}", username, activity.getId());
            throw new BusinessException(403, "Only the activity organizer can reply to feedback");
        }

        // Update feedback with reply
        feedback.setReplyContent(replyContent);
        feedback.setReplyBy(user.getId());

        // Save feedback
        Feedback updatedFeedback = feedbackRepository.save(feedback);
        log.info("Feedback replied successfully - feedback ID: {}", feedbackId);

        return FeedbackDTO.fromEntity(updatedFeedback);
    }

}
