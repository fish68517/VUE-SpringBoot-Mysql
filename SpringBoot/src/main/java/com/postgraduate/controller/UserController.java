package com.postgraduate.controller;

import com.postgraduate.dto.ApiResponse;
import com.postgraduate.dto.UserProfileDTO;
import com.postgraduate.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for user profile endpoints.
 * Handles user profile retrieval and update operations.
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Get the current authenticated user's profile.
     *
     * @return ResponseEntity with ApiResponse containing the user's profile
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserProfileDTO>> getUserProfile() {
        log.info("Get user profile endpoint called");
        UserProfileDTO profileDTO = userService.getUserProfile();
        return ResponseEntity.ok(ApiResponse.success("User profile retrieved successfully", profileDTO));
    }

    /**
     * Update the current authenticated user's profile.
     *
     * @param profileDTO the updated profile information
     * @return ResponseEntity with ApiResponse containing the updated profile
     */
    @PutMapping("/me/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserProfileDTO>> updateUserProfile(
            @Valid @RequestBody UserProfileDTO profileDTO) {
        log.info("Update user profile endpoint called");
        UserProfileDTO updatedProfile = userService.updateUserProfile(profileDTO);
        return ResponseEntity.ok(ApiResponse.success("User profile updated successfully", updatedProfile));
    }

}
