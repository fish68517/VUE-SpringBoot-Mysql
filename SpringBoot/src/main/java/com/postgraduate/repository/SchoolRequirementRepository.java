package com.postgraduate.repository;

import com.postgraduate.entity.SchoolRequirement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for SchoolRequirement entity providing database access operations.
 * Supports school requirement search and retrieval operations.
 */
@Repository
public interface SchoolRequirementRepository extends JpaRepository<SchoolRequirement, Long> {

    /**
     * Find all requirements for a specific school, excluding soft-deleted records.
     *
     * @param schoolId the school ID to filter by
     * @return List of requirements for the specified school
     */
    List<SchoolRequirement> findBySchoolIdAndDeletedFalse(Long schoolId);

    /**
     * Find all requirements for a specific school with pagination, excluding soft-deleted records.
     *
     * @param schoolId the school ID to filter by
     * @param pageable pagination information
     * @return Page of requirements for the specified school
     */
    Page<SchoolRequirement> findBySchoolIdAndDeletedFalse(Long schoolId, Pageable pageable);

    /**
     * Find all requirements for a specific major, excluding soft-deleted records.
     *
     * @param majorId the major ID to filter by
     * @return List of requirements for the specified major
     */
    List<SchoolRequirement> findByMajorIdAndDeletedFalse(Long majorId);

    /**
     * Find all requirements for a specific school and major, excluding soft-deleted records.
     *
     * @param schoolId the school ID to filter by
     * @param majorId the major ID to filter by
     * @return List of requirements for the specified school and major
     */
    List<SchoolRequirement> findBySchoolIdAndMajorIdAndDeletedFalse(Long schoolId, Long majorId);

    /**
     * Find all non-deleted requirements.
     *
     * @return List of all non-deleted requirements
     */
    List<SchoolRequirement> findByDeletedFalse();
}
