package com.postgraduate.controller;

import com.postgraduate.dto.ApiResponse;
import com.postgraduate.dto.FavoriteDTO;
import com.postgraduate.dto.FavoriteStatsDTO;
import com.postgraduate.service.FavoriteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for favorite management endpoints.
 * Handles adding, removing, and listing user favorites.
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    /**
     * Add a school to the current user's favorites.
     * If the school is already favorited, returns the existing favorite.
     *
     * @param schoolId the school ID to favorite
     * @return ResponseEntity with ApiResponse containing the favorite information
     */
    @PostMapping("/schools/{id}/favorite")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FavoriteDTO>> addFavorite(@PathVariable("id") Long schoolId) {
        log.info("Add favorite endpoint called for school id: {}", schoolId);
        FavoriteDTO favorite = favoriteService.addFavorite(schoolId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("School added to favorites successfully", favorite));
    }

    /**
     * Remove a school from the current user's favorites.
     *
     * @param schoolId the school ID to remove from favorites
     * @return ResponseEntity with ApiResponse indicating success
     */
    @DeleteMapping("/schools/{id}/favorite")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> removeFavorite(@PathVariable("id") Long schoolId) {
        log.info("Remove favorite endpoint called for school id: {}", schoolId);
        favoriteService.removeFavorite(schoolId);
        return ResponseEntity.ok(ApiResponse.success("School removed from favorites successfully", null));
    }

    /**
     * Get all favorites for the current user with pagination.
     * Favorites are ordered by creation date (most recent first).
     * Default page size is 20, maximum page size is 100.
     *
     * @param page page number (0-indexed, default 0)
     * @param size page size (default 20, max 100)
     * @return ResponseEntity with ApiResponse containing paginated favorites
     */
    @GetMapping("/me/favorites")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<FavoriteDTO>>> getUserFavorites(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {
        log.info("Get user favorites endpoint called - page: {}, size: {}", page, size);
        Page<FavoriteDTO> favorites = favoriteService.getUserFavorites(page, size);
        return ResponseEntity.ok(ApiResponse.success("User favorites retrieved successfully", favorites));
    }

    /**
     * Get demographic statistics for users who favorited a school.
     * Displays undergradTier distribution and CET-4 score bucket distribution.
     *
     * @param schoolId the school ID to get statistics for
     * @return ResponseEntity with ApiResponse containing favorite statistics
     */
    @GetMapping("/schools/{id}/favorite-stats")
    public ResponseEntity<ApiResponse<FavoriteStatsDTO>> getFavoriteStats(@PathVariable("id") Long schoolId) {
        log.info("Get favorite stats endpoint called for school id: {}", schoolId);
        FavoriteStatsDTO stats = favoriteService.getFavoriteStats(schoolId);
        return ResponseEntity.ok(ApiResponse.success("Favorite statistics retrieved successfully", stats));
    }
}
