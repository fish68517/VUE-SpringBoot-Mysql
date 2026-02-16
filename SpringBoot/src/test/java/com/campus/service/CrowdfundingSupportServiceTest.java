package com.campus.service;

import com.campus.dto.CrowdfundingSupportDTO;
import com.campus.entity.Activity;
import com.campus.entity.CrowdfundingSupport;
import com.campus.entity.User;
import com.campus.exception.BusinessException;
import com.campus.repository.ActivityRepository;
import com.campus.repository.CrowdfundingSupportRepository;
import com.campus.repository.UserRepository;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Crowdfunding Support Service Test
 */
@ExtendWith(MockitoExtension.class)
class CrowdfundingSupportServiceTest {

    @Mock
    private CrowdfundingSupportRepository crowdfundingSupportRepository;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CrowdfundingSupportService crowdfundingSupportService;

    private User testUser;
    private Activity testActivity;
    private CrowdfundingSupport testSupport;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .role(User.UserRole.USER)
                .status(User.AccountStatus.ACTIVE)
                .build();
        testUser.setId(1L);

        // Create test activity with crowdfunding enabled
        testActivity = Activity.builder()
                .organizerId(2L)
                .title("Test Activity")
                .description("Test Description")
                .type("lecture")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(2))
                .location("Test Location")
                .maxParticipants(100)
                .enableCrowdfunding(true)
                .crowdfundingTarget(BigDecimal.valueOf(1000))
                .status(Activity.ActivityStatus.ONGOING)
                .build();
        testActivity.setId(1L);

        // Create test support
        testSupport = CrowdfundingSupport.builder()
                .activityId(1L)
                .userId(1L)
                .amount(BigDecimal.valueOf(50))
                .tierId(1L)
                .paymentStatus(CrowdfundingSupport.PaymentStatus.COMPLETED)
                .build();
        testSupport.setId(1L);
    }

    @Test
    void testCreateSupport_Success() {
        // Arrange
        CrowdfundingSupportDTO supportDTO = CrowdfundingSupportDTO.builder()
                .activityId(1L)
                .amount(BigDecimal.valueOf(50))
                .tierId(1L)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));
        when(crowdfundingSupportRepository.save(any(CrowdfundingSupport.class))).thenReturn(testSupport);

        // Act
        CrowdfundingSupportDTO result = crowdfundingSupportService.createSupport(supportDTO, "testuser");

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(BigDecimal.valueOf(50), result.getAmount());
        assertEquals(CrowdfundingSupport.PaymentStatus.COMPLETED, result.getPaymentStatus());
        verify(crowdfundingSupportRepository, times(1)).save(any(CrowdfundingSupport.class));
    }

    @Test
    void testCreateSupport_UserNotFound() {
        // Arrange
        CrowdfundingSupportDTO supportDTO = CrowdfundingSupportDTO.builder()
                .activityId(1L)
                .amount(BigDecimal.valueOf(50))
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            crowdfundingSupportService.createSupport(supportDTO, "testuser");
        });
    }

    @Test
    void testCreateSupport_ActivityNotFound() {
        // Arrange
        CrowdfundingSupportDTO supportDTO = CrowdfundingSupportDTO.builder()
                .activityId(1L)
                .amount(BigDecimal.valueOf(50))
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(activityRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            crowdfundingSupportService.createSupport(supportDTO, "testuser");
        });
    }

    @Test
    void testCreateSupport_CrowdfundingNotEnabled() {
        // Arrange
        testActivity.setEnableCrowdfunding(false);
        CrowdfundingSupportDTO supportDTO = CrowdfundingSupportDTO.builder()
                .activityId(1L)
                .amount(BigDecimal.valueOf(50))
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            crowdfundingSupportService.createSupport(supportDTO, "testuser");
        });
    }

    @Test
    void testCreateSupport_InvalidAmount() {
        // Arrange
        CrowdfundingSupportDTO supportDTO = CrowdfundingSupportDTO.builder()
                .activityId(1L)
                .amount(BigDecimal.ZERO)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            crowdfundingSupportService.createSupport(supportDTO, "testuser");
        });
    }

    @Test
    void testGetCrowdfundingDetails_Success() {
        // Arrange
        when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));
        when(crowdfundingSupportRepository.getTotalAmountByActivityId(1L))
                .thenReturn(BigDecimal.valueOf(500));
        when(crowdfundingSupportRepository.countByActivityIdAndPaymentStatus(1L, CrowdfundingSupport.PaymentStatus.COMPLETED))
                .thenReturn(10L);

        // Act
        CrowdfundingSupportService.CrowdfundingDetailsDTO details = crowdfundingSupportService.getCrowdfundingDetails(1L);

        // Assert
        assertNotNull(details);
        assertEquals(1L, details.getActivityId());
        assertEquals(BigDecimal.valueOf(1000), details.getTargetAmount());
        assertEquals(BigDecimal.valueOf(500), details.getRaisedAmount());
        assertEquals(10L, details.getSupporterCount());
        assertTrue(details.getCompletionPercentage().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testGetCrowdfundingDetails_ActivityNotFound() {
        // Arrange
        when(activityRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            crowdfundingSupportService.getCrowdfundingDetails(1L);
        });
    }

    @Test
    void testGetSupportsByActivityId() {
        // Arrange
        Page<CrowdfundingSupport> supportPage = new PageImpl<>(Arrays.asList(testSupport));
        Pageable pageable = PageRequest.of(0, 10);

        when(crowdfundingSupportRepository.findByActivityId(1L, pageable))
                .thenReturn(supportPage);

        // Act
        Page<CrowdfundingSupportDTO> result = crowdfundingSupportService.getSupportsByActivityId(1L, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).getId());
    }

    @Test
    void testGetMySupports() {
        // Arrange
        Page<CrowdfundingSupport> supportPage = new PageImpl<>(Arrays.asList(testSupport));
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(crowdfundingSupportRepository.findByUserId(1L, pageable))
                .thenReturn(supportPage);

        // Act
        Page<CrowdfundingSupportDTO> result = crowdfundingSupportService.getMySupports("testuser", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).getId());
    }

}
