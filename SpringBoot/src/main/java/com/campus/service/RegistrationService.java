package com.campus.service;

import com.campus.dto.RegistrationDTO;
import com.campus.entity.Activity;
import com.campus.entity.Registration;
import com.campus.entity.User;
import com.campus.exception.BusinessException;
import com.campus.repository.ActivityRepository;
import com.campus.repository.RegistrationRepository;
import com.campus.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Registration Service
 * Handles activity registration operations
 */
@Slf4j
@Service
@Transactional
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Register user for an activity
     * Validates registration deadline, participant limit, and duplicate registration
     * 
     * @param registrationDTO Registration data
     * @param username Username of the user registering
     * @return Created RegistrationDTO
     * @throws BusinessException if validation fails
     */
    public RegistrationDTO registerForActivity(RegistrationDTO registrationDTO, String username) {
        log.info("Registering user {} for activity {}", username, registrationDTO.getActivityId());

        // Find user
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            log.warn("User not found: {}", username);
            throw new BusinessException(404, "User not found");
        }

        User user = userOptional.get();

        // Find activity
        Optional<Activity> activityOptional = activityRepository.findById(registrationDTO.getActivityId());
        if (activityOptional.isEmpty()) {
            log.warn("Activity not found: {}", registrationDTO.getActivityId());
            throw new BusinessException(404, "Activity not found");
        }

        Activity activity = activityOptional.get();

        // Validate activity status
        if (activity.getStatus() != Activity.ActivityStatus.APPROVED && 
            activity.getStatus() != Activity.ActivityStatus.ONGOING) {
            log.warn("Activity {} is not available for registration", registrationDTO.getActivityId());
            throw new BusinessException(400, "Activity is not available for registration");
        }

        // Check registration deadline
        if (activity.getRegistrationDeadline() != null && 
            LocalDateTime.now().isAfter(activity.getRegistrationDeadline())) {
            log.warn("Registration deadline has passed for activity {}", registrationDTO.getActivityId());
            throw new BusinessException(400, "Registration deadline has passed");
        }

        // Check participant limit
        if (activity.getMaxParticipants() != null && activity.getMaxParticipants() > 0) {
            long registeredCount = registrationRepository.countByActivityIdAndStatus(
                    registrationDTO.getActivityId(),
                    Registration.RegistrationStatus.REGISTERED
            );
            if (registeredCount >= activity.getMaxParticipants()) {
                log.warn("Activity {} has reached maximum participants", registrationDTO.getActivityId());
                throw new BusinessException(400, "Activity has reached maximum participants");
            }
        }

        // Check for duplicate registration
        Optional<Registration> existingRegistration = registrationRepository.findByActivityIdAndUserId(
                registrationDTO.getActivityId(),
                user.getId()
        );

        if (existingRegistration.isPresent() && 
            existingRegistration.get().getStatus() == Registration.RegistrationStatus.REGISTERED) {
            log.warn("User {} is already registered for activity {}", username, registrationDTO.getActivityId());
            throw new BusinessException(409, "You are already registered for this activity");
        }

        // Create registration
        Registration registration = Registration.builder()
                .activityId(registrationDTO.getActivityId())
                .userId(user.getId())
                .status(Registration.RegistrationStatus.REGISTERED)
                .build();

        // Save registration
        Registration savedRegistration = registrationRepository.save(registration);
        log.info("User {} registered successfully for activity {}", username, registrationDTO.getActivityId());

        return RegistrationDTO.fromEntity(savedRegistration);
    }

    /**
     * Cancel registration for an activity
     * User can only cancel before registration deadline
     * 
     * @param registrationId Registration ID
     * @param username Username of the user cancelling
     * @throws BusinessException if validation fails
     */
    public void cancelRegistration(Long registrationId, String username) {
        log.info("Cancelling registration {} by user {}", registrationId, username);

        // Find registration
        Optional<Registration> registrationOptional = registrationRepository.findById(registrationId);
        if (registrationOptional.isEmpty()) {
            log.warn("Registration not found: {}", registrationId);
            throw new BusinessException(404, "Registration not found");
        }

        Registration registration = registrationOptional.get();

        // Find user
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            log.warn("User not found: {}", username);
            throw new BusinessException(404, "User not found");
        }

        User user = userOptional.get();

        // Check authorization
        if (!registration.getUserId().equals(user.getId())) {
            log.warn("User {} is not authorized to cancel registration {}", username, registrationId);
            throw new BusinessException(403, "You are not authorized to cancel this registration");
        }

        // Find activity
        Optional<Activity> activityOptional = activityRepository.findById(registration.getActivityId());
        if (activityOptional.isEmpty()) {
            log.warn("Activity not found: {}", registration.getActivityId());
            throw new BusinessException(404, "Activity not found");
        }

        Activity activity = activityOptional.get();

        // Check if cancellation is allowed (before registration deadline)
        if (activity.getRegistrationDeadline() != null && 
            LocalDateTime.now().isAfter(activity.getRegistrationDeadline())) {
            log.warn("Cannot cancel registration after deadline for activity {}", activity.getId());
            throw new BusinessException(400, "Cannot cancel registration after deadline. Please contact the organizer");
        }

        // Update registration status
        registration.setStatus(Registration.RegistrationStatus.CANCELLED);
        registrationRepository.save(registration);
        log.info("Registration {} cancelled successfully", registrationId);
    }

    /**
     * Get user's registrations
     * 
     * @param username Username
     * @param page Page number
     * @param size Page size
     * @return Page of RegistrationDTO
     */
    public Page<RegistrationDTO> getUserRegistrations(String username, int page, int size) {
        log.info("Fetching registrations for user {}", username);

        // Find user
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            log.warn("User not found: {}", username);
            throw new BusinessException(404, "User not found");
        }

        User user = userOptional.get();

        // Validate pagination
        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 10;

        Pageable pageable = PageRequest.of(page, size);

        // Get registrations
        Page<Registration> registrations = registrationRepository.findByUserId(user.getId(), pageable);

        return registrations.map(RegistrationDTO::fromEntity);
    }

    /**
     * Get activity registrations (for organizer/admin)
     * 
     * @param activityId Activity ID
     * @param page Page number
     * @param size Page size
     * @return Page of RegistrationDTO
     */
    public Page<RegistrationDTO> getActivityRegistrations(Long activityId, int page, int size) {
        log.info("Fetching registrations for activity {}", activityId);

        // Validate activity exists
        Optional<Activity> activityOptional = activityRepository.findById(activityId);
        if (activityOptional.isEmpty()) {
            log.warn("Activity not found: {}", activityId);
            throw new BusinessException(404, "Activity not found");
        }

        // Validate pagination
        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 10;

        Pageable pageable = PageRequest.of(page, size);

        // Get registrations
        Page<Registration> registrations = registrationRepository.findByActivityId(activityId, pageable);

        return registrations.map(RegistrationDTO::fromEntity);
    }

}
