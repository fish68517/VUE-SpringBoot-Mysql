package com.campus.service;

import com.campus.dto.FavoriteDTO;
import com.campus.entity.Activity;
import com.campus.entity.Favorite;
import com.campus.entity.User;
import com.campus.exception.BusinessException;
import com.campus.repository.ActivityRepository;
import com.campus.repository.FavoriteRepository;
import com.campus.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Favorite Service
 * Handles favorite management operations
 */
@Slf4j
@Service
@Transactional
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityRepository activityRepository;

    /**
     * Add activity to user's favorites
     * 
     * @param activityId Activity ID
     * @param username Username of the user
     * @return FavoriteDTO
     * @throws BusinessException if activity or user not found, or already favorited
     */
    public FavoriteDTO addFavorite(Long activityId, String username) {
        log.info("Adding favorite - activity ID: {}, username: {}", activityId, username);

        // Find user
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            log.warn("User not found: {}", username);
            throw new BusinessException(404, "User not found");
        }

        User user = userOptional.get();

        // Find activity
        Optional<Activity> activityOptional = activityRepository.findById(activityId);
        if (activityOptional.isEmpty()) {
            log.warn("Activity not found: {}", activityId);
            throw new BusinessException(404, "Activity not found");
        }

        Activity activity = activityOptional.get();

        // Check if already favorited
        if (favoriteRepository.existsByUserIdAndActivityId(user.getId(), activityId)) {
            log.warn("Activity already favorited by user {} for activity {}", user.getId(), activityId);
            throw new BusinessException(409, "Activity is already in your favorites");
        }

        // Create favorite
        Favorite favorite = Favorite.builder()
                .userId(user.getId())
                .activityId(activityId)
                .build();

        // Save favorite
        Favorite savedFavorite = favoriteRepository.save(favorite);
        log.info("Favorite added successfully - ID: {}", savedFavorite.getId());

        return FavoriteDTO.fromEntity(savedFavorite);
    }

    /**
     * Remove activity from user's favorites
     * 
     * @param activityId Activity ID
     * @param username Username of the user
     * @throws BusinessException if activity or user not found, or not favorited
     */
    public void removeFavorite(Long activityId, String username) {
        log.info("Removing favorite - activity ID: {}, username: {}", activityId, username);

        // Find user
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            log.warn("User not found: {}", username);
            throw new BusinessException(404, "User not found");
        }

        User user = userOptional.get();

        // Find activity
        Optional<Activity> activityOptional = activityRepository.findById(activityId);
        if (activityOptional.isEmpty()) {
            log.warn("Activity not found: {}", activityId);
            throw new BusinessException(404, "Activity not found");
        }

        // Check if favorited
        if (!favoriteRepository.existsByUserIdAndActivityId(user.getId(), activityId)) {
            log.warn("Activity not in favorites for user {} and activity {}", user.getId(), activityId);
            throw new BusinessException(404, "Activity is not in your favorites");
        }

        // Delete favorite
        favoriteRepository.deleteByUserIdAndActivityId(user.getId(), activityId);
        log.info("Favorite removed successfully");
    }

    /**
     * Get user's favorite activities list
     * 
     * @param username Username of the user
     * @param page Page number (0-indexed)
     * @param size Page size
     * @return Page of FavoriteDTO
     * @throws BusinessException if user not found
     */
    public Page<FavoriteDTO> getFavorites(String username, int page, int size) {
        log.info("Fetching favorites - username: {}, page: {}, size: {}", username, page, size);

        // Find user
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            log.warn("User not found: {}", username);
            throw new BusinessException(404, "User not found");
        }

        User user = userOptional.get();

        // Validate pagination parameters
        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 10;

        // Create pageable with sorting by creation time (newest first)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        // Query favorites
        Page<Favorite> favoritePage = favoriteRepository.findByUserId(user.getId(), pageable);

        // Convert to DTO
        return favoritePage.map(FavoriteDTO::fromEntity);
    }

    /**
     * Check if user has favorited an activity
     * 
     * @param activityId Activity ID
     * @param username Username of the user
     * @return true if favorited, false otherwise
     * @throws BusinessException if user not found
     */
    public boolean isFavorited(Long activityId, String username) {
        log.info("Checking if activity is favorited - activity ID: {}, username: {}", activityId, username);

        // Find user
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            log.warn("User not found: {}", username);
            throw new BusinessException(404, "User not found");
        }

        User user = userOptional.get();

        return favoriteRepository.existsByUserIdAndActivityId(user.getId(), activityId);
    }

}
