package com.submission.controller;

import com.submission.dto.UserDTO;
import com.submission.entity.User;
import com.submission.service.UserService;
import com.submission.vo.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User Controller - Handles user-related operations
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Get users by role
     */
    @GetMapping("/by-role/{role}")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUsersByRole(
            @PathVariable String role,
            HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            List<User> users = userService.getUsersByRole(role);
            List<UserDTO> userDTOs = users.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("Users retrieved", userDTOs));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get user by id
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(
            @PathVariable Long userId,
            HttpSession session) {
        try {
            Long currentUserId = (Long) session.getAttribute("userId");
            if (currentUserId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            User user = userService.getUserById(userId);
            UserDTO userDTO = convertToDTO(user);

            return ResponseEntity.ok(ApiResponse.success("User retrieved", userDTO));
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
                .expertiseAreas(user.getExpertiseAreas())
                .researchDirections(user.getResearchDirections())
                .build();
    }
}
