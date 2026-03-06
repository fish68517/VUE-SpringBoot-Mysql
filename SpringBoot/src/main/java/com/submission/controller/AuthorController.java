package com.submission.controller;

import com.submission.dto.UserDTO;
import com.submission.entity.User;
import com.submission.service.AuthorService;
import com.submission.vo.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

/**
 * Author Controller - Handles author-specific operations
 */
@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    /**
     * Get author personal information
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserDTO>> getAuthorProfile(HttpSession session) {
        try {
            Long authorId = (Long) session.getAttribute("userId");
            if (authorId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            User author = authorService.getAuthorInfo(authorId);
            UserDTO responseDTO = convertToDTO(author);
            return ResponseEntity.ok(ApiResponse.success("Author profile retrieved", responseDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Update author personal information
     */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserDTO>> updateAuthorProfile(
            @RequestBody UserDTO userDTO,
            HttpSession session) {
        try {
            Long authorId = (Long) session.getAttribute("userId");
            if (authorId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            User author = authorService.updateAuthorInfo(authorId, userDTO);
            
            // Update session user information
            session.setAttribute("user", author);
            
            UserDTO responseDTO = convertToDTO(author);
            return ResponseEntity.ok(ApiResponse.success("Author profile updated", responseDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Update author password
     */
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestBody PasswordChangeRequest request,
            HttpSession session) {
        try {
            Long authorId = (Long) session.getAttribute("userId");
            if (authorId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            authorService.updateAuthorPassword(authorId, request.getOldPassword(), request.getNewPassword());
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
                .academicAchievements(user.getAcademicAchievements())
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
}
