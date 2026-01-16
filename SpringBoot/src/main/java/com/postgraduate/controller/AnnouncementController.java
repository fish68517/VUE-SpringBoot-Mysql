package com.postgraduate.controller;

import com.postgraduate.dto.AnnouncementDTO;
import com.postgraduate.dto.ApiResponse;
import com.postgraduate.service.AnnouncementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for public announcement retrieval endpoints.
 * Handles retrieval of published announcements with pagination.
 * No authentication required for these endpoints.
 */
@Slf4j
@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    /**
     * Get all published announcements with pagination.
     * Returns only PUBLISHED announcements sorted by sortOrder in ascending order.
     * Default page size is 20, maximum page size is 100.
     *
     * @param page page number (0-indexed, default 0)
     * @param size page size (default 20, max 100)
     * @return ResponseEntity with ApiResponse containing paginated published announcements
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<AnnouncementDTO>>> getAnnouncements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {
        log.info("Get announcements endpoint called - page: {}, size: {}", page, size);
        Page<AnnouncementDTO> announcements = announcementService.getPublishedAnnouncements(page, size);
        return ResponseEntity.ok(ApiResponse.success("Announcements retrieved successfully", announcements));
    }
}
