package com.postgraduate.service;

import com.postgraduate.dto.AnnouncementDTO;
import com.postgraduate.entity.Announcement;
import com.postgraduate.repository.AnnouncementRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service for handling public announcement retrieval operations.
 * Manages retrieval of published announcements with pagination.
 */
@Slf4j
@Service
public class AnnouncementService {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    @Autowired
    private AnnouncementRepository announcementRepository;

    /**
     * Get all published announcements with pagination.
     * Returns only PUBLISHED announcements sorted by sortOrder in ascending order.
     *
     * @param page page number (0-indexed)
     * @param size page size (default 20, max 100)
     * @return Page of published announcements
     */
    public Page<AnnouncementDTO> getPublishedAnnouncements(int page, Integer size) {
        log.info("Retrieving published announcements - page: {}, size: {}", page, size);

        // Validate and set page size
        int pageSize = size != null ? Math.min(size, MAX_PAGE_SIZE) : DEFAULT_PAGE_SIZE;
        if (pageSize <= 0) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        // Create pageable with sortOrder ascending
        Pageable pageable = PageRequest.of(page, pageSize);

        // Fetch published announcements from repository
        Page<Announcement> announcements = announcementRepository
                .findByStatusAndDeletedFalseOrderBySortOrderAsc(
                        Announcement.AnnouncementStatus.PUBLISHED,
                        pageable);

        log.info("Retrieved {} published announcements from page {}", 
                announcements.getNumberOfElements(), page);

        // Convert to DTOs
        return announcements.map(AnnouncementDTO::fromEntity);
    }
}
