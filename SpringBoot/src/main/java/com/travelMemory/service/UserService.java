package com.travelMemory.service;

import com.travelMemory.dto.UserResponse;
import com.travelMemory.dto.UpdateUserRequest;
import com.travelMemory.dto.UserStatisticsResponse;
import com.travelMemory.entity.User;
import com.travelMemory.repository.UserRepository;
import com.travelMemory.repository.TravelRecordRepository;
import com.travelMemory.repository.TravelPlanRepository;
import com.travelMemory.repository.LikeRepository;
import com.travelMemory.repository.CommentRepository;
import com.travelMemory.repository.MapFootprintRepository;
import com.travelMemory.repository.MultimediaFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final TravelRecordRepository travelRecordRepository;
    private final TravelPlanRepository travelPlanRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final MapFootprintRepository mapFootprintRepository;
    private final MultimediaFileRepository multimediaFileRepository;

    /**
     * Get user information by ID
     * @param userId the user ID
     * @return UserResponse containing user information
     * @throws IllegalArgumentException if user not found
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return convertToUserResponse(user);
    }

    /**
     * Update user information
     * @param userId the user ID
     * @param updateRequest the update request containing new user information
     * @return UserResponse containing updated user information
     * @throws IllegalArgumentException if user not found or validation fails
     */
    public UserResponse updateUser(Long userId, UpdateUserRequest updateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Update username if provided and not already taken
        if (updateRequest.getUsername() != null && !updateRequest.getUsername().isEmpty()) {
            if (!user.getUsername().equals(updateRequest.getUsername())) {
                if (userRepository.existsByUsername(updateRequest.getUsername())) {
                    throw new IllegalArgumentException("Username already taken");
                }
                user.setUsername(updateRequest.getUsername());
            }
        }

        // Update avatar URL if provided
        if (updateRequest.getAvatarUrl() != null) {
            user.setAvatarUrl(updateRequest.getAvatarUrl());
        }

        // Update bio if provided
        if (updateRequest.getBio() != null) {
            user.setBio(updateRequest.getBio());
        }

        // Save updated user
        User updatedUser = userRepository.save(user);
        return convertToUserResponse(updatedUser);
    }

    /**
     * Get user statistics for dashboard
     * @param userId the user ID
     * @return UserStatisticsResponse containing aggregated statistics
     * @throws IllegalArgumentException if user not found
     */
    @Transactional(readOnly = true)
    public UserStatisticsResponse getUserStatistics(Long userId) {
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found");
        }

        // Get total records count
        Long totalRecords = travelRecordRepository.countByUserId(userId);

        // Get total plans count
        Long totalPlans = travelPlanRepository.countByUserId(userId);

        // Get total likes received on user's public records
        Long totalLikesReceived = likeRepository.countLikesForUserPublicRecords(userId);

        // Get total comments received on user's public records
        Long totalCommentsReceived = commentRepository.countCommentsForUserPublicRecords(userId);

        // Get total footprints count
        Long totalFootprints = mapFootprintRepository.countByUserId(userId);

        // Get total multimedia files count
        Long totalMultimediaFiles = multimediaFileRepository.countByUserId(userId);

        return UserStatisticsResponse.builder()
                .totalRecords(totalRecords)
                .totalPlans(totalPlans)
                .totalLikesReceived(totalLikesReceived)
                .totalCommentsReceived(totalCommentsReceived)
                .totalFootprints(totalFootprints)
                .totalMultimediaFiles(totalMultimediaFiles)
                .build();
    }

    /**
     * Convert User entity to UserResponse DTO
     * @param user the user entity
     * @return UserResponse DTO
     */
    private UserResponse convertToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
