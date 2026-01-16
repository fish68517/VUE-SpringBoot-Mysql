package com.postgraduate.repository;

import com.postgraduate.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Favorite entity providing database access operations.
 * Supports favorite management, statistics calculation, and user favorite retrieval.
 */
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    /**
     * Find a favorite by user ID and school ID.
     *
     * @param userId the user ID
     * @param schoolId the school ID
     * @return Optional containing the favorite if found
     */
    Optional<Favorite> findByUserIdAndSchoolIdAndDeletedFalse(Long userId, Long schoolId);

    /**
     * Check if a user has favorited a school.
     *
     * @param userId the user ID
     * @param schoolId the school ID
     * @return true if the favorite exists, false otherwise
     */
    boolean existsByUserIdAndSchoolIdAndDeletedFalse(Long userId, Long schoolId);

    /**
     * Find all favorites for a specific user, excluding soft-deleted records.
     *
     * @param userId the user ID
     * @param pageable pagination information
     * @return Page of favorites for the user
     */
    Page<Favorite> findByUserIdAndDeletedFalse(Long userId, Pageable pageable);

    /**
     * Find all non-deleted favorites for a specific school.
     * Used for statistics calculation.
     *
     * @param schoolId the school ID
     * @return List of all non-deleted favorites for the school
     */
    List<Favorite> findBySchoolIdAndDeletedFalse(Long schoolId);

    /**
     * Count non-deleted favorites for a specific school.
     *
     * @param schoolId the school ID
     * @return count of non-deleted favorites
     */
    long countBySchoolIdAndDeletedFalse(Long schoolId);
}
