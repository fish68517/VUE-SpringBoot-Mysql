package com.campus.controller;

import com.campus.dto.ApiResponse;
import com.campus.dto.CrowdfundingSupportDTO;
import com.campus.service.CrowdfundingSupportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Crowdfunding Support Controller Test
 */
@ExtendWith(MockitoExtension.class)
class CrowdfundingSupportControllerTest {

    @Mock
    private CrowdfundingSupportService crowdfundingSupportService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CrowdfundingSupportController crowdfundingSupportController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(crowdfundingSupportController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCreateSupport_Success() throws Exception {
        // Arrange
        CrowdfundingSupportDTO supportDTO = CrowdfundingSupportDTO.builder()
                .activityId(1L)
                .amount(BigDecimal.valueOf(50))
                .tierId(1L)
                .build();

        CrowdfundingSupportDTO createdSupport = CrowdfundingSupportDTO.builder()
                .id(1L)
                .activityId(1L)
                .userId(1L)
                .amount(BigDecimal.valueOf(50))
                .tierId(1L)
                .paymentStatus(com.campus.entity.CrowdfundingSupport.PaymentStatus.COMPLETED)
                .createdAt(LocalDateTime.now())
                .build();

        when(authentication.getName()).thenReturn("testuser");
        when(crowdfundingSupportService.createSupport(any(CrowdfundingSupportDTO.class), eq("testuser")))
                .thenReturn(createdSupport);

        // Act & Assert
        mockMvc.perform(post("/api/crowdfunding/support")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(supportDTO))
                .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.amount").value(50));

        verify(crowdfundingSupportService, times(1)).createSupport(any(CrowdfundingSupportDTO.class), eq("testuser"));
    }

    @Test
    void testGetCrowdfundingDetails_Success() throws Exception {
        // Arrange
        CrowdfundingSupportService.CrowdfundingDetailsDTO details = CrowdfundingSupportService.CrowdfundingDetailsDTO.builder()
                .activityId(1L)
                .targetAmount(BigDecimal.valueOf(1000))
                .raisedAmount(BigDecimal.valueOf(500))
                .completionPercentage(BigDecimal.valueOf(50))
                .supporterCount(10L)
                .build();

        when(crowdfundingSupportService.getCrowdfundingDetails(1L))
                .thenReturn(details);

        // Act & Assert
        mockMvc.perform(get("/api/crowdfunding/activity/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.activityId").value(1L))
                .andExpect(jsonPath("$.data.targetAmount").value(1000))
                .andExpect(jsonPath("$.data.raisedAmount").value(500))
                .andExpect(jsonPath("$.data.supporterCount").value(10));

        verify(crowdfundingSupportService, times(1)).getCrowdfundingDetails(1L);
    }

}
