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

/**
 * Authentication Controller - Handles user registration, login, and logout
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * User registration endpoint
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> register(@RequestBody UserDTO userDTO) {
        try {
            User user = userService.register(userDTO);
            UserDTO responseDTO = convertToDTO(user);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("User registered successfully", responseDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * User login endpoint
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserDTO>> login(@RequestBody UserDTO userDTO, HttpSession session) {
        try {
            User user = userService.login(userDTO.getUsername(), userDTO.getPassword());
            
            // Store user information in session
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("role", user.getRole());
            session.setAttribute("user", user);
            
            UserDTO responseDTO = convertToDTO(user);
            return ResponseEntity.ok(ApiResponse.success("Login successful", responseDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * User logout endpoint
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(ApiResponse.success("Logout successful", null));
    }

    /**
     * Get current user information
     */
    @GetMapping("/current-user")
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("User not logged in"));
        }
        
        UserDTO responseDTO = convertToDTO(user);
        return ResponseEntity.ok(ApiResponse.success("Current user retrieved", responseDTO));
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
                .workEmail(user.getWorkEmail())
                .expertiseAreas(user.getExpertiseAreas())
                .researchDirections(user.getResearchDirections())
                .build();
    }
}
