package com.postgraduate.service;

import com.postgraduate.dto.AdminUserDTO;
import com.postgraduate.entity.User;
import com.postgraduate.exception.ResourceNotFoundException;
import com.postgraduate.exception.ValidationException;
import com.postgraduate.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Random;

/**
 * Service for handling admin user management operations.
 * Manages user listing, filtering, status updates, and password resets.
 */
@Slf4j
@Service
public class AdminUserService {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Get all users with optional filtering by status and search keyword.
     * Supports pagination with default page size of 20.
     *
     * @param keyword optional search keyword to filter by username
     * @param status optional user status to filter by (ENABLED/DISABLED)
     * @param page page number (0-indexed)
     * @param size page size (default 20, max 100)
     * @return Page of AdminUserDTO containing matching users
     */
    @Transactional(readOnly = true)
    public Page<AdminUserDTO> getAllUsers(String keyword, String status, int page, Integer size) {
        log.info("Getting all users - keyword: {}, status: {}, page: {}, size: {}", keyword, status, page, size);

        // Validate and set page size
        int pageSize = size != null ? Math.min(size, MAX_PAGE_SIZE) : DEFAULT_PAGE_SIZE;
        Pageable pageable = PageRequest.of(page, pageSize);

        Page<User> users;

        // Apply filters based on provided parameters
        if (StringUtils.hasText(keyword) && StringUtils.hasText(status)) {
            // Filter by both keyword and status
            User.UserStatus userStatus = validateAndParseStatus(status);
            users = userRepository.findByStatusAndUsernameContainingIgnoreCaseAndDeletedFalse(
                    userStatus, keyword, pageable);
        } else if (StringUtils.hasText(keyword)) {
            // Filter by keyword only
            users = userRepository.findByUsernameContainingIgnoreCaseAndDeletedFalse(keyword, pageable);
        } else if (StringUtils.hasText(status)) {
            // Filter by status only
            User.UserStatus userStatus = validateAndParseStatus(status);
            users = userRepository.findByStatusAndDeletedFalse(userStatus, pageable);
        } else {
            // No filters, get all users
            users = userRepository.findByDeletedFalse(pageable);
        }

        log.info("Retrieved {} users", users.getNumberOfElements());
        return users.map(AdminUserDTO::fromEntity);
    }

    /**
     * Update user status (enable or disable account).
     *
     * @param userId the ID of the user to update
     * @param status the new status (ENABLED/DISABLED)
     * @return AdminUserDTO containing the updated user information
     * @throws ResourceNotFoundException if user is not found
     * @throws ValidationException if status is invalid
     */
    @Transactional
    public AdminUserDTO updateUserStatus(Long userId, String status) {
        log.info("Updating user status - userId: {}, status: {}", userId, status);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        User.UserStatus userStatus = validateAndParseStatus(status);
        user.setStatus(userStatus);

        user = userRepository.save(user);
        log.info("User status updated successfully - userId: {}, newStatus: {}", userId, status);

        return AdminUserDTO.fromEntity(user);
    }

    /**
     * Reset user password and generate a temporary password.
     * The temporary password should be communicated to the user through a secure channel.
     *
     * @param userId the ID of the user whose password to reset
     * @return the temporary password generated for the user
     * @throws ResourceNotFoundException if user is not found
     */
    @Transactional
    public String resetUserPassword(Long userId) {
        log.info("Resetting user password - userId: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Generate temporary password
        String tempPassword = generateTemporaryPassword();

        // Hash and set the temporary password
        user.setPasswordHash(passwordEncoder.encode(tempPassword));
        userRepository.save(user);

        log.info("User password reset successfully - userId: {}", userId);
        return tempPassword;
    }

    /**
     * Validate and parse user status string to enum.
     *
     * @param status the status string to validate
     * @return the parsed UserStatus enum
     * @throws ValidationException if status is invalid
     */
    private User.UserStatus validateAndParseStatus(String status) {
        try {
            return User.UserStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid status. Must be one of: ENABLED, DISABLED");
        }
    }

    /**
     * Generate a temporary password for password reset.
     * Password is 12 characters long containing uppercase, lowercase, digits, and special characters.
     *
     * @return the generated temporary password
     */
    private String generateTemporaryPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        Random random = new Random();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }

}
