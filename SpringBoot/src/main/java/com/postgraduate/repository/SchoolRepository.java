package com.postgraduate.repository;

import com.postgraduate.entity.School;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for School entity providing database access operations.
 * Supports school search, filtering, and retrieval operations.
 */
@Repository
public interface SchoolRepository extends JpaRepository<School, Long>, JpaSpecificationExecutor<School> {

    /**
     * Find all non-deleted schools with pagination.
     *
     * @param pageable pagination information
     * @return Page of all non-deleted schools
     */
    Page<School> findByDeletedFalse(Pageable pageable);

    /**
     * Find schools by city, excluding soft-deleted records.
     *
     * @param city the city to filter by
     * @param pageable pagination information
     * @return Page of schools in the specified city
     */
    Page<School> findByCityAndDeletedFalse(String city, Pageable pageable);

    /**
     * Find schools by tier, excluding soft-deleted records.
     *
     * @param tier the school tier to filter by
     * @param pageable pagination information
     * @return Page of schools with the specified tier
     */
    Page<School> findByTierAndDeletedFalse(String tier, Pageable pageable);

    /**
     * Find schools by city and tier, excluding soft-deleted records.
     *
     * @param city the city to filter by
     * @param tier the school tier to filter by
     * @param pageable pagination information
     * @return Page of schools matching both city and tier
     */
    Page<School> findByCityAndTierAndDeletedFalse(String city, String tier, Pageable pageable);

    /**
     * Find schools by name containing the search keyword (case-insensitive), excluding soft-deleted records.
     *
     * @param keyword the search keyword
     * @param pageable pagination information
     * @return Page of schools matching the keyword
     */
    Page<School> findByNameContainingIgnoreCaseAndDeletedFalse(String keyword, Pageable pageable);

    /**
     * Find all non-deleted schools.
     *
     * @return List of all non-deleted schools
     */
    List<School> findByDeletedFalse();


}
