package com.postgraduate.controller;

import com.postgraduate.dto.AnnouncementCreateRequest;
import com.postgraduate.dto.AnnouncementDTO;
import com.postgraduate.dto.AnnouncementUpdateRequest;
import com.postgraduate.dto.ApiResponse;
import com.postgraduate.service.AdminAnnouncementService;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for admin announcement management endpoints.
 * Handles announcement CRUD operations including creation, update, and deletion.
 * All endpoints require ADMIN role.
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/announcements")
public class AdminAnnouncementController {

    @Autowired
    private AdminAnnouncementService adminAnnouncementService;

    /**
     * Get all announcements with pagination (admin view).
     * Returns all announcements (both DRAFT and PUBLISHED) for admin management.
     * Default page size is 20, maximum page size is 100.
     *
     * @param page page number (0-indexed, default 0)
     * @param size page size (default 20, max 100)
     * @return ResponseEntity with ApiResponse containing paginated announcements
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<AnnouncementDTO>>> getAnnouncements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {
        log.info("Get announcements endpoint called - page: {}, size: {}", page, size);
        Page<AnnouncementDTO> announcements = adminAnnouncementService.getAllAnnouncements(page, size);
        return ResponseEntity.ok(ApiResponse.success("Announcements retrieved successfully", announcements));
    }

    /**
     * Create a new announcement.
     * Admin can create announcements with title, content, status, and sort order.
     *
     * @param request the announcement creation request containing announcement details
     * @return ResponseEntity with ApiResponse containing the created announcement
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AnnouncementDTO>> createAnnouncement(
            @Valid @RequestBody AnnouncementCreateRequest request) {
        log.info("Create announcement endpoint called - title: {}, status: {}", 
                request.getTitle(), request.getStatus());
        AnnouncementDTO announcement = adminAnnouncementService.createAnnouncement(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Announcement created successfully", announcement));
    }

    /**
     * Update an existing announcement.
     * Admin can update announcement title, content, status, and sort order.
     *
     * @param announcementId the ID of the announcement to update
     * @param request the announcement update request containing updated details
     * @return ResponseEntity with ApiResponse containing the updated announcement
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AnnouncementDTO>> updateAnnouncement(
            @PathVariable("id") Long announcementId,
            @Valid @RequestBody AnnouncementUpdateRequest request) {
        log.info("Update announcement endpoint called - announcementId: {}, title: {}, status: {}", 
                announcementId, request.getTitle(), request.getStatus());
        AnnouncementDTO announcement = adminAnnouncementService.updateAnnouncement(announcementId, request);
        return ResponseEntity.ok(ApiResponse.success("Announcement updated successfully", announcement));
    }

    /**
     * Delete an announcement (soft delete).
     * Admin can delete announcements. The announcement is marked as deleted rather than permanently removed.
     *
     * @param announcementId the ID of the announcement to delete
     * @return ResponseEntity with ApiResponse indicating successful deletion
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteAnnouncement(@PathVariable("id") Long announcementId) {
        log.info("Delete announcement endpoint called - announcementId: {}", announcementId);
        adminAnnouncementService.deleteAnnouncement(announcementId);
        return ResponseEntity.ok(ApiResponse.success("Announcement deleted successfully", null));
    }
}
