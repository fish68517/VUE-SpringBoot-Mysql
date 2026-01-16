package com.postgraduate.repository;

import com.postgraduate.entity.ExamSubject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for ExamSubject entity providing database access operations.
 * Supports exam subject search and retrieval operations.
 */
@Repository
public interface ExamSubjectRepository extends JpaRepository<ExamSubject, Long> {

    /**
     * Find all exam subjects for a specific school, excluding soft-deleted records.
     *
     * @param schoolId the school ID to filter by
     * @return List of exam subjects for the specified school
     */
    List<ExamSubject> findBySchoolIdAndDeletedFalse(Long schoolId);

    /**
     * Find all exam subjects for a specific school with pagination, excluding soft-deleted records.
     *
     * @param schoolId the school ID to filter by
     * @param pageable pagination information
     * @return Page of exam subjects for the specified school
     */
    Page<ExamSubject> findBySchoolIdAndDeletedFalse(Long schoolId, Pageable pageable);

    /**
     * Find all exam subjects for a specific major, excluding soft-deleted records.
     *
     * @param majorId the major ID to filter by
     * @return List of exam subjects for the specified major
     */
    List<ExamSubject> findByMajorIdAndDeletedFalse(Long majorId);

    /**
     * Find all exam subjects for a specific school and major, excluding soft-deleted records.
     *
     * @param schoolId the school ID to filter by
     * @param majorId the major ID to filter by
     * @return List of exam subjects for the specified school and major
     */
    List<ExamSubject> findBySchoolIdAndMajorIdAndDeletedFalse(Long schoolId, Long majorId);

    /**
     * Find all non-deleted exam subjects.
     *
     * @return List of all non-deleted exam subjects
     */
    List<ExamSubject> findByDeletedFalse();
}
