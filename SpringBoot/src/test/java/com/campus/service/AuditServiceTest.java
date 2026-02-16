package com.campus.service;

import com.campus.dto.ActivityDTO;
import com.campus.dto.AuditLogDTO;
import com.campus.entity.Activity;
import com.campus.entity.AuditLog;
import com.campus.entity.User;
import com.campus.exception.BusinessException;
import com.campus.repository.ActivityRepository;
import com.campus.repository.AuditLogRepository;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Audit Service Test
 */
@ExtendWith(MockitoExtension.class)
public class AuditServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuditService auditService;

    private User admin;
    private Activity activity;

    @BeforeEach
    public void setUp() {
        // Create test admin user
        admin = User.builder()
                .username("admin")
                .email("admin@test.com")
                .role(User.UserRole.ADMIN)
                .status(User.AccountStatus.ACTIVE)
                .build();
        admin.setId(1L);

        // Create test activity
        activity = Activity.builder()
                .organizerId(2L)
                .title("Test Activity")
                .description("Test Description")
                .type("lecture")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(2))
                .location("Test Location")
                .maxParticipants(100)
                .enableCrowdfunding(false)
                .status(Activity.ActivityStatus.PENDING_AUDIT)
                .build();
        activity.setId(1L);
    }

    @Test
    public void testGetPendingAuditActivities() {
        // Arrange
        Page<Activity> activityPage = new PageImpl<>(Arrays.asList(activity), PageRequest.of(0, 10), 1);
        when(activityRepository.findByStatus(eq(Activity.ActivityStatus.PENDING_AUDIT), any()))
                .thenReturn(activityPage);

        // Act
        Page<ActivityDTO> result = auditService.getPendingAuditActivities(0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Test Activity", result.getContent().get(0).getTitle());
        verify(activityRepository, times(1)).findByStatus(eq(Activity.ActivityStatus.PENDING_AUDIT), any());
    }

    @Test
    public void testAuditActivityApprove() {
        // Arrange
        when(activityRepository.findById(1L)).thenReturn(Optional.of(activity));
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(activityRepository.save(any(Activity.class))).thenReturn(activity);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        // Act
        ActivityDTO result = auditService.auditActivity(1L, true, null, "admin");

        // Assert
        assertNotNull(result);
        assertEquals("Test Activity", result.getTitle());
        verify(activityRepository, times(1)).save(any(Activity.class));
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    public void testAuditActivityReject() {
        // Arrange
        when(activityRepository.findById(1L)).thenReturn(Optional.of(activity));
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(activityRepository.save(any(Activity.class))).thenReturn(activity);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        // Act
        ActivityDTO result = auditService.auditActivity(1L, false, "Invalid content", "admin");

        // Assert
        assertNotNull(result);
        assertEquals("Test Activity", result.getTitle());
        verify(activityRepository, times(1)).save(any(Activity.class));
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    public void testAuditActivityNotFound() {
        // Arrange
        when(activityRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            auditService.auditActivity(999L, true, null, "admin");
        });
    }

    @Test
    public void testAuditActivityNotPendingAudit() {
        // Arrange
        activity.setStatus(Activity.ActivityStatus.APPROVED);
        when(activityRepository.findById(1L)).thenReturn(Optional.of(activity));

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            auditService.auditActivity(1L, true, null, "admin");
        });
    }

    @Test
    public void testAuditActivityAuditorNotFound() {
        // Arrange
        when(activityRepository.findById(1L)).thenReturn(Optional.of(activity));
        when(userRepository.findByUsername("admin")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            auditService.auditActivity(1L, true, null, "admin");
        });
    }

    @Test
    public void testAuditActivityNotAdmin() {
        // Arrange
        User nonAdmin = User.builder()
                .username("user")
                .email("user@test.com")
                .role(User.UserRole.USER)
                .status(User.AccountStatus.ACTIVE)
                .build();
        nonAdmin.setId(2L);
        when(activityRepository.findById(1L)).thenReturn(Optional.of(activity));
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(nonAdmin));

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            auditService.auditActivity(1L, true, null, "user");
        });
    }

    @Test
    public void testAuditActivityRejectWithoutReason() {
        // Arrange
        when(activityRepository.findById(1L)).thenReturn(Optional.of(activity));
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            auditService.auditActivity(1L, false, null, "admin");
        });
    }

    @Test
    public void testGetActivityAuditLogs() {
        // Arrange
        AuditLog auditLog = AuditLog.builder()
                .auditorId(1L)
                .targetId(1L)
                .auditType(AuditLog.AuditType.ACTIVITY)
                .auditStatus(AuditLog.AuditStatus.APPROVED)
                .reason(null)
                .build();
        auditLog.setId(1L);

        Page<AuditLog> auditLogPage = new PageImpl<>(Arrays.asList(auditLog), PageRequest.of(0, 10), 1);
        when(auditLogRepository.findByTargetId(eq(1L), any())).thenReturn(auditLogPage);

        // Act
        Page<AuditLogDTO> result = auditService.getActivityAuditLogs(1L, 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(AuditLog.AuditStatus.APPROVED, result.getContent().get(0).getAuditStatus());
        verify(auditLogRepository, times(1)).findByTargetId(eq(1L), any());
    }

}
