package com.postgraduate.repository;

import com.postgraduate.entity.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Announcement entity providing database access operations.
 * Supports announcement management, retrieval, and filtering by status.
 */
@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    /**
     * Find an announcement by ID, excluding soft-deleted records.
     *
     * @param id the announcement ID
     * @return Optional containing the announcement if found
     */
    Optional<Announcement> findByIdAndDeletedFalse(Long id);

    /**
     * Check if announcement exists by ID and is not soft-deleted.
     *
     * @param id the announcement ID
     * @return true if the announcement exists and is not deleted, false otherwise
     */
    boolean existsByIdAndDeletedFalse(Long id);

    /**
     * Find all published announcements, excluding soft-deleted records.
     * Used by public announcement retrieval endpoint.
     *
     * @param status the announcement status
     * @param pageable pagination information
     * @return Page of published announcements sorted by sortOrder
     */
    Page<Announcement> findByStatusAndDeletedFalseOrderBySortOrderAsc(
            Announcement.AnnouncementStatus status,
            Pageable pageable);

    /**
     * Find all announcements (both DRAFT and PUBLISHED), excluding soft-deleted records.
     * Used by admin announcement management endpoint.
     *
     * @param deleted the deleted flag (should be false)
     * @param pageable pagination information
     * @return Page of all non-deleted announcements
     */
    Page<Announcement> findByDeletedFalse(Pageable pageable);
}
