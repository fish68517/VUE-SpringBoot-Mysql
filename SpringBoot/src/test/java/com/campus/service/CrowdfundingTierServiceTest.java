package com.campus.service;

import com.campus.dto.CrowdfundingTierDTO;
import com.campus.entity.CrowdfundingTier;
import com.campus.repository.CrowdfundingTierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Crowdfunding Tier Service Test
 */
@ExtendWith(MockitoExtension.class)
class CrowdfundingTierServiceTest {

    @Mock
    private CrowdfundingTierRepository crowdfundingTierRepository;

    @InjectMocks
    private CrowdfundingTierService crowdfundingTierService;

    private CrowdfundingTier testTier1;
    private CrowdfundingTier testTier2;
    private CrowdfundingTier testTier3;

    @BeforeEach
    void setUp() {
        // Create test tiers
        testTier1 = CrowdfundingTier.builder()
                .activityId(1L)
                .name("10元档")
                .amount(BigDecimal.valueOf(10))
                .description("支持10元")
                .isPreset(true)
                .displayOrder(1)
                .build();
        testTier1.setId(1L);

        testTier2 = CrowdfundingTier.builder()
                .activityId(1L)
                .name("50元档")
                .amount(BigDecimal.valueOf(50))
                .description("支持50元")
                .isPreset(true)
                .displayOrder(2)
                .build();
        testTier2.setId(2L);

        testTier3 = CrowdfundingTier.builder()
                .activityId(1L)
                .name("自定义档位")
                .amount(BigDecimal.valueOf(100))
                .description("自定义支持金额")
                .isPreset(false)
                .displayOrder(3)
                .build();
        testTier3.setId(3L);
    }

    @Test
    void testGetTiersByActivityId_Success() {
        // Arrange
        List<CrowdfundingTier> tiers = Arrays.asList(testTier1, testTier2, testTier3);
        when(crowdfundingTierRepository.findByActivityIdOrderByDisplayOrder(1L)).thenReturn(tiers);

        // Act
        List<CrowdfundingTierDTO> result = crowdfundingTierService.getTiersByActivityId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("10元档", result.get(0).getName());
        assertEquals("50元档", result.get(1).getName());
        assertEquals("自定义档位", result.get(2).getName());
        verify(crowdfundingTierRepository, times(1)).findByActivityIdOrderByDisplayOrder(1L);
    }

    @Test
    void testGetTiersByActivityId_Empty() {
        // Arrange
        when(crowdfundingTierRepository.findByActivityIdOrderByDisplayOrder(999L)).thenReturn(Arrays.asList());

        // Act
        List<CrowdfundingTierDTO> result = crowdfundingTierService.getTiersByActivityId(999L);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testGetPresetTiersByActivityId_Success() {
        // Arrange
        List<CrowdfundingTier> presetTiers = Arrays.asList(testTier1, testTier2);
        when(crowdfundingTierRepository.findPresetTiersByActivityId(1L)).thenReturn(presetTiers);

        // Act
        List<CrowdfundingTierDTO> result = crowdfundingTierService.getPresetTiersByActivityId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getIsPreset());
        assertTrue(result.get(1).getIsPreset());
    }

    @Test
    void testGetCustomTiersByActivityId_Success() {
        // Arrange
        List<CrowdfundingTier> customTiers = Arrays.asList(testTier3);
        when(crowdfundingTierRepository.findCustomTiersByActivityId(1L)).thenReturn(customTiers);

        // Act
        List<CrowdfundingTierDTO> result = crowdfundingTierService.getCustomTiersByActivityId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertFalse(result.get(0).getIsPreset());
    }

    @Test
    void testCreateTier_Success() {
        // Arrange
        CrowdfundingTierDTO tierDTO = CrowdfundingTierDTO.builder()
                .activityId(1L)
                .name("100元档")
                .amount(BigDecimal.valueOf(100))
                .description("支持100元")
                .isPreset(true)
                .displayOrder(3)
                .build();

        CrowdfundingTier savedTier = tierDTO.toEntity();
        savedTier.setId(4L);

        when(crowdfundingTierRepository.save(any(CrowdfundingTier.class))).thenReturn(savedTier);

        // Act
        CrowdfundingTierDTO result = crowdfundingTierService.createTier(tierDTO);

        // Assert
        assertNotNull(result);
        assertEquals(4L, result.getId());
        assertEquals("100元档", result.getName());
        assertEquals(BigDecimal.valueOf(100), result.getAmount());
        verify(crowdfundingTierRepository, times(1)).save(any(CrowdfundingTier.class));
    }

    @Test
    void testUpdateTier_Success() {
        // Arrange
        CrowdfundingTierDTO updateDTO = CrowdfundingTierDTO.builder()
                .name("20元档")
                .amount(BigDecimal.valueOf(20))
                .description("更新的描述")
                .displayOrder(1)
                .build();

        when(crowdfundingTierRepository.findById(1L)).thenReturn(Optional.of(testTier1));
        when(crowdfundingTierRepository.save(any(CrowdfundingTier.class))).thenReturn(testTier1);

        // Act
        CrowdfundingTierDTO result = crowdfundingTierService.updateTier(1L, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals("20元档", result.getName());
        verify(crowdfundingTierRepository, times(1)).findById(1L);
        verify(crowdfundingTierRepository, times(1)).save(any(CrowdfundingTier.class));
    }

    @Test
    void testUpdateTier_NotFound() {
        // Arrange
        CrowdfundingTierDTO updateDTO = CrowdfundingTierDTO.builder()
                .name("20元档")
                .build();

        when(crowdfundingTierRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            crowdfundingTierService.updateTier(999L, updateDTO);
        });
        assertEquals("Crowdfunding tier not found", exception.getMessage());
    }

    @Test
    void testDeleteTier_Success() {
        // Act
        crowdfundingTierService.deleteTier(1L);

        // Assert
        verify(crowdfundingTierRepository, times(1)).deleteById(1L);
    }

    @Test
    void testInitializeDefaultTiers_Success() {
        // Act
        crowdfundingTierService.initializeDefaultTiers(1L);

        // Assert
        verify(crowdfundingTierRepository, times(3)).save(any(CrowdfundingTier.class));
    }

}
