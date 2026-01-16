package com.postgraduate.service;

import com.postgraduate.dto.AnnouncementDTO;
import com.postgraduate.entity.Announcement;
import com.postgraduate.repository.AnnouncementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Test class for AnnouncementService.
 * Tests public announcement retrieval functionality.
 */
@SpringBootTest
class AnnouncementServiceTest {

    @Autowired
    private AnnouncementService announcementService;

    @MockitoBean
    private AnnouncementRepository announcementRepository;

    private Announcement announcement1;
    private Announcement announcement2;

    @BeforeEach
    void setUp() {
        announcement1 = Announcement.builder()
                .id(1L)
                .title("System Maintenance Notice")
                .content("The system will be under maintenance on 2024-01-20")
                .status(Announcement.AnnouncementStatus.PUBLISHED)
                .sortOrder(1)
                .publishAt(LocalDateTime.now())
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        announcement2 = Announcement.builder()
                .id(2L)
                .title("New Features Released")
                .content("We have released new features for school search")
                .status(Announcement.AnnouncementStatus.PUBLISHED)
                .sortOrder(2)
                .publishAt(LocalDateTime.now())
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testGetPublishedAnnouncementsWithDefaultPageSize() {
        Page<Announcement> page = new PageImpl<>(Arrays.asList(announcement1, announcement2));
        when(announcementRepository.findByStatusAndDeletedFalseOrderBySortOrderAsc(
                eq(Announcement.AnnouncementStatus.PUBLISHED),
                any(Pageable.class)))
                .thenReturn(page);

        Page<AnnouncementDTO> result = announcementService.getPublishedAnnouncements(0, null);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals("System Maintenance Notice", result.getContent().get(0).getTitle());
        assertEquals("New Features Released", result.getContent().get(1).getTitle());
    }

    @Test
    void testGetPublishedAnnouncementsWithCustomPageSize() {
        Page<Announcement> page = new PageImpl<>(Arrays.asList(announcement1), 0, 1, 2);
        when(announcementRepository.findByStatusAndDeletedFalseOrderBySortOrderAsc(
                eq(Announcement.AnnouncementStatus.PUBLISHED),
                any(Pageable.class)))
                .thenReturn(page);

        Page<AnnouncementDTO> result = announcementService.getPublishedAnnouncements(0, 1);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("System Maintenance Notice", result.getContent().get(0).getTitle());
    }

    @Test
    void testGetPublishedAnnouncementsWithPagination() {
        Page<Announcement> page = new PageImpl<>(Arrays.asList(announcement2), 1, 1, 2);
        when(announcementRepository.findByStatusAndDeletedFalseOrderBySortOrderAsc(
                eq(Announcement.AnnouncementStatus.PUBLISHED),
                any(Pageable.class)))
                .thenReturn(page);

        Page<AnnouncementDTO> result = announcementService.getPublishedAnnouncements(1, 1);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("New Features Released", result.getContent().get(0).getTitle());
    }

    @Test
    void testGetPublishedAnnouncementsSortedBySortOrder() {
        Page<Announcement> page = new PageImpl<>(Arrays.asList(announcement1, announcement2));
        when(announcementRepository.findByStatusAndDeletedFalseOrderBySortOrderAsc(
                eq(Announcement.AnnouncementStatus.PUBLISHED),
                any(Pageable.class)))
                .thenReturn(page);

        Page<AnnouncementDTO> result = announcementService.getPublishedAnnouncements(0, null);

        assertNotNull(result);
        assertEquals(1, result.getContent().get(0).getSortOrder());
        assertEquals(2, result.getContent().get(1).getSortOrder());
    }

    @Test
    void testGetPublishedAnnouncementsEmpty() {
        Page<Announcement> page = new PageImpl<>(Arrays.asList());
        when(announcementRepository.findByStatusAndDeletedFalseOrderBySortOrderAsc(
                eq(Announcement.AnnouncementStatus.PUBLISHED),
                any(Pageable.class)))
                .thenReturn(page);

        Page<AnnouncementDTO> result = announcementService.getPublishedAnnouncements(0, null);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getContent().size());
    }

    @Test
    void testGetPublishedAnnouncementsMaxPageSizeEnforced() {
        Page<Announcement> page = new PageImpl<>(Arrays.asList(announcement1, announcement2));
        when(announcementRepository.findByStatusAndDeletedFalseOrderBySortOrderAsc(
                eq(Announcement.AnnouncementStatus.PUBLISHED),
                any(Pageable.class)))
                .thenReturn(page);

        // Request size larger than max (100)
        Page<AnnouncementDTO> result = announcementService.getPublishedAnnouncements(0, 200);

        assertNotNull(result);
        // Should be capped at 100
        assertEquals(2, result.getContent().size());
    }

    @Test
    void testGetPublishedAnnouncementsConvertsToDTOs() {
        Page<Announcement> page = new PageImpl<>(Arrays.asList(announcement1));
        when(announcementRepository.findByStatusAndDeletedFalseOrderBySortOrderAsc(
                eq(Announcement.AnnouncementStatus.PUBLISHED),
                any(Pageable.class)))
                .thenReturn(page);

        Page<AnnouncementDTO> result = announcementService.getPublishedAnnouncements(0, null);

        assertNotNull(result);
        AnnouncementDTO dto = result.getContent().get(0);
        assertEquals(1L, dto.getId());
        assertEquals("System Maintenance Notice", dto.getTitle());
        assertEquals("The system will be under maintenance on 2024-01-20", dto.getContent());
        assertEquals("PUBLISHED", dto.getStatus());
        assertEquals(1, dto.getSortOrder());
    }
}
