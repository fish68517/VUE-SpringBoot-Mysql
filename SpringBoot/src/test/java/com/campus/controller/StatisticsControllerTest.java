package com.campus.controller;

import com.campus.dto.ActivityStatisticsDTO;
import com.campus.dto.AdminDashboardDTO;
import com.campus.dto.ApiResponse;
import com.campus.dto.CrowdfundingStatisticsDTO;
import com.campus.dto.FeedbackStatisticsDTO;
import com.campus.exception.BusinessException;
import com.campus.service.StatisticsService;
import com.campus.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Statistics Controller Test
 */
@ExtendWith(MockitoExtension.class)
class StatisticsControllerTest {

    @Mock
    private StatisticsService statisticsService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private StatisticsController statisticsController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private String validToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(statisticsController).build();
        objectMapper = new ObjectMapper();
        validToken = "Bearer valid_token";
    }

    @Test
    void testGetActivityStatistics_Success() throws Exception {
        // Arrange
        ActivityStatisticsDTO statistics = ActivityStatisticsDTO.builder()
                .totalActivities(5L)
                .totalRegistrations(50L)
                .averageRegistrationsPerActivity(10.0)
                .averageRating(4.5)
                .build();

        when(jwtUtil.validateToken("valid_token")).thenReturn(true);
        when(jwtUtil.extractUsername("valid_token")).thenReturn("organizer");
        when(statisticsService.getActivityStatistics("organizer")).thenReturn(statistics);

        // Act & Assert
        mockMvc.perform(get("/api/statistics/activities")
                .header("Authorization", validToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalActivities").value(5))
                .andExpect(jsonPath("$.data.totalRegistrations").value(50))
                .andExpect(jsonPath("$.data.averageRating").value(4.5));

        verify(statisticsService, times(1)).getActivityStatistics("organizer");
    }

    @Test
    void testGetActivityStatistics_InvalidToken() throws Exception {
        // Arrange
        when(jwtUtil.validateToken("invalid_token")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(get("/api/statistics/activities")
                .header("Authorization", "Bearer invalid_token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));

        verify(statisticsService, times(0)).getActivityStatistics(anyString());
    }

    @Test
    void testGetActivityStatistics_MissingToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/statistics/activities")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));

        verify(statisticsService, times(0)).getActivityStatistics(anyString());
    }

    @Test
    void testGetCrowdfundingStatistics_Success() throws Exception {
        // Arrange
        CrowdfundingStatisticsDTO statistics = CrowdfundingStatisticsDTO.builder()
                .totalCrowdfundingAmount(5000L)
                .totalTargetAmount(10000L)
                .completionRate(50.0)
                .totalSupporters(100L)
                .averageSupportPerActivity(20.0)
                .build();

        when(jwtUtil.validateToken("valid_token")).thenReturn(true);
        when(jwtUtil.extractUsername("valid_token")).thenReturn("organizer");
        when(statisticsService.getCrowdfundingStatistics("organizer")).thenReturn(statistics);

        // Act & Assert
        mockMvc.perform(get("/api/statistics/crowdfunding")
                .header("Authorization", validToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalCrowdfundingAmount").value(5000))
                .andExpect(jsonPath("$.data.totalTargetAmount").value(10000))
                .andExpect(jsonPath("$.data.completionRate").value(50.0))
                .andExpect(jsonPath("$.data.totalSupporters").value(100));

        verify(statisticsService, times(1)).getCrowdfundingStatistics("organizer");
    }

    @Test
    void testGetCrowdfundingStatistics_InvalidToken() throws Exception {
        // Arrange
        when(jwtUtil.validateToken("invalid_token")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(get("/api/statistics/crowdfunding")
                .header("Authorization", "Bearer invalid_token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));

        verify(statisticsService, times(0)).getCrowdfundingStatistics(anyString());
    }

    @Test
    void testGetFeedbackStatistics_Success() throws Exception {
        // Arrange
        FeedbackStatisticsDTO statistics = FeedbackStatisticsDTO.builder()
                .totalFeedback(50L)
                .averageRating(4.3)
                .activitiesWithFeedback(5L)
                .build();

        when(jwtUtil.validateToken("valid_token")).thenReturn(true);
        when(jwtUtil.extractUsername("valid_token")).thenReturn("organizer");
        when(statisticsService.getFeedbackStatistics("organizer")).thenReturn(statistics);

        // Act & Assert
        mockMvc.perform(get("/api/statistics/feedback")
                .header("Authorization", validToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalFeedback").value(50))
                .andExpect(jsonPath("$.data.averageRating").value(4.3))
                .andExpect(jsonPath("$.data.activitiesWithFeedback").value(5));

        verify(statisticsService, times(1)).getFeedbackStatistics("organizer");
    }

    @Test
    void testGetFeedbackStatistics_InvalidToken() throws Exception {
        // Arrange
        when(jwtUtil.validateToken("invalid_token")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(get("/api/statistics/feedback")
                .header("Authorization", "Bearer invalid_token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));

        verify(statisticsService, times(0)).getFeedbackStatistics(anyString());
    }

    @Test
    void testGetAdminDashboard_Success() throws Exception {
        // Arrange
        com.campus.dto.AdminDashboardDTO dashboard = com.campus.dto.AdminDashboardDTO.builder()
                .totalUsers(100L)
                .totalActivities(50L)
                .totalCrowdfundingAmount(50000L)
                .averageActivityRating(4.3)
                .totalRegistrations(500L)
                .totalFeedback(200L)
                .pendingAuditActivities(5L)
                .pendingAuditCrowdfunding(2L)
                .build();

        when(statisticsService.getAdminDashboard()).thenReturn(dashboard);

        // Act & Assert
        mockMvc.perform(get("/api/statistics/dashboard")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalUsers").value(100))
                .andExpect(jsonPath("$.data.totalActivities").value(50))
                .andExpect(jsonPath("$.data.totalCrowdfundingAmount").value(50000))
                .andExpect(jsonPath("$.data.averageActivityRating").value(4.3))
                .andExpect(jsonPath("$.data.totalRegistrations").value(500))
                .andExpect(jsonPath("$.data.totalFeedback").value(200))
                .andExpect(jsonPath("$.data.pendingAuditActivities").value(5))
                .andExpect(jsonPath("$.data.pendingAuditCrowdfunding").value(2));

        verify(statisticsService, times(1)).getAdminDashboard();
    }

    @Test
    void testExportStatistics_Success() throws Exception {
        // Arrange
        byte[] excelData = new byte[]{1, 2, 3, 4, 5};
        when(statisticsService.exportStatisticsToExcel()).thenReturn(excelData);

        // Act & Assert
        mockMvc.perform(get("/api/statistics/export")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=statistics.xlsx"))
                .andExpect(header().string("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

        verify(statisticsService, times(1)).exportStatisticsToExcel();
    }

}
