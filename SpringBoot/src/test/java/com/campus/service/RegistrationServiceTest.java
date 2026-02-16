package com.campus.service;

import com.campus.dto.RegistrationDTO;
import com.campus.entity.Activity;
import com.campus.entity.Registration;
import com.campus.entity.User;
import com.campus.exception.BusinessException;
import com.campus.repository.ActivityRepository;
import com.campus.repository.RegistrationRepository;
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
 * Registration Service Test
 */
@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RegistrationService registrationService;

    private User testUser;
    private Activity testActivity;
    private Registration testRegistration;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .role(User.UserRole.USER)
                .status(User.AccountStatus.ACTIVE)
                .build();
        testUser.setId(1L);

        // Create test activity
        testActivity = Activity.builder()
                .organizerId(2L)
                .title("Test Activity")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(2))
                .registrationDeadline(LocalDateTime.now().plusHours(12))
                .maxParticipants(50)
                .status(Activity.ActivityStatus.APPROVED)
                .enableCrowdfunding(false)
                .build();
        testActivity.setId(1L);

        // Create test registration
        testRegistration = Registration.builder()
                .activityId(testActivity.getId())
                .userId(testUser.getId())
                .participantName("Test User")
                .contactPhone("13800138000")
                .remarks("Test remarks")
                .status(Registration.RegistrationStatus.REGISTERED)
                .build();
        testRegistration.setId(1L);
    }

    @Test
    void testRegisterForActivity_Success() {
        // Arrange
        RegistrationDTO registrationDTO = RegistrationDTO.builder()
                .activityId(1L)
                .participantName("Test User")
                .contactPhone("13800138000")
                .remarks("Test remarks")
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));
        when(registrationRepository.countByActivityIdAndStatus(1L, Registration.RegistrationStatus.REGISTERED))
                .thenReturn(10L);
        when(registrationRepository.findByActivityIdAndUserId(1L, 1L)).thenReturn(Optional.empty());
        when(registrationRepository.save(any(Registration.class))).thenReturn(testRegistration);

        // Act
        RegistrationDTO result = registrationService.registerForActivity(registrationDTO, "testuser");

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getActivityId());
        assertEquals(1L, result.getUserId());
        assertEquals("Test User", result.getParticipantName());
        assertEquals("13800138000", result.getContactPhone());
        verify(registrationRepository, times(1)).save(any(Registration.class));
    }

    @Test
    void testRegisterForActivity_UserNotFound() {
        // Arrange
        RegistrationDTO registrationDTO = RegistrationDTO.builder()
                .activityId(1L)
                .participantName("Test User")
                .contactPhone("13800138000")
                .build();

        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            registrationService.registerForActivity(registrationDTO, "nonexistent");
        });
        assertEquals(404, exception.getCode());
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testRegisterForActivity_ActivityNotFound() {
        // Arrange
        RegistrationDTO registrationDTO = RegistrationDTO.builder()
                .activityId(999L)
                .participantName("Test User")
                .contactPhone("13800138000")
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(activityRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            registrationService.registerForActivity(registrationDTO, "testuser");
        });
        assertEquals(404, exception.getCode());
        assertEquals("Activity not found", exception.getMessage());
    }

    @Test
    void testRegisterForActivity_ActivityNotAvailable() {
        // Arrange
        Activity rejectedActivity = Activity.builder()
                .organizerId(2L)
                .title("Rejected Activity")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(2))
                .status(Activity.ActivityStatus.REJECTED)
                .enableCrowdfunding(false)
                .build();
        rejectedActivity.setId(1L);

        RegistrationDTO registrationDTO = RegistrationDTO.builder()
                .activityId(1L)
                .participantName("Test User")
                .contactPhone("13800138000")
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(activityRepository.findById(1L)).thenReturn(Optional.of(rejectedActivity));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            registrationService.registerForActivity(registrationDTO, "testuser");
        });
        assertEquals(400, exception.getCode());
        assertEquals("Activity is not available for registration", exception.getMessage());
    }

    @Test
    void testRegisterForActivity_DeadlinePassed() {
        // Arrange
        Activity expiredActivity = Activity.builder()
                .organizerId(2L)
                .title("Expired Activity")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(2))
                .registrationDeadline(LocalDateTime.now().minusHours(1))
                .status(Activity.ActivityStatus.APPROVED)
                .enableCrowdfunding(false)
                .build();
        expiredActivity.setId(1L);

        RegistrationDTO registrationDTO = RegistrationDTO.builder()
                .activityId(1L)
                .participantName("Test User")
                .contactPhone("13800138000")
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(activityRepository.findById(1L)).thenReturn(Optional.of(expiredActivity));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            registrationService.registerForActivity(registrationDTO, "testuser");
        });
        assertEquals(400, exception.getCode());
        assertEquals("Registration deadline has passed", exception.getMessage());
    }

    @Test
    void testRegisterForActivity_MaxParticipantsReached() {
        // Arrange
        RegistrationDTO registrationDTO = RegistrationDTO.builder()
                .activityId(1L)
                .participantName("Test User")
                .contactPhone("13800138000")
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));
        when(registrationRepository.countByActivityIdAndStatus(1L, Registration.RegistrationStatus.REGISTERED))
                .thenReturn(50L); // Max participants reached

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            registrationService.registerForActivity(registrationDTO, "testuser");
        });
        assertEquals(400, exception.getCode());
        assertEquals("Activity has reached maximum participants", exception.getMessage());
    }

    @Test
    void testRegisterForActivity_DuplicateRegistration() {
        // Arrange
        RegistrationDTO registrationDTO = RegistrationDTO.builder()
                .activityId(1L)
                .participantName("Test User")
                .contactPhone("13800138000")
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));
        when(registrationRepository.countByActivityIdAndStatus(1L, Registration.RegistrationStatus.REGISTERED))
                .thenReturn(10L);
        when(registrationRepository.findByActivityIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(testRegistration));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            registrationService.registerForActivity(registrationDTO, "testuser");
        });
        assertEquals(409, exception.getCode());
        assertEquals("You are already registered for this activity", exception.getMessage());
    }

    @Test
    void testCancelRegistration_Success() {
        // Arrange
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(testRegistration));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));

        // Act
        registrationService.cancelRegistration(1L, "testuser");

        // Assert
        verify(registrationRepository, times(1)).save(any(Registration.class));
    }

    @Test
    void testCancelRegistration_RegistrationNotFound() {
        // Arrange
        when(registrationRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            registrationService.cancelRegistration(999L, "testuser");
        });
        assertEquals(404, exception.getCode());
        assertEquals("Registration not found", exception.getMessage());
    }

    @Test
    void testCancelRegistration_Unauthorized() {
        // Arrange
        User otherUser = User.builder()
                .username("otheruser")
                .email("other@example.com")
                .role(User.UserRole.USER)
                .status(User.AccountStatus.ACTIVE)
                .build();
        otherUser.setId(999L);

        when(registrationRepository.findById(1L)).thenReturn(Optional.of(testRegistration));
        when(userRepository.findByUsername("otheruser")).thenReturn(Optional.of(otherUser));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            registrationService.cancelRegistration(1L, "otheruser");
        });
        assertEquals(403, exception.getCode());
        assertEquals("You are not authorized to cancel this registration", exception.getMessage());
    }

    @Test
    void testGetUserRegistrations_Success() {
        // Arrange
        Registration registration2 = Registration.builder()
                .activityId(2L)
                .userId(testUser.getId())
                .status(Registration.RegistrationStatus.REGISTERED)
                .build();
        registration2.setId(2L);

        Page<Registration> registrationPage = new PageImpl<>(Arrays.asList(testRegistration, registration2));

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(registrationRepository.findByUserId(eq(1L), any(Pageable.class))).thenReturn(registrationPage);

        // Act
        Page<RegistrationDTO> result = registrationService.getUserRegistrations("testuser", 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(1L, result.getContent().get(0).getId());
        assertEquals(2L, result.getContent().get(1).getId());
    }

    @Test
    void testGetActivityRegistrations_Success() {
        // Arrange
        Registration registration2 = Registration.builder()
                .activityId(testActivity.getId())
                .userId(2L)
                .status(Registration.RegistrationStatus.REGISTERED)
                .build();
        registration2.setId(2L);

        Page<Registration> registrationPage = new PageImpl<>(Arrays.asList(testRegistration, registration2));

        when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));
        when(registrationRepository.findByActivityId(eq(1L), any(Pageable.class))).thenReturn(registrationPage);

        // Act
        Page<RegistrationDTO> result = registrationService.getActivityRegistrations(1L, 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(1L, result.getContent().get(0).getId());
        assertEquals(2L, result.getContent().get(1).getId());
    }

}
