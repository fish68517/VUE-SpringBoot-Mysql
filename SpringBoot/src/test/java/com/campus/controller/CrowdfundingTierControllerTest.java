package com.campus.controller;

import com.campus.dto.ApiResponse;
import com.campus.dto.CrowdfundingTierDTO;
import com.campus.service.CrowdfundingTierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Crowdfunding Tier Controller Test
 */
@ExtendWith(MockitoExtension.class)
class CrowdfundingTierControllerTest {

    @Mock
    private CrowdfundingTierService crowdfundingTierService;

    @InjectMocks
    private CrowdfundingTierController crowdfundingTierController;

    private CrowdfundingTierDTO testTier1;
    private CrowdfundingTierDTO testTier2;

    @BeforeEach
    void setUp() {
        testTier1 = CrowdfundingTierDTO.builder()
                .id(1L)
                .activityId(1L)
                .name("10元档")
                .amount(BigDecimal.valueOf(10))
                .description("支持10元")
                .isPreset(true)
                .displayOrder(1)
                .build();

        testTier2 = CrowdfundingTierDTO.builder()
                .id(2L)
                .activityId(1L)
                .name("50元档")
                .amount(BigDecimal.valueOf(50))
                .description("支持50元")
                .isPreset(true)
                .displayOrder(2)
                .build();
    }

    @Test
    void testGetTiersByActivityId_Success() {
        // Arrange
        List<CrowdfundingTierDTO> tiers = Arrays.asList(testTier1, testTier2);
        when(crowdfundingTierService.getTiersByActivityId(1L)).thenReturn(tiers);

        // Act
        ResponseEntity<ApiResponse<List<CrowdfundingTierDTO>>> response = 
            crowdfundingTierController.getTiersByActivityId(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(200, response.getBody().getCode());
        assertEquals(2, response.getBody().getData().size());
        verify(crowdfundingTierService, times(1)).getTiersByActivityId(1L);
    }

    @Test
    void testGetTiersByActivityId_Empty() {
        // Arrange
        when(crowdfundingTierService.getTiersByActivityId(999L)).thenReturn(Arrays.asList());

        // Act
        ResponseEntity<ApiResponse<List<CrowdfundingTierDTO>>> response = 
            crowdfundingTierController.getTiersByActivityId(999L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getData().size());
    }

    @Test
    void testGetPresetTiersByActivityId_Success() {
        // Arrange
        List<CrowdfundingTierDTO> presetTiers = Arrays.asList(testTier1, testTier2);
        when(crowdfundingTierService.getPresetTiersByActivityId(1L)).thenReturn(presetTiers);

        // Act
        ResponseEntity<ApiResponse<List<CrowdfundingTierDTO>>> response = 
            crowdfundingTierController.getPresetTiersByActivityId(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getData().size());
        assertTrue(response.getBody().getData().get(0).getIsPreset());
        verify(crowdfundingTierService, times(1)).getPresetTiersByActivityId(1L);
    }

    @Test
    void testGetCustomTiersByActivityId_Success() {
        // Arrange
        CrowdfundingTierDTO customTier = CrowdfundingTierDTO.builder()
                .id(3L)
                .activityId(1L)
                .name("自定义档位")
                .amount(BigDecimal.valueOf(100))
                .isPreset(false)
                .displayOrder(3)
                .build();

        List<CrowdfundingTierDTO> customTiers = Arrays.asList(customTier);
        when(crowdfundingTierService.getCustomTiersByActivityId(1L)).thenReturn(customTiers);

        // Act
        ResponseEntity<ApiResponse<List<CrowdfundingTierDTO>>> response = 
            crowdfundingTierController.getCustomTiersByActivityId(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getData().size());
        assertFalse(response.getBody().getData().get(0).getIsPreset());
        verify(crowdfundingTierService, times(1)).getCustomTiersByActivityId(1L);
    }

}
