package com.submission.service;

import com.submission.dto.InitialReviewDTO;
import com.submission.dto.ReviewDTO;
import com.submission.dto.UserDTO;
import com.submission.entity.InitialReview;
import com.submission.entity.Manuscript;
import com.submission.entity.Review;
import com.submission.entity.User;
import com.submission.mapper.InitialReviewMapper;
import com.submission.mapper.ManuscriptMapper;
import com.submission.mapper.ReviewMapper;
import com.submission.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Editor Service - Business logic for editor operations
 */
@Service
@RequiredArgsConstructor
public class EditorService {

    private final UserMapper userMapper;
    private final ManuscriptMapper manuscriptMapper;
    private final InitialReviewMapper initialReviewMapper;
    private final ReviewMapper reviewMapper;
    private final NotificationService notificationService;

    /**
     * Get editor personal information
     */
    public User getEditorInfo(Long editorId) {
        User editor = userMapper.findById(editorId);
        if (editor == null) {
            throw new RuntimeException("Editor not found");
        }
        if (!"EDITOR".equals(editor.getRole())) {
            throw new RuntimeException("User is not an editor");
        }
        return editor;
    }

    /**
     * Update editor personal information
     */
    public User updateEditorInfo(Long editorId, UserDTO userDTO) {
        User editor = userMapper.findById(editorId);
        if (editor == null) {
            throw new RuntimeException("Editor not found");
        }
        if (!"EDITOR".equals(editor.getRole())) {
            throw new RuntimeException("User is not an editor");
        }

        // Validate work email format if provided
        if (userDTO.getWorkEmail() != null && !userDTO.getWorkEmail().isEmpty()) {
            if (!isValidEmail(userDTO.getWorkEmail())) {
                throw new RuntimeException("Invalid work email format");
            }
        }

        // Update only allowed fields for editor
        editor.setEmail(userDTO.getEmail());
        editor.setPhone(userDTO.getPhone());
        editor.setWorkEmail(userDTO.getWorkEmail());

        userMapper.update(editor);
        return editor;
    }

    /**
     * Update editor password
     */
    public void updateEditorPassword(Long editorId, String oldPassword, String newPassword) {
        User editor = userMapper.findById(editorId);
        if (editor == null) {
            throw new RuntimeException("Editor not found");
        }
        if (!"EDITOR".equals(editor.getRole())) {
            throw new RuntimeException("User is not an editor");
        }

        // Verify old password (plain text comparison as per requirements)
        if (!editor.getPassword().equals(oldPassword)) {
            throw new RuntimeException("Old password is incorrect");
        }

        // Update password
        editor.setPassword(newPassword);
        userMapper.update(editor);
    }

    /**
     * Validate email format
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    /**
     * Get pending manuscripts for initial review
     */
    public List<Manuscript> getPendingManuscripts() {
        return manuscriptMapper.findByStatus("SUBMITTED");
    }

