package com.postgraduate.repository;

import com.postgraduate.entity.Major;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Major entity providing database access operations.
 * Supports major search and retrieval operations.
 */
@Repository
public interface MajorRepository extends JpaRepository<Major, Long> {

    /**
     * Find all majors for a specific school, excluding soft-deleted records.
     *
     * @param schoolId the school ID to filter by
     * @return List of majors for the specified school
     */
    List<Major> findBySchoolIdAndDeletedFalse(Long schoolId);

    /**
     * Find all majors for a specific school with pagination, excluding soft-deleted records.
     *
     * @param schoolId the school ID to filter by
     * @param pageable pagination information
     * @return Page of majors for the specified school
     */
    Page<Major> findBySchoolIdAndDeletedFalse(Long schoolId, Pageable pageable);

    /**
     * Find majors by name containing the search keyword (case-insensitive) for a specific school, excluding soft-deleted records.
     *
     * @param schoolId the school ID to filter by
     * @param keyword the search keyword
     * @return List of majors matching the keyword
     */
    List<Major> findBySchoolIdAndNameContainingIgnoreCaseAndDeletedFalse(Long schoolId, String keyword);

    /**
     * Find all non-deleted majors.
     *
     * @return List of all non-deleted majors
     */
    List<Major> findByDeletedFalse();
}
