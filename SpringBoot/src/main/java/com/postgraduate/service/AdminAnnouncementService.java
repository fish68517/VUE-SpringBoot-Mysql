package com.postgraduate.service;

import com.postgraduate.dto.AnnouncementCreateRequest;
import com.postgraduate.dto.AnnouncementDTO;
import com.postgraduate.dto.AnnouncementUpdateRequest;
import com.postgraduate.entity.Announcement;
import com.postgraduate.exception.ResourceNotFoundException;
import com.postgraduate.exception.ValidationException;
import com.postgraduate.repository.AnnouncementRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Service for handling admin announcement management operations.
 * Manages announcement CRUD operations including creation, update, and deletion.
 */
@Slf4j
@Service
public class AdminAnnouncementService {

    @Autowired
    private AnnouncementRepository announcementRepository;

    /**
     * Get all announcements with pagination (admin view).
     * Returns all announcements (both DRAFT and PUBLISHED) for admin management.
     *
     * @param page the page number (0-indexed)
     * @param size the page size (default 20, max 100)
     * @return Page containing announcements
     */
    public Page<AnnouncementDTO> getAllAnnouncements(int page, Integer size) {
        log.info("Getting all announcements - page: {}, size: {}", page, size);

        // Set default and max page size
        if (size == null || size <= 0) {
            size = 20;
        }
        if (size > 100) {
            size = 100;
        }

        // Create pageable with sort by sortOrder ascending
        Pageable pageable = PageRequest.of(page, size, Sort.by("sortOrder").ascending());

        // Get all non-deleted announcements
        Page<Announcement> announcements = announcementRepository.findByDeletedFalse(pageable);

        // Convert to DTOs
        return announcements.map(AnnouncementDTO::fromEntity);
    }

    /**
     * Create a new announcement.
     *
     * @param request the announcement creation request containing announcement details
     * @return AnnouncementDTO containing the created announcement information
     * @throws ValidationException if announcement data is invalid
     */
    @Transactional
    public AnnouncementDTO createAnnouncement(AnnouncementCreateRequest request) {
        log.info("Creating new announcement - title: {}, status: {}", request.getTitle(), request.getStatus());

        // Validate announcement data
        validateAnnouncementData(request.getTitle(), request.getContent(), request.getStatus(), request.getSortOrder());

        // Parse status
        Announcement.AnnouncementStatus status = parseAnnouncementStatus(request.getStatus());

        // Create new announcement entity
        Announcement announcement = Announcement.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .status(status)
                .sortOrder(request.getSortOrder())
                .deleted(false)
                .build();

        // Save to database
        announcement = announcementRepository.save(announcement);
        log.info("Announcement created successfully - announcementId: {}, title: {}", announcement.getId(), announcement.getTitle());

        return AnnouncementDTO.fromEntity(announcement);
    }

    /**
     * Update an existing announcement.
     *
     * @param announcementId the ID of the announcement to update
     * @param request the announcement update request containing updated details
     * @return AnnouncementDTO containing the updated announcement information
     * @throws ResourceNotFoundException if announcement is not found
     * @throws ValidationException if announcement data is invalid
     */
    @Transactional
    public AnnouncementDTO updateAnnouncement(Long announcementId, AnnouncementUpdateRequest request) {
        log.info("Updating announcement - announcementId: {}, title: {}, status: {}", 
                announcementId, request.getTitle(), request.getStatus());

        // Find existing announcement
        Announcement announcement = announcementRepository.findByIdAndDeletedFalse(announcementId)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement not found with id: " + announcementId));

        // Validate announcement data
        validateAnnouncementData(request.getTitle(), request.getContent(), request.getStatus(), request.getSortOrder());

        // Parse status
        Announcement.AnnouncementStatus status = parseAnnouncementStatus(request.getStatus());

        // Update announcement fields
        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());
        announcement.setStatus(status);
        announcement.setSortOrder(request.getSortOrder());

        // Save to database
        announcement = announcementRepository.save(announcement);
        log.info("Announcement updated successfully - announcementId: {}, title: {}", announcement.getId(), announcement.getTitle());

        return AnnouncementDTO.fromEntity(announcement);
    }

    /**
     * Delete an announcement (soft delete).
     * Marks the announcement as deleted instead of permanently removing it.
     *
     * @param announcementId the ID of the announcement to delete
     * @throws ResourceNotFoundException if announcement is not found
     */
    @Transactional
    public void deleteAnnouncement(Long announcementId) {
        log.info("Deleting announcement - announcementId: {}", announcementId);

        // Find existing announcement
        Announcement announcement = announcementRepository.findByIdAndDeletedFalse(announcementId)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement not found with id: " + announcementId));

        // Soft delete
        announcement.setDeleted(true);
        announcementRepository.save(announcement);

        log.info("Announcement deleted successfully - announcementId: {}", announcementId);
    }

    /**
     * Validate announcement data for creation and update operations.
     *
     * @param title the announcement title
     * @param content the announcement content
     * @param status the announcement status
     * @param sortOrder the announcement sort order
     * @throws ValidationException if any field is invalid
     */
    private void validateAnnouncementData(String title, String content, String status, Integer sortOrder) {
        if (!StringUtils.hasText(title)) {
            throw new ValidationException("Announcement title cannot be empty");
        }

        if (!StringUtils.hasText(content)) {
            throw new ValidationException("Announcement content cannot be empty");
        }

        if (!StringUtils.hasText(status)) {
            throw new ValidationException("Announcement status cannot be empty");
        }

        // Validate status is one of the allowed values
        try {
            Announcement.AnnouncementStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid announcement status. Must be one of: DRAFT, PUBLISHED");
        }

        if (sortOrder == null) {
            throw new ValidationException("Sort order cannot be null");
        }
    }

    /**
     * Parse announcement status string to enum.
     *
     * @param status the status string
     * @return AnnouncementStatus enum value
     * @throws ValidationException if status is invalid
     */
    private Announcement.AnnouncementStatus parseAnnouncementStatus(String status) {
        try {
            return Announcement.AnnouncementStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid announcement status: " + status);
        }
    }
}
