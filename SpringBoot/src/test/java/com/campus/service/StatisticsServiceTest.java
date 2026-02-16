package com.campus.service;

import com.campus.dto.ActivityStatisticsDTO;
import com.campus.dto.AdminDashboardDTO;
import com.campus.dto.CrowdfundingStatisticsDTO;
import com.campus.dto.FeedbackStatisticsDTO;
import com.campus.entity.Activity;
import com.campus.entity.CrowdfundingSupport;
import com.campus.entity.Registration;
import com.campus.entity.User;
import com.campus.exception.BusinessException;
import com.campus.repository.ActivityRepository;
import com.campus.repository.CrowdfundingSupportRepository;
import com.campus.repository.FeedbackRepository;
import com.campus.repository.RegistrationRepository;
import com.campus.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Statistics Service Test
 */
@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private CrowdfundingSupportRepository crowdfundingSupportRepository;

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private StatisticsService statisticsService;

    private User organizerUser;
    private Activity activity1;
    private Activity activity2;

    @BeforeEach
    void setUp() {
        organizerUser = User.builder()
                .username("organizer")
                .email("organizer@example.com")
                .role(User.UserRole.ORGANIZER)
                .build();
        organizerUser.setId(1L);

        activity1 = Activity.builder()
                .title("Activity 1")
                .organizerId(1L)
                .status(Activity.ActivityStatus.APPROVED)
                .enableCrowdfunding(true)
                .crowdfundingTarget(BigDecimal.valueOf(1000))
                .build();
        activity1.setId(1L);

        activity2 = Activity.builder()
                .title("Activity 2")
                .organizerId(1L)
                .status(Activity.ActivityStatus.APPROVED)
                .enableCrowdfunding(false)
                .build();
        activity2.setId(2L);
    }

    @Test
    void testGetActivityStatistics_Success() {
        // Arrange
        when(userRepository.findByUsername("organizer")).thenReturn(Optional.of(organizerUser));
        when(activityRepository.findByStatus(Activity.ActivityStatus.APPROVED))
                .thenReturn(Arrays.asList(activity1, activity2));
        when(registrationRepository.countByActivityIdAndStatus(1L, Registration.RegistrationStatus.REGISTERED))
                .thenReturn(10L);
        when(registrationRepository.countByActivityIdAndStatus(2L, Registration.RegistrationStatus.REGISTERED))
                .thenReturn(5L);
        when(crowdfundingSupportRepository.getTotalAmountByActivityId(1L))
                .thenReturn(BigDecimal.valueOf(800));
        when(feedbackRepository.getAverageRatingByActivityId(1L)).thenReturn(4.5);
        when(feedbackRepository.getAverageRatingByActivityId(2L)).thenReturn(4.0);

        // Act
        ActivityStatisticsDTO result = statisticsService.getActivityStatistics("organizer");

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getTotalActivities());
        assertEquals(15L, result.getTotalRegistrations());
        assertEquals(7.5, result.getAverageRegistrationsPerActivity());
        assertEquals(4.25, result.getAverageRating());
    }

    @Test
    void testGetActivityStatistics_OrganizerNotFound() {
        // Arrange
        when(userRepository.findByUsername("organizer")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            statisticsService.getActivityStatistics("organizer");
        });
    }

    @Test
    void testGetActivityStatistics_NotOrganizerRole() {
        // Arrange
        User regularUser = User.builder()
                .username("user")
                .email("user@example.com")
                .role(User.UserRole.USER)
                .build();
        regularUser.setId(2L);

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(regularUser));

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            statisticsService.getActivityStatistics("user");
        });
    }

    @Test
    void testGetActivityStatistics_NoActivities() {
        // Arrange
        when(userRepository.findByUsername("organizer")).thenReturn(Optional.of(organizerUser));
        when(activityRepository.findByStatus(Activity.ActivityStatus.APPROVED))
                .thenReturn(Arrays.asList());

        // Act
        ActivityStatisticsDTO result = statisticsService.getActivityStatistics("organizer");

        // Assert
        assertNotNull(result);
        assertEquals(0L, result.getTotalActivities());
        assertEquals(0L, result.getTotalRegistrations());
        assertEquals(0.0, result.getAverageRegistrationsPerActivity());
        assertEquals(0.0, result.getAverageRating());
    }

    @Test
    void testGetCrowdfundingStatistics_Success() {
        // Arrange
        when(userRepository.findByUsername("organizer")).thenReturn(Optional.of(organizerUser));
        when(activityRepository.findByStatus(Activity.ActivityStatus.APPROVED))
                .thenReturn(Arrays.asList(activity1, activity2));
        when(crowdfundingSupportRepository.getTotalAmountByActivityId(1L))
                .thenReturn(BigDecimal.valueOf(1200));
        when(crowdfundingSupportRepository.countByActivityIdAndPaymentStatus(1L, CrowdfundingSupport.PaymentStatus.COMPLETED))
                .thenReturn(15L);

        // Act
        CrowdfundingStatisticsDTO result = statisticsService.getCrowdfundingStatistics("organizer");

        // Assert
        assertNotNull(result);
        assertEquals(1200L, result.getTotalCrowdfundingAmount());
        assertEquals(1000L, result.getTotalTargetAmount());
        assertEquals(100.0, result.getCompletionRate());
        assertEquals(15L, result.getTotalSupporters());
        assertEquals(15.0, result.getAverageSupportPerActivity());
    }

    @Test
    void testGetCrowdfundingStatistics_OrganizerNotFound() {
        // Arrange
        when(userRepository.findByUsername("organizer")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            statisticsService.getCrowdfundingStatistics("organizer");
        });
    }

    @Test
    void testGetCrowdfundingStatistics_NotOrganizerRole() {
        // Arrange
        User regularUser = User.builder()
                .username("user")
                .email("user@example.com")
                .role(User.UserRole.USER)
                .build();
        regularUser.setId(2L);

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(regularUser));

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            statisticsService.getCrowdfundingStatistics("user");
        });
    }

    @Test
    void testGetCrowdfundingStatistics_NoCrowdfundingActivities() {
        // Arrange
        when(userRepository.findByUsername("organizer")).thenReturn(Optional.of(organizerUser));
        when(activityRepository.findByStatus(Activity.ActivityStatus.APPROVED))
                .thenReturn(Arrays.asList(activity2));

        // Act
        CrowdfundingStatisticsDTO result = statisticsService.getCrowdfundingStatistics("organizer");

        // Assert
        assertNotNull(result);
        assertEquals(0L, result.getTotalCrowdfundingAmount());
        assertEquals(0L, result.getTotalTargetAmount());
        assertEquals(0.0, result.getCompletionRate());
        assertEquals(0L, result.getTotalSupporters());
    }

    @Test
    void testGetFeedbackStatistics_Success() {
        // Arrange
        when(userRepository.findByUsername("organizer")).thenReturn(Optional.of(organizerUser));
        when(activityRepository.findByStatus(Activity.ActivityStatus.APPROVED))
                .thenReturn(Arrays.asList(activity1, activity2));
        when(feedbackRepository.countByActivityId(1L)).thenReturn(10L);
        when(feedbackRepository.countByActivityId(2L)).thenReturn(5L);
        when(feedbackRepository.getAverageRatingByActivityId(1L)).thenReturn(4.5);
        when(feedbackRepository.getAverageRatingByActivityId(2L)).thenReturn(4.0);

        // Act
        FeedbackStatisticsDTO result = statisticsService.getFeedbackStatistics("organizer");

        // Assert
        assertNotNull(result);
        assertEquals(15L, result.getTotalFeedback());
        assertEquals(4.25, result.getAverageRating());
        assertEquals(2L, result.getActivitiesWithFeedback());
    }

    @Test
    void testGetFeedbackStatistics_OrganizerNotFound() {
        // Arrange
        when(userRepository.findByUsername("organizer")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            statisticsService.getFeedbackStatistics("organizer");
        });
    }

    @Test
    void testGetFeedbackStatistics_NotOrganizerRole() {
        // Arrange
        User regularUser = User.builder()
                .username("user")
                .email("user@example.com")
                .role(User.UserRole.USER)
                .build();
        regularUser.setId(2L);

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(regularUser));

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            statisticsService.getFeedbackStatistics("user");
        });
    }

    @Test
    void testGetFeedbackStatistics_NoFeedback() {
        // Arrange
        when(userRepository.findByUsername("organizer")).thenReturn(Optional.of(organizerUser));
        when(activityRepository.findByStatus(Activity.ActivityStatus.APPROVED))
                .thenReturn(Arrays.asList(activity1, activity2));
        when(feedbackRepository.countByActivityId(1L)).thenReturn(0L);
        when(feedbackRepository.countByActivityId(2L)).thenReturn(0L);
        when(feedbackRepository.getAverageRatingByActivityId(1L)).thenReturn(null);
        when(feedbackRepository.getAverageRatingByActivityId(2L)).thenReturn(null);

        // Act
        FeedbackStatisticsDTO result = statisticsService.getFeedbackStatistics("organizer");

        // Assert
        assertNotNull(result);
        assertEquals(0L, result.getTotalFeedback());
        assertEquals(0.0, result.getAverageRating());
        assertEquals(0L, result.getActivitiesWithFeedback());
    }

    @Test
    void testGetAdminDashboard_Success() {
        // Arrange
        when(userRepository.count()).thenReturn(100L);
        when(activityRepository.count()).thenReturn(50L);
        when(crowdfundingSupportRepository.getTotalCrowdfundingAmount())
                .thenReturn(BigDecimal.valueOf(50000));
        when(feedbackRepository.getAverageRating()).thenReturn(4.3);
        when(registrationRepository.countByStatus(Registration.RegistrationStatus.REGISTERED))
                .thenReturn(500L);
        when(feedbackRepository.count()).thenReturn(200L);
        when(activityRepository.countByStatus(Activity.ActivityStatus.PENDING_AUDIT))
                .thenReturn(5L);

        // Act
        AdminDashboardDTO result = statisticsService.getAdminDashboard();

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.getTotalUsers());
        assertEquals(50L, result.getTotalActivities());
        assertEquals(50000L, result.getTotalCrowdfundingAmount());
        assertEquals(4.3, result.getAverageActivityRating());
        assertEquals(500L, result.getTotalRegistrations());
        assertEquals(200L, result.getTotalFeedback());
        assertEquals(5L, result.getPendingAuditActivities());
    }

    @Test
    void testGetAdminDashboard_WithNullValues() {
        // Arrange
        when(userRepository.count()).thenReturn(50L);
        when(activityRepository.count()).thenReturn(20L);
        when(crowdfundingSupportRepository.getTotalCrowdfundingAmount()).thenReturn(null);
        when(feedbackRepository.getAverageRating()).thenReturn(null);
        when(registrationRepository.countByStatus(Registration.RegistrationStatus.REGISTERED))
                .thenReturn(100L);
        when(feedbackRepository.count()).thenReturn(50L);
        when(activityRepository.countByStatus(Activity.ActivityStatus.PENDING_AUDIT))
                .thenReturn(2L);

        // Act
        AdminDashboardDTO result = statisticsService.getAdminDashboard();

        // Assert
        assertNotNull(result);
        assertEquals(50L, result.getTotalUsers());
        assertEquals(20L, result.getTotalActivities());
        assertEquals(0L, result.getTotalCrowdfundingAmount());
        assertEquals(0.0, result.getAverageActivityRating());
        assertEquals(100L, result.getTotalRegistrations());
        assertEquals(50L, result.getTotalFeedback());
        assertEquals(2L, result.getPendingAuditActivities());
    }

    @Test
    void testExportStatisticsToExcel_Success() {
        // Arrange
        organizerUser.setStatus(User.AccountStatus.ACTIVE);
        when(userRepository.findAll()).thenReturn(Arrays.asList(organizerUser));
        when(activityRepository.findAll()).thenReturn(Arrays.asList(activity1, activity2));
        when(registrationRepository.countByActivityIdAndStatus(1L, Registration.RegistrationStatus.REGISTERED))
                .thenReturn(10L);
        when(registrationRepository.countByActivityIdAndStatus(2L, Registration.RegistrationStatus.REGISTERED))
                .thenReturn(5L);
        when(feedbackRepository.getAverageRatingByActivityId(1L)).thenReturn(4.5);
        when(feedbackRepository.getAverageRatingByActivityId(2L)).thenReturn(null);
        when(crowdfundingSupportRepository.getTotalAmountByActivityId(1L))
                .thenReturn(BigDecimal.valueOf(1000));
        when(crowdfundingSupportRepository.countByActivityIdAndPaymentStatus(1L, CrowdfundingSupport.PaymentStatus.COMPLETED))
                .thenReturn(10L);

        // Act
        byte[] result = statisticsService.exportStatisticsToExcel();

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

}
