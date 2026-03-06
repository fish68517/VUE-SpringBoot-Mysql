package com.submission.controller;

import com.submission.dto.InitialReviewDTO;
import com.submission.dto.ReviewDTO;
import com.submission.dto.UserDTO;
import com.submission.entity.InitialReview;
import com.submission.entity.User;
import com.submission.service.EditorService;
import com.submission.vo.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Editor Controller - Handles editor-specific operations
 */
@RestController
@RequestMapping("/api/editors")
@RequiredArgsConstructor
public class EditorController {

    private final EditorService editorService;

    /**
     * Get editor personal information
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserDTO>> getEditorProfile(HttpSession session) {
        try {
            Long editorId = (Long) session.getAttribute("userId");
            if (editorId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            User editor = editorService.getEditorInfo(editorId);
            UserDTO responseDTO = convertToDTO(editor);
            return ResponseEntity.ok(ApiResponse.success("Editor profile retrieved", responseDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Update editor personal information
     */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserDTO>> updateEditorProfile(
            @RequestBody UserDTO userDTO,
            HttpSession session) {
        try {
            Long editorId = (Long) session.getAttribute("userId");
            if (editorId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            User editor = editorService.updateEditorInfo(editorId, userDTO);
            
            // Update session user information
            session.setAttribute("user", editor);
            
            UserDTO responseDTO = convertToDTO(editor);
            return ResponseEntity.ok(ApiResponse.success("Editor profile updated", responseDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Update editor password
     */
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestBody PasswordChangeRequest request,
            HttpSession session) {
        try {
            Long editorId = (Long) session.getAttribute("userId");
            if (editorId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            editorService.updateEditorPassword(editorId, request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
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
                .workEmail(user.getWorkEmail())
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
     * Get pending manuscripts for initial review
     */
    @GetMapping("/manuscripts/pending")
    public ResponseEntity<ApiResponse<List<InitialReviewDTO>>> getPendingManuscripts(HttpSession session) {
        try {
            Long editorId = (Long) session.getAttribute("userId");
            if (editorId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            List<InitialReviewDTO> manuscripts = editorService.getPendingManuscriptsWithDetails();
            return ResponseEntity.ok(ApiResponse.success("Pending manuscripts retrieved", manuscripts));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Submit initial review for a manuscript
     */
    @PostMapping("/manuscripts/{manuscriptId}/initial-review")
    public ResponseEntity<ApiResponse<InitialReviewDTO>> submitInitialReview(
            @PathVariable Long manuscriptId,
            @RequestBody InitialReviewRequest request,
            HttpSession session) {
        try {
            Long editorId = (Long) session.getAttribute("userId");
            if (editorId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            InitialReview initialReview = editorService.submitInitialReview(
                    manuscriptId,
                    editorId,
                    request.getStatus(),
                    request.getOpinion()
            );

            InitialReviewDTO responseDTO = convertInitialReviewToDTO(initialReview);
            return ResponseEntity.ok(ApiResponse.success("Initial review submitted successfully", responseDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Convert InitialReview entity to InitialReviewDTO
     */
    private InitialReviewDTO convertInitialReviewToDTO(InitialReview initialReview) {
        return InitialReviewDTO.builder()
                .id(initialReview.getId())
                .manuscriptId(initialReview.getManuscriptId())
                .editorId(initialReview.getEditorId())
                .status(initialReview.getStatus())
                .opinion(initialReview.getOpinion())
                .createdAt(initialReview.getCreatedAt())
                .updatedAt(initialReview.getUpdatedAt())
                .build();
    }

    /**
     * Initial review request DTO
     */
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class InitialReviewRequest {
        private String status; // PASS, REJECT, REVISION_REQUIRED
        private String opinion;
    }

    /**
     * Get all active reviewers
     */
    @GetMapping("/reviewers")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllReviewers(HttpSession session) {
        try {
            Long editorId = (Long) session.getAttribute("userId");
            if (editorId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            List<User> reviewers = editorService.getAllActiveReviewers();
            List<UserDTO> reviewerDTOs = reviewers.stream()
                    .map(this::convertToDTO)
                    .collect(java.util.stream.Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success("Reviewers retrieved", reviewerDTOs));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Assign reviewer to manuscript
     */
    @PostMapping("/manuscripts/{manuscriptId}/assign-reviewer")
    public ResponseEntity<ApiResponse<Void>> assignReviewer(
            @PathVariable Long manuscriptId,
            @RequestBody AssignReviewerRequest request,
            HttpSession session) {
        try {
            Long editorId = (Long) session.getAttribute("userId");
            if (editorId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            editorService.assignReviewer(manuscriptId, request.getReviewerId(), editorId);
            return ResponseEntity.ok(ApiResponse.success("Reviewer assigned successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Assign reviewer request DTO
     */
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class AssignReviewerRequest {
        private Long reviewerId;
    }

    /**
     * Get review progress for a manuscript
     */
    @GetMapping("/manuscripts/{manuscriptId}/review-progress")
    public ResponseEntity<ApiResponse<List<ReviewDTO>>> getReviewProgress(
            @PathVariable Long manuscriptId,
            HttpSession session) {
        try {
            Long editorId = (Long) session.getAttribute("userId");
            if (editorId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            List<ReviewDTO> reviewProgress = editorService.getReviewProgress(manuscriptId);
            return ResponseEntity.ok(ApiResponse.success("Review progress retrieved", reviewProgress));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get review opinions for a manuscript
     */
    @GetMapping("/manuscripts/{manuscriptId}/review-opinions")
    public ResponseEntity<ApiResponse<List<ReviewDTO>>> getReviewOpinions(
            @PathVariable Long manuscriptId,
            HttpSession session) {
        try {
            Long editorId = (Long) session.getAttribute("userId");
            if (editorId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            List<ReviewDTO> reviewOpinions = editorService.getReviewOpinions(manuscriptId);
            return ResponseEntity.ok(ApiResponse.success("Review opinions retrieved", reviewOpinions));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get accepted manuscripts for editing
     */
    @GetMapping("/manuscripts/accepted")
    public ResponseEntity<ApiResponse<List<ManuscriptDTO>>> getAcceptedManuscripts(HttpSession session) {
        try {
            Long editorId = (Long) session.getAttribute("userId");
            if (editorId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            List<com.submission.entity.Manuscript> manuscripts = editorService.getAcceptedManuscripts();
            List<ManuscriptDTO> manuscriptDTOs = manuscripts.stream()
                    .map(this::convertManuscriptToDTO)
                    .collect(java.util.stream.Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success("Accepted manuscripts retrieved", manuscriptDTOs));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Edit manuscript content
     */
    @PutMapping("/manuscripts/{manuscriptId}/edit-content")
    public ResponseEntity<ApiResponse<ManuscriptDTO>> editManuscriptContent(
            @PathVariable Long manuscriptId,
            @RequestBody EditManuscriptRequest request,
            HttpSession session) {
        try {
            Long editorId = (Long) session.getAttribute("userId");
            if (editorId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            com.submission.entity.Manuscript manuscript = editorService.editManuscriptContent(
                    manuscriptId,
                    request.getTitle(),
                    request.getAbstractText(),
                    request.getContent()
            );

            ManuscriptDTO responseDTO = convertManuscriptToDTO(manuscript);
            return ResponseEntity.ok(ApiResponse.success("Manuscript content updated successfully", responseDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Generate acceptance notification for a manuscript
     */
    @PostMapping("/manuscripts/{manuscriptId}/generate-acceptance-notification")
    public ResponseEntity<ApiResponse<String>> generateAcceptanceNotification(
            @PathVariable Long manuscriptId,
            HttpSession session) {
        try {
            Long editorId = (Long) session.getAttribute("userId");
            if (editorId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            String notification = editorService.generateAcceptanceNotification(manuscriptId);
            return ResponseEntity.ok(ApiResponse.success("Acceptance notification generated", notification));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Convert Manuscript entity to ManuscriptDTO
     */
    private ManuscriptDTO convertManuscriptToDTO(com.submission.entity.Manuscript manuscript) {
        return ManuscriptDTO.builder()
                .id(manuscript.getId())
                .authorId(manuscript.getAuthorId())
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
     * Edit manuscript request DTO
     */
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class EditManuscriptRequest {
        private String title;
        private String abstractText;
        private String content;
    }

    /**
     * Manuscript DTO
     */
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    @lombok.Builder
    public static class ManuscriptDTO {
        private Long id;
        private Long authorId;
        private Long categoryId;
        private String title;
        private String abstractText;
        private String content;
        private String filePath;
        private String status;
        private LocalDateTime submissionDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
