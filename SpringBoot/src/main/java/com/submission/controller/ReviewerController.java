package com.submission.controller;

import com.submission.dto.UserDTO;
import com.submission.dto.ReviewDTO;
import com.submission.dto.ManuscriptDTO;
import com.submission.entity.User;
import com.submission.entity.Review;
import com.submission.service.ReviewerService;
import com.submission.vo.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

/**
 * Reviewer Controller - Handles reviewer-specific operations
 */
@RestController
@RequestMapping("/api/reviewers")
@RequiredArgsConstructor
public class ReviewerController {

    private final ReviewerService reviewerService;

    /**
     * Get reviewer personal information
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserDTO>> getReviewerProfile(HttpSession session) {
        try {
            Long reviewerId = (Long) session.getAttribute("userId");
            if (reviewerId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            User reviewer = reviewerService.getReviewerInfo(reviewerId);
            UserDTO responseDTO = convertToDTO(reviewer);
            return ResponseEntity.ok(ApiResponse.success("Reviewer profile retrieved", responseDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Update reviewer expertise areas and research directions
     */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserDTO>> updateReviewerProfile(
            @RequestBody UserDTO userDTO,
            HttpSession session) {
        try {
            Long reviewerId = (Long) session.getAttribute("userId");
            if (reviewerId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            User reviewer = reviewerService.updateReviewerExpertise(reviewerId, userDTO);
            
            // Update session user information
            session.setAttribute("user", reviewer);
            
            UserDTO responseDTO = convertToDTO(reviewer);
            return ResponseEntity.ok(ApiResponse.success("Reviewer profile updated", responseDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Update reviewer password
     */
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestBody PasswordChangeRequest request,
            HttpSession session) {
        try {
            Long reviewerId = (Long) session.getAttribute("userId");
            if (reviewerId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            reviewerService.updateReviewerPassword(reviewerId, request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get review tasks for the current reviewer
     */
    @GetMapping("/tasks")
    public ResponseEntity<ApiResponse<List<ReviewDTO>>> getReviewTasks(HttpSession session) {
        try {
            Long reviewerId = (Long) session.getAttribute("userId");
            if (reviewerId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            List<ReviewDTO> tasks = reviewerService.getReviewTasks(reviewerId);
            return ResponseEntity.ok(ApiResponse.success("Review tasks retrieved", tasks));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }


    @GetMapping("/tasksAll")
    public ResponseEntity<ApiResponse<List<ReviewDTO>>> getReviewTasksAll(HttpSession session) {
        try {
            Long reviewerId = (Long) session.getAttribute("userId");
            if (reviewerId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            List<ReviewDTO> tasks = reviewerService.getReviewTasksAll(reviewerId);
            return ResponseEntity.ok(ApiResponse.success("Review tasks retrieved", tasks));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }


    @GetMapping("/tasks/accepted-endpoint")
    public ResponseEntity<ApiResponse<List<ReviewDTO>>> getReviewTasksAccepted(HttpSession session) {
        try {
            Long reviewerId = (Long) session.getAttribute("userId");
            if (reviewerId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            List<ReviewDTO> tasks = reviewerService.getReviewTasksAccepted(reviewerId);
            return ResponseEntity.ok(ApiResponse.success("Review tasks retrieved", tasks));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }


    @GetMapping("/tasks/completed-endpoint")
    public ResponseEntity<ApiResponse<List<ReviewDTO>>> getReviewTasksCommented(HttpSession session) {
        try {
            Long reviewerId = (Long) session.getAttribute("userId");
            if (reviewerId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            List<ReviewDTO> tasks = reviewerService.getReviewTasksCommented(reviewerId);
            return ResponseEntity.ok(ApiResponse.success("Review tasks retrieved", tasks));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Accept a review task
     */
    @PostMapping("/tasks/{reviewId}/accept")
    public ResponseEntity<ApiResponse<ReviewDTO>> acceptReviewTask(
            @PathVariable Long reviewId,
            HttpSession session) {
        try {
            Long reviewerId = (Long) session.getAttribute("userId");
            if (reviewerId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            Review review = reviewerService.acceptReviewTask(reviewId, reviewerId);
            ReviewDTO responseDTO = convertReviewToDTO(review);
            return ResponseEntity.ok(ApiResponse.success("Review task accepted", responseDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Reject a review task
     */
    @PostMapping("/tasks/{reviewId}/reject")
    public ResponseEntity<ApiResponse<ReviewDTO>> rejectReviewTask(
            @PathVariable Long reviewId,
            HttpSession session) {
        try {
            Long reviewerId = (Long) session.getAttribute("userId");
            if (reviewerId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            Review review = reviewerService.rejectReviewTask(reviewId, reviewerId);
            ReviewDTO responseDTO = convertReviewToDTO(review);
            return ResponseEntity.ok(ApiResponse.success("Review task rejected", responseDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get manuscript content for review
     */
    @GetMapping("/manuscripts/{manuscriptId}")
    public ResponseEntity<ApiResponse<ManuscriptDTO>> getManuscriptForReview(
            @PathVariable Long manuscriptId,
            HttpSession session) {
        try {
            Long reviewerId = (Long) session.getAttribute("userId");
            if (reviewerId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            ManuscriptDTO manuscript = reviewerService.getManuscriptForReview(manuscriptId, reviewerId);
            return ResponseEntity.ok(ApiResponse.success("Manuscript retrieved", manuscript));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Submit review opinion
     */
    @PostMapping("/reviews/{reviewId}/submit")
    public ResponseEntity<ApiResponse<ReviewDTO>> submitReviewOpinion(
            @PathVariable Long reviewId,
            @RequestBody ReviewOpinionRequest request,
            HttpSession session) {
        try {
            Long reviewerId = (Long) session.getAttribute("userId");
            if (reviewerId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            Review review = reviewerService.submitReviewOpinion(
                    reviewId,
                    reviewerId,
                    request.getOpinion(),
                    request.getScore(),
                    request.getRecommendation()
            );
            ReviewDTO responseDTO = convertReviewToDTO(review);
            return ResponseEntity.ok(ApiResponse.success("Review opinion submitted", responseDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get review history for the current reviewer
     */
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<ReviewDTO>>> getReviewHistory(HttpSession session) {
        try {
            Long reviewerId = (Long) session.getAttribute("userId");
            if (reviewerId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            List<ReviewDTO> history = reviewerService.getReviewHistory(reviewerId);
            return ResponseEntity.ok(ApiResponse.success("Review history retrieved", history));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get review opinion details
     */
    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewDTO>> getReviewOpinionDetails(
            @PathVariable Long reviewId,
            HttpSession session) {
        try {
            Long reviewerId = (Long) session.getAttribute("userId");
            if (reviewerId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            ReviewDTO review = reviewerService.getReviewOpinionDetails(reviewId, reviewerId);
            return ResponseEntity.ok(ApiResponse.success("Review opinion retrieved", review));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Convert User entity to UserDTO
     */
    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .expertiseAreas(user.getExpertiseAreas())
                .researchDirections(user.getResearchDirections())
                .build();
    }

    /**
     * Convert Review entity to ReviewDTO
     */
    private ReviewDTO convertReviewToDTO(Review review) {
        return ReviewDTO.builder()
                .id(review.getId())
                .manuscriptId(review.getManuscriptId())
                .reviewerId(review.getReviewerId())
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
     * Password change request DTO
     */
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class PasswordChangeRequest {
        private String oldPassword;
        private String newPassword;
    }

    /**
     * Review opinion submission request DTO
     */
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ReviewOpinionRequest {
        private String opinion;
        private Integer score;
        private String recommendation;
    }
}
