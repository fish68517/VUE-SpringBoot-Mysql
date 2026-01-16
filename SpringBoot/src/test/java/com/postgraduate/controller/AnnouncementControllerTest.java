package com.postgraduate.controller;

import com.postgraduate.dto.AnnouncementDTO;
import com.postgraduate.service.AnnouncementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for AnnouncementController endpoints.
 * Tests public announcement retrieval functionality.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AnnouncementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnnouncementService announcementService;

    private AnnouncementDTO announcement1;
    private AnnouncementDTO announcement2;

    @BeforeEach
    void setUp() {
        announcement1 = AnnouncementDTO.builder()
                .id(1L)
                .title("System Maintenance Notice")
                .content("The system will be under maintenance on 2024-01-20")
                .status("PUBLISHED")
                .sortOrder(1)
                .publishAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        announcement2 = AnnouncementDTO.builder()
                .id(2L)
                .title("New Features Released")
                .content("We have released new features for school search")
                .status("PUBLISHED")
                .sortOrder(2)
                .publishAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testGetAnnouncementsWithDefaultPagination() throws Exception {
        Page<AnnouncementDTO> page = new PageImpl<>(Arrays.asList(announcement1, announcement2));
        when(announcementService.getPublishedAnnouncements(0, null))
                .thenReturn(page);

        mockMvc.perform(get("/api/announcements")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.content[0].title").value("System Maintenance Notice"))
                .andExpect(jsonPath("$.data.content[1].title").value("New Features Released"))
                .andExpect(jsonPath("$.data.content[0].status").value("PUBLISHED"))
                .andExpect(jsonPath("$.data.content[1].status").value("PUBLISHED"));
    }

    @Test
    void testGetAnnouncementsWithCustomPageSize() throws Exception {
        Page<AnnouncementDTO> page = new PageImpl<>(Arrays.asList(announcement1), 0, 1, 2);
        when(announcementService.getPublishedAnnouncements(0, 1))
                .thenReturn(page);

        mockMvc.perform(get("/api/announcements")
                .param("page", "0")
                .param("size", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.size").value(1))
                .andExpect(jsonPath("$.data.content[0].title").value("System Maintenance Notice"));
    }

    @Test
    void testGetAnnouncementsWithPagination() throws Exception {
        Page<AnnouncementDTO> page = new PageImpl<>(Arrays.asList(announcement2), 1, 1, 2);
        when(announcementService.getPublishedAnnouncements(1, 1))
                .thenReturn(page);

        mockMvc.perform(get("/api/announcements")
                .param("page", "1")
                .param("size", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.number").value(1))
                .andExpect(jsonPath("$.data.content[0].title").value("New Features Released"));
    }

    @Test
    void testGetAnnouncementsSortedBySortOrder() throws Exception {
        Page<AnnouncementDTO> page = new PageImpl<>(Arrays.asList(announcement1, announcement2));
        when(announcementService.getPublishedAnnouncements(0, null))
                .thenReturn(page);

        mockMvc.perform(get("/api/announcements")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content[0].sortOrder").value(1))
                .andExpect(jsonPath("$.data.content[1].sortOrder").value(2));
    }

    @Test
    void testGetAnnouncementsEmpty() throws Exception {
        Page<AnnouncementDTO> page = new PageImpl<>(Arrays.asList());
        when(announcementService.getPublishedAnnouncements(0, null))
                .thenReturn(page);

        mockMvc.perform(get("/api/announcements")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalElements").value(0))
                .andExpect(jsonPath("$.data.content.length()").value(0));
    }

    @Test
    void testGetAnnouncementsWithLargePageSize() throws Exception {
        Page<AnnouncementDTO> page = new PageImpl<>(Arrays.asList(announcement1, announcement2));
        when(announcementService.getPublishedAnnouncements(0, 100))
                .thenReturn(page);

        mockMvc.perform(get("/api/announcements")
                .param("page", "0")
                .param("size", "100")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalElements").value(2));
    }

    @Test
    void testGetAnnouncementsResponseStructure() throws Exception {
        Page<AnnouncementDTO> page = new PageImpl<>(Arrays.asList(announcement1));
        when(announcementService.getPublishedAnnouncements(0, null))
                .thenReturn(page);

        mockMvc.perform(get("/api/announcements")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.content[0].id").value(1))
                .andExpect(jsonPath("$.data.content[0].title").exists())
                .andExpect(jsonPath("$.data.content[0].content").exists())
                .andExpect(jsonPath("$.data.content[0].status").exists())
                .andExpect(jsonPath("$.data.content[0].sortOrder").exists())
                .andExpect(jsonPath("$.data.content[0].publishAt").exists())
                .andExpect(jsonPath("$.data.content[0].createdAt").exists())
                .andExpect(jsonPath("$.data.content[0].updatedAt").exists());
    }
}