    /**
     * Get pending manuscripts for initial review with details
     */
    public List<InitialReviewDTO> getPendingManuscriptsWithDetails() {
        List<Manuscript> manuscripts = getPendingManuscripts();
        return manuscripts.stream()
                .map(m -> {
                    InitialReview review = initialReviewMapper.findByManuscriptId(m.getId());
                    if (review != null) {
                        return convertInitialReviewToDTO(review);
                    }
                    // Return manuscript info without review if not yet reviewed
                    return InitialReviewDTO.builder()
                            .manuscriptId(m.getId())
                            .status("PENDING")
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * Submit initial review for a manuscript
     */
    public InitialReview submitInitialReview(Long manuscriptId, Long editorId, String status, String opinion) {
        // Validate manuscript exists
        Manuscript manuscript = manuscriptMapper.findById(manuscriptId);
        if (manuscript == null) {
            throw new RuntimeException("Manuscript not found");
        }

        // Validate status
        if (!isValidInitialReviewStatus(status)) {
            throw new RuntimeException("Invalid initial review status. Must be PASS, REJECT, or REVISION_REQUIRED");
        }

        // Check if initial review already exists
        InitialReview existingReview = initialReviewMapper.findByManuscriptId(manuscriptId);
        InitialReview initialReview;
        if (existingReview != null) {
            // Update existing review
            existingReview.setStatus(status);
            existingReview.setOpinion(opinion);
            initialReviewMapper.update(existingReview);
            initialReview = existingReview;
            // return existingReview;
        } else {
            // Create new initial review
            initialReview = InitialReview.builder()
                    .manuscriptId(manuscriptId)
                    .editorId(editorId)
                    .status(status)
                    .opinion(opinion)
                    .build();

            initialReviewMapper.insert(initialReview);


        }


        // Update manuscript status based on review result
        String oldStatus = manuscript.getStatus();
        System.out.println("Old status: " + oldStatus);
        System.out.println(" status: " + oldStatus);
        updateManuscriptStatusAfterInitialReview(manuscript, status);

        // Send notification to author about initial review result
        notificationService.sendInitialReviewNotification(manuscriptId, status, opinion);

        return initialReview;
    }

    /**
     * Update manuscript status after initial review
     */
    private void updateManuscriptStatusAfterInitialReview(Manuscript manuscript, String reviewStatus) {
        String newStatus;
        switch (reviewStatus) {
            case "PASS":
                newStatus = "UNDER_REVIEW";
                break;
            case "REJECT":
                newStatus = "REJECTED";
                break;
            case "REVISION_REQUIRED":
                newStatus = "REVISION_REQUIRED";
                break;
            default:
                return;
        }

        manuscript.setStatus(newStatus);
        manuscriptMapper.update(manuscript);
    }

    /**
     * Validate initial review status
     */
    private boolean isValidInitialReviewStatus(String status) {
        return "PASS".equals(status) || "REJECT".equals(status) || "REVISION_REQUIRED".equals(status);
    }

    /**
     * Convert InitialReview entity to InitialReviewDTO
     */
    private InitialReviewDTO convertInitialReviewToDTO(InitialReview initialReview) {
        User editor = userMapper.findById(initialReview.getEditorId());
        String editorName = editor != null ? editor.getUsername() : "Unknown";

        return InitialReviewDTO.builder()
                .id(initialReview.getId())
                .manuscriptId(initialReview.getManuscriptId())
                .editorId(initialReview.getEditorId())
                .editorName(editorName)
                .status(initialReview.getStatus())
                .opinion(initialReview.getOpinion())
                .createdAt(initialReview.getCreatedAt())
                .updatedAt(initialReview.getUpdatedAt())
                .build();
    }

    /**
     * Get all active reviewers
     */
    public List<User> getAllActiveReviewers() {
        return userMapper.findAllActiveReviewers();
    }

    /**
     * Assign reviewer to manuscript
     */
    public void assignReviewer(Long manuscriptId, Long reviewerId, Long editorId) {
        // Validate manuscript exists
        Manuscript manuscript = manuscriptMapper.findById(manuscriptId);
        if (manuscript == null) {
            throw new RuntimeException("Manuscript not found");
        }

        // Validate reviewer exists and is active
        User reviewer = userMapper.findById(reviewerId);
        if (reviewer == null) {
            throw new RuntimeException("Reviewer not found");
        }
        if (!"REVIEWER".equals(reviewer.getRole())) {
            throw new RuntimeException("User is not a reviewer");
        }
        if (!"ACTIVE".equals(reviewer.getStatus())) {
            throw new RuntimeException("Reviewer is not active");
        }

        // Check if review already exists for this manuscript and reviewer
        List<Review> existingReviews = reviewMapper.findByManuscriptId(manuscriptId);
        for (Review review : existingReviews) {
            if (review.getReviewerId().equals(reviewerId)) {
                throw new RuntimeException("This reviewer is already assigned to this manuscript");
            }
        }

        // Create new review assignment
        Review review = Review.builder()
                .manuscriptId(manuscriptId)
                .reviewerId(reviewerId)
                .editorId(editorId)
                .status("PENDING")
                .build();

        reviewMapper.insert(review);

        // Send notification to reviewer about assignment
        notificationService.sendReviewerAssignmentNotification(reviewerId, manuscriptId, editorId);
    }

    /**
     * Get review progress for a manuscript
     */
    public List<com.submission.dto.ReviewDTO> getReviewProgress(Long manuscriptId) {
        // Validate manuscript exists
        Manuscript manuscript = manuscriptMapper.findById(manuscriptId);
        if (manuscript == null) {
            throw new RuntimeException("Manuscript not found");
        }

        // Get all reviews for this manuscript
        List<Review> reviews = reviewMapper.findByManuscriptId(manuscriptId);
        
        return reviews.stream()
                .map(this::convertReviewToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get review opinions for a manuscript
     */
    public List<com.submission.dto.ReviewDTO> getReviewOpinions(Long manuscriptId) {
        // Validate manuscript exists
        Manuscript manuscript = manuscriptMapper.findById(manuscriptId);
        if (manuscript == null) {
            throw new RuntimeException("Manuscript not found");
        }

        // Get all submitted reviews for this manuscript
        List<Review> reviews = reviewMapper.findByManuscriptId(manuscriptId);
        
        return reviews.stream()
                .filter(r -> "SUBMITTED".equals(r.getStatus()))
                .map(this::convertReviewToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert Review entity to ReviewDTO
     */
    private com.submission.dto.ReviewDTO convertReviewToDTO(Review review) {
        User reviewer = userMapper.findById(review.getReviewerId());
        String reviewerName = reviewer != null ? reviewer.getUsername() : "Unknown";

        return com.submission.dto.ReviewDTO.builder()
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

    /**
     * Get manuscripts that have passed final review (ACCEPTED status)
     */
    public List<Manuscript> getAcceptedManuscripts() {
        return manuscriptMapper.findByStatus("ACCEPTED");
    }

    /**
     * Edit manuscript content
     */
    public Manuscript editManuscriptContent(Long manuscriptId, String title, String abstractText, String content) {
        // Validate manuscript exists
        Manuscript manuscript = manuscriptMapper.findById(manuscriptId);
        if (manuscript == null) {
            throw new RuntimeException("Manuscript not found");
        }

        // Validate manuscript is in ACCEPTED status
        if (!"ACCEPTED".equals(manuscript.getStatus())) {
            throw new RuntimeException("Only accepted manuscripts can be edited");
        }

        // Update manuscript content
        manuscript.setTitle(title);
        manuscript.setAbstractText(abstractText);
        manuscript.setContent(content);
        manuscript.setUpdatedAt(LocalDateTime.now());

        manuscriptMapper.update(manuscript);
        return manuscript;
    }

    /**
     * Generate acceptance notification for a manuscript
     */
    public String generateAcceptanceNotification(Long manuscriptId) {
        // Validate manuscript exists
        Manuscript manuscript = manuscriptMapper.findById(manuscriptId);
        if (manuscript == null) {
            throw new RuntimeException("Manuscript not found");
        }

        // Validate manuscript is in ACCEPTED status
        if (!"ACCEPTED".equals(manuscript.getStatus())) {
            throw new RuntimeException("Only accepted manuscripts can generate acceptance notification");
        }

        // Get author information
        User author = userMapper.findById(manuscript.getAuthorId());
        if (author == null) {
            throw new RuntimeException("Author not found");
        }

        // Generate acceptance notification content
        String notification = generateNotificationContent(manuscript, author);
        return notification;
    }

    /**
     * Generate notification content
     */
    private String generateNotificationContent(Manuscript manuscript, User author) {
        StringBuilder sb = new StringBuilder();
        sb.append("尊敬的 ").append(author.getUsername()).append("：\n\n");
        sb.append("感谢您向本刊投稿。\n\n");
        sb.append("经过我们的编辑和审稿专家的认真评审，您的稿件《").append(manuscript.getTitle()).append("》已被我们接受。\n\n");
        sb.append("稿件编号：").append(manuscript.getId()).append("\n");
        sb.append("投稿日期：").append(manuscript.getSubmissionDate()).append("\n");
        sb.append("接受日期：").append(LocalDateTime.now()).append("\n\n");
        sb.append("我们将尽快安排您的稿件出版。如有任何问题，欢迎与我们联系。\n\n");
        sb.append("此致\n敬礼\n\n");
        sb.append("编辑部");
        return sb.toString();
    }
}
