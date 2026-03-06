package com.submission.controller;

import com.submission.dto.UserDTO;
import com.submission.entity.User;
import com.submission.service.AdminService;
import com.submission.service.UserManagementService;
import com.submission.vo.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

/**
 * Admin Controller - Handles admin-specific operations
 */
@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final UserManagementService userManagementService;

    /**
     * Get admin personal information
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserDTO>> getAdminProfile(HttpSession session) {
        try {
            Long adminId = (Long) session.getAttribute("userId");
            if (adminId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            User admin = adminService.getAdminInfo(adminId);
            UserDTO responseDTO = convertToDTO(admin);
            return ResponseEntity.ok(ApiResponse.success("Admin profile retrieved", responseDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Update admin personal information
     */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserDTO>> updateAdminProfile(
            @RequestBody UserDTO userDTO,
            HttpSession session) {
        try {
            Long adminId = (Long) session.getAttribute("userId");
            if (adminId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            User admin = adminService.updateAdminInfo(adminId, userDTO);
            
            // Update session user information
            session.setAttribute("user", admin);
            
            UserDTO responseDTO = convertToDTO(admin);
            return ResponseEntity.ok(ApiResponse.success("Admin profile updated", responseDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Update admin password
     */
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestBody PasswordChangeRequest request,
            HttpSession session) {
        try {
            Long adminId = (Long) session.getAttribute("userId");
            if (adminId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            adminService.updateAdminPassword(adminId, request.getOldPassword(), request.getNewPassword());
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
     * Get pending users awaiting approval
     */
    @GetMapping("/users/pending")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getPendingUsers(HttpSession session) {
        try {
            Long adminId = (Long) session.getAttribute("userId");
            if (adminId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            List<UserDTO> pendingUsers = userManagementService.getPendingUsers();
            return ResponseEntity.ok(ApiResponse.success("Pending users retrieved", pendingUsers));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Approve user registration
     */
    @PostMapping("/users/{userId}/approve")
    public ResponseEntity<ApiResponse<UserDTO>> approveUser(
            @PathVariable Long userId,
            HttpSession session) {
        try {
            Long adminId = (Long) session.getAttribute("userId");
            if (adminId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            UserDTO approvedUser = userManagementService.approveUser(userId);
            return ResponseEntity.ok(ApiResponse.success("User approved successfully", approvedUser));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Reject user registration
     */
    @PostMapping("/users/{userId}/reject")
    public ResponseEntity<ApiResponse<UserDTO>> rejectUser(
            @PathVariable Long userId,
            HttpSession session) {
        try {
            Long adminId = (Long) session.getAttribute("userId");
            if (adminId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            UserDTO rejectedUser = userManagementService.rejectUser(userId);
            return ResponseEntity.ok(ApiResponse.success("User rejected successfully", rejectedUser));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get all users with optional filtering
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            HttpSession session) {
        try {
            Long adminId = (Long) session.getAttribute("userId");
            if (adminId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            List<UserDTO> users = userManagementService.getAllUsers(username, email, role, status);
            return ResponseEntity.ok(ApiResponse.success("Users retrieved", users));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Update user information
     */
    @PutMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(
            @PathVariable Long userId,
            @RequestBody UserDTO userDTO,
            HttpSession session) {
        try {
            Long adminId = (Long) session.getAttribute("userId");
            if (adminId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            UserDTO updatedUser = userManagementService.updateUser(userId, userDTO);
            return ResponseEntity.ok(ApiResponse.success("User updated successfully", updatedUser));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Delete user
     */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable Long userId,
            HttpSession session) {
        try {
            Long adminId = (Long) session.getAttribute("userId");
            if (adminId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            userManagementService.deleteUser(userId);
            return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Assign user role/permission
     */
    @PostMapping("/users/{userId}/assign-role")
    public ResponseEntity<ApiResponse<UserDTO>> assignUserRole(
            @PathVariable Long userId,
            @RequestBody RoleAssignmentRequest request,
            HttpSession session) {
        try {
            Long adminId = (Long) session.getAttribute("userId");
            if (adminId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            UserDTO updatedUser = userManagementService.assignUserRole(userId, request.getRole());
            return ResponseEntity.ok(ApiResponse.success("User role assigned successfully", updatedUser));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Role assignment request DTO
     */
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class RoleAssignmentRequest {
        private String role;
    }
}
