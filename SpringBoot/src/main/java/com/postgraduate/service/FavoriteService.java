package com.postgraduate.service;

import com.postgraduate.dto.FavoriteDTO;
import com.postgraduate.dto.FavoriteStatsDTO;
import com.postgraduate.dto.SchoolDTO;
import com.postgraduate.entity.Favorite;
import com.postgraduate.entity.School;
import com.postgraduate.entity.User;
import com.postgraduate.exception.ResourceNotFoundException;
import com.postgraduate.repository.FavoriteRepository;
import com.postgraduate.repository.SchoolRepository;
import com.postgraduate.repository.UserRepository;
import com.postgraduate.util.AuthorizationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for handling favorite management operations.
 * Provides methods for adding, removing, and listing user favorites.
 */
@Slf4j
@Service
public class FavoriteService {

    private static final int DEFAULT_PAGE_SIZE = 20;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private StatisticsCalculator statisticsCalculator;

    @Autowired
    private AuthorizationUtil authorizationUtil;

    /**
     * Add a school to the current user's favorites.
     * If the school is already favorited, this operation is idempotent.
     *
     * @param schoolId the school ID to favorite
     * @return FavoriteDTO containing the favorite information
     * @throws ResourceNotFoundException if school is not found
     */
    @Transactional
    public FavoriteDTO addFavorite(Long schoolId) {
        log.info("Adding favorite for school id: {}", schoolId);

        User currentUser = authorizationUtil.getCurrentUser();

        // Verify school exists
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with id: " + schoolId));

        // Check if already favorited
        if (favoriteRepository.existsByUserIdAndSchoolIdAndDeletedFalse(currentUser.getId(), schoolId)) {
            log.info("School {} is already favorited by user {}", schoolId, currentUser.getId());
            Favorite existing = favoriteRepository.findByUserIdAndSchoolIdAndDeletedFalse(currentUser.getId(), schoolId)
                    .orElseThrow(() -> new ResourceNotFoundException("Favorite not found"));
            return FavoriteDTO.fromEntityWithSchool(existing, SchoolDTO.fromEntity(school));
        }

        // Create new favorite
        Favorite favorite = Favorite.builder()
                .userId(currentUser.getId())
                .schoolId(schoolId)
                .build();

        Favorite savedFavorite = favoriteRepository.save(favorite);
        log.info("Favorite added successfully for user {} and school {}", currentUser.getId(), schoolId);

        return FavoriteDTO.fromEntityWithSchool(savedFavorite, SchoolDTO.fromEntity(school));
    }

    /**
     * Remove a school from the current user's favorites.
     * Uses soft delete to maintain data integrity.
     *
     * @param schoolId the school ID to remove from favorites
     * @throws ResourceNotFoundException if favorite is not found
     */
    @Transactional
    public void removeFavorite(Long schoolId) {
        log.info("Removing favorite for school id: {}", schoolId);

        User currentUser = authorizationUtil.getCurrentUser();

        Favorite favorite = favoriteRepository.findByUserIdAndSchoolIdAndDeletedFalse(currentUser.getId(), schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Favorite not found for school id: " + schoolId));

        favorite.setDeleted(true);
        favoriteRepository.save(favorite);
        log.info("Favorite removed successfully for user {} and school {}", currentUser.getId(), schoolId);
    }

    /**
     * Get all favorites for the current user with pagination.
     * Favorites are ordered by creation date (most recent first).
     *
     * @param page page number (0-indexed)
     * @param size page size (default 20 if not specified or exceeds max)
     * @return Page of FavoriteDTO containing user's favorites with school information
     */
    @Transactional(readOnly = true)
    public Page<FavoriteDTO> getUserFavorites(int page, Integer size) {
        log.info("Retrieving favorites for current user - page: {}, size: {}", page, size);

        User currentUser = authorizationUtil.getCurrentUser();

        // Validate and set page size
        int pageSize = (size == null || size <= 0 || size > 100) ? DEFAULT_PAGE_SIZE : size;
        Pageable pageable = PageRequest.of(page, pageSize);

        Page<Favorite> favorites = favoriteRepository.findByUserIdAndDeletedFalse(currentUser.getId(), pageable);
        log.info("Found {} favorites for user {}", favorites.getTotalElements(), currentUser.getId());

        return favorites.map(favorite -> {
            School school = schoolRepository.findById(favorite.getSchoolId()).orElse(null);
            SchoolDTO schoolDTO = school != null ? SchoolDTO.fromEntity(school) : null;
            return FavoriteDTO.fromEntityWithSchool(favorite, schoolDTO);
        });
    }

    /**
     * Check if a school is favorited by the current user.
     *
     * @param schoolId the school ID to check
     * @return true if the school is favorited, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean isFavorited(Long schoolId) {
        User currentUser = authorizationUtil.getCurrentUser();
        return favoriteRepository.existsByUserIdAndSchoolIdAndDeletedFalse(currentUser.getId(), schoolId);
    }

    /**
     * Get demographic statistics for users who favorited a school.
     * Calculates undergradTier distribution and CET-4 score bucket distribution.
     *
     * @param schoolId the school ID to get statistics for
     * @return FavoriteStatsDTO containing demographic distributions
     * @throws ResourceNotFoundException if school is not found
     */
    @Transactional(readOnly = true)
    public FavoriteStatsDTO getFavoriteStats(Long schoolId) {
        log.info("Retrieving favorite statistics for school id: {}", schoolId);

        // Verify school exists
        schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with id: " + schoolId));

        // Get all favorites for the school
        List<Favorite> favorites = favoriteRepository.findBySchoolIdAndDeletedFalse(schoolId);
        log.info("Found {} favorites for school {}", favorites.size(), schoolId);

        // Calculate statistics
        FavoriteStatsDTO stats = statisticsCalculator.calculateFavoriteStats(favorites);
        log.info("Favorite statistics calculated for school {}", schoolId);

        return stats;
    }
}
