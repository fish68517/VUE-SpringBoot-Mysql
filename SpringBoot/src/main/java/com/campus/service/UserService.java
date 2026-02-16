package com.campus.service;

import com.campus.dto.ChangePasswordRequest;
import com.campus.dto.PageResponse;
import com.campus.dto.UpdateProfileRequest;
import com.campus.dto.UpdateUserStatusRequest;
import com.campus.dto.UserDTO;
import com.campus.entity.User;
import com.campus.exception.BusinessException;
import com.campus.repository.UserRepository;
import com.campus.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * User Service
 * Handles user profile management operations
 */
@Slf4j
@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Get current user profile by username
     * 
     * @param username Username of the user
     * @return UserDTO with user information
     * @throws BusinessException if user not found
     */
    public UserDTO getCurrentUserProfile(String username) {
        log.info("Fetching profile for user: {}", username);

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            log.warn("User not found: {}", username);
            throw new BusinessException(404, "User not found");
        }

        User user = userOptional.get();
        log.debug("Profile fetched successfully for user: {}", username);

        return UserDTO.fromEntity(user);
    }

    /**
     * Update user profile information
     * 
     * @param username Username of the user
     * @param request Update profile request
     * @return Updated UserDTO
     * @throws BusinessException if user not found
     */
    public UserDTO updateUserProfile(String username, UpdateProfileRequest request) {
        log.info("Updating profile for user: {}", username);

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            log.warn("User not found: {}", username);
            throw new BusinessException(404, "User not found");
        }

        User user = userOptional.get();

        // Update profile fields
        if (request.getNickname() != null && !request.getNickname().isEmpty()) {
            user.setNickname(request.getNickname());
        }

        if (request.getAvatarUrl() != null && !request.getAvatarUrl().isEmpty()) {
            user.setAvatarUrl(request.getAvatarUrl());
        }

        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            user.setPhone(request.getPhone());
        }

        if (request.getGender() != null && !request.getGender().isEmpty()) {
            user.setGender(request.getGender());
        }

        if (request.getBirthday() != null) {
            user.setBirthday(request.getBirthday());
        }

        user = userRepository.save(user);
        log.info("Profile updated successfully for user: {}", username);

        return UserDTO.fromEntity(user);
    }

    /**
     * Change user password
     * 
     * @param username Username of the user
     * @param request Change password request
     * @throws BusinessException if user not found, old password is incorrect, or new passwords don't match
     */
    public void changePassword(String username, ChangePasswordRequest request) {
        log.info("Changing password for user: {}", username);

        // Validate new passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            log.warn("New passwords do not match for user: {}", username);
            throw new BusinessException(400, "New passwords do not match");
        }

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            log.warn("User not found: {}", username);
            throw new BusinessException(404, "User not found");
        }

        User user = userOptional.get();

        // Verify old password
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            log.warn("Old password is incorrect for user: {}", username);
            throw new BusinessException(401, "Old password is incorrect");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Password changed successfully for user: {}", username);
    }

    /**
     * Get user by ID
     * 
     * @param userId User ID
     * @return UserDTO with user information
     * @throws BusinessException if user not found
     */
    public UserDTO getUserById(Long userId) {
        log.debug("Fetching user by ID: {}", userId);

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            log.warn("User not found with ID: {}", userId);
            throw new BusinessException(404, "User not found");
        }

        return UserDTO.fromEntity(userOptional.get());
    }

    /**
     * Get all users with pagination
     * 
     * @param pageable Pagination information
     * @return PageResponse with user list
     */
    public PageResponse<UserDTO> getAllUsers(Pageable pageable) {
        log.info("Fetching all users with pagination - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<User> userPage = userRepository.findAll(pageable);
        List<UserDTO> userDTOs = userPage.getContent().stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());

        Page<UserDTO> dtoPage = userPage.map(user -> UserDTO.fromEntity(user));
        log.debug("Fetched {} users", userDTOs.size());

        return PageResponse.fromPage(dtoPage);
    }

    /**
     * Search users by keyword (username or nickname) with pagination
     * 
     * @param keyword Search keyword
     * @param pageable Pagination information
     * @return PageResponse with filtered user list
     */
    public PageResponse<UserDTO> searchUsers(String keyword, Pageable pageable) {
        log.info("Searching users with keyword: {} - page: {}, size: {}", keyword, pageable.getPageNumber(), pageable.getPageSize());

        Page<User> userPage = userRepository.findByKeyword(keyword, pageable);
        Page<UserDTO> dtoPage = userPage.map(UserDTO::fromEntity);

        log.debug("Found {} users matching keyword: {}", userPage.getTotalElements(), keyword);

        return PageResponse.fromPage(dtoPage);
    }

    /**
     * Update user account status
     * 
     * @param userId User ID
     * @param request Update status request
     * @return Updated UserDTO
     * @throws BusinessException if user not found
     */
    public UserDTO updateUserStatus(Long userId, UpdateUserStatusRequest request) {
        log.info("Updating user status for user ID: {} to status: {}", userId, request.getStatus());

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            log.warn("User not found with ID: {}", userId);
            throw new BusinessException(404, "User not found");
        }

        User user = userOptional.get();
        user.setStatus(request.getStatus());
        user = userRepository.save(user);

        log.info("User status updated successfully for user ID: {}", userId);

        return UserDTO.fromEntity(user);
    }

}
