package com.campus.service;

import com.campus.dto.ActivityDTO;
import com.campus.entity.Activity;
import com.campus.entity.CrowdfundingSupport;
import com.campus.entity.Registration;
import com.campus.entity.User;
import com.campus.exception.BusinessException;
import com.campus.repository.ActivityRepository;
import com.campus.repository.CrowdfundingSupportRepository;
import com.campus.repository.RegistrationRepository;
import com.campus.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Activity Service
 * Handles activity management operations
 */
@Slf4j
@Service
@Transactional
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private CrowdfundingSupportRepository crowdfundingSupportRepository;

    /**
     * Create a new activity
     * Activity is saved as PENDING_AUDIT status
     * 
     * @param activityDTO Activity data transfer object
     * @param organizerUsername Username of the organizer
     * @return Created ActivityDTO
     * @throws BusinessException if organizer not found or validation fails
     */
    public ActivityDTO createActivity(ActivityDTO activityDTO, String organizerUsername) {
        log.info("Creating activity: {} by organizer: {}", activityDTO.getTitle(), organizerUsername);

        // Find organizer
        Optional<User> organizerOptional = userRepository.findByUsername(organizerUsername);
        if (organizerOptional.isEmpty()) {
            log.warn("Organizer not found: {}", organizerUsername);
            throw new BusinessException(404, "Organizer not found");
        }

        User organizer = organizerOptional.get();

        // Validate organizer role
        if (organizer.getRole() != User.UserRole.ORGANIZER && organizer.getRole() != User.UserRole.ADMIN) {
            log.warn("User {} is not an organizer", organizerUsername);
            throw new BusinessException(403, "Only organizers can create activities");
        }

        // Validate activity data
        if (activityDTO.getTitle() == null || activityDTO.getTitle().isEmpty()) {
            throw new BusinessException(400, "Activity title is required");
        }

        if (activityDTO.getStartTime() == null || activityDTO.getEndTime() == null) {
            throw new BusinessException(400, "Activity start time and end time are required");
        }

        if (activityDTO.getStartTime().isAfter(activityDTO.getEndTime())) {
            throw new BusinessException(400, "Activity start time must be before end time");
        }

        // Create activity entity
        Activity activity = Activity.builder()
                .organizerId(organizer.getId())
                .title(activityDTO.getTitle())
                .description(activityDTO.getDescription())
                .type(activityDTO.getType())
                .coverUrl(activityDTO.getCoverUrl())
                .startTime(activityDTO.getStartTime())
                .endTime(activityDTO.getEndTime())
                .location(activityDTO.getLocation())
                .maxParticipants(activityDTO.getMaxParticipants())
                .registrationDeadline(activityDTO.getRegistrationDeadline())
                .enableCrowdfunding(activityDTO.getEnableCrowdfunding() != null && activityDTO.getEnableCrowdfunding())
                .crowdfundingTarget(activityDTO.getCrowdfundingTarget())
                .status(Activity.ActivityStatus.PENDING_AUDIT)
                .build();

        // Save activity
        Activity savedActivity = activityRepository.save(activity);
        log.info("Activity created successfully with ID: {}", savedActivity.getId());

        return ActivityDTO.fromEntity(savedActivity);
    }

    /**
     * Update an existing activity
     * Only the organizer who created the activity can edit it
     * 
     * @param activityId Activity ID
     * @param activityDTO Updated activity data
     * @param organizerUsername Username of the organizer
     * @return Updated ActivityDTO
     * @throws BusinessException if activity not found, unauthorized, or validation fails
     */
    public ActivityDTO updateActivity(Long activityId, ActivityDTO activityDTO, String organizerUsername) {
        log.info("Updating activity: {} by organizer: {}", activityId, organizerUsername);

        // Find activity
        Optional<Activity> activityOptional = activityRepository.findById(activityId);
        if (activityOptional.isEmpty()) {
            log.warn("Activity not found: {}", activityId);
            throw new BusinessException(404, "Activity not found");
        }

        Activity activity = activityOptional.get();

        // Find organizer
        Optional<User> organizerOptional = userRepository.findByUsername(organizerUsername);
        if (organizerOptional.isEmpty()) {
            log.warn("Organizer not found: {}", organizerUsername);
            throw new BusinessException(404, "Organizer not found");
        }

        User organizer = organizerOptional.get();

        // Check authorization - only the organizer who created the activity can edit it
        if (!activity.getOrganizerId().equals(organizer.getId())) {
            log.warn("User {} is not authorized to edit activity {}", organizerUsername, activityId);
            throw new BusinessException(403, "You are not authorized to edit this activity");
        }

        // Validate activity data
        if (activityDTO.getTitle() != null && !activityDTO.getTitle().isEmpty()) {
            activity.setTitle(activityDTO.getTitle());
        }

        if (activityDTO.getDescription() != null) {
            activity.setDescription(activityDTO.getDescription());
        }

        if (activityDTO.getType() != null) {
            activity.setType(activityDTO.getType());
        }

        if (activityDTO.getCoverUrl() != null) {
            activity.setCoverUrl(activityDTO.getCoverUrl());
        }

        if (activityDTO.getStartTime() != null) {
            activity.setStartTime(activityDTO.getStartTime());
        }

        if (activityDTO.getEndTime() != null) {
            activity.setEndTime(activityDTO.getEndTime());
        }

        // Validate time range
        if (activity.getStartTime().isAfter(activity.getEndTime())) {
            throw new BusinessException(400, "Activity start time must be before end time");
        }

        if (activityDTO.getLocation() != null) {
            activity.setLocation(activityDTO.getLocation());
        }

        if (activityDTO.getMaxParticipants() != null) {
            activity.setMaxParticipants(activityDTO.getMaxParticipants());
        }

        if (activityDTO.getRegistrationDeadline() != null) {
            activity.setRegistrationDeadline(activityDTO.getRegistrationDeadline());
        }

        if (activityDTO.getEnableCrowdfunding() != null) {
            activity.setEnableCrowdfunding(activityDTO.getEnableCrowdfunding());
        }

        if (activityDTO.getCrowdfundingTarget() != null) {
            activity.setCrowdfundingTarget(activityDTO.getCrowdfundingTarget());
        }

        // Save updated activity
        Activity updatedActivity = activityRepository.save(activity);
        log.info("Activity updated successfully: {}", activityId);

        return ActivityDTO.fromEntity(updatedActivity);
    }

    /**
     * Delete an activity
     * Only the organizer who created the activity can delete it
     * 
     * @param activityId Activity ID
     * @param organizerUsername Username of the organizer
     * @throws BusinessException if activity not found or unauthorized
     */
    public void deleteActivity(Long activityId, String organizerUsername) {
        log.info("Deleting activity: {} by organizer: {}", activityId, organizerUsername);

        // Find activity
        Optional<Activity> activityOptional = activityRepository.findById(activityId);
        if (activityOptional.isEmpty()) {
            log.warn("Activity not found: {}", activityId);
            throw new BusinessException(404, "Activity not found");
        }

        Activity activity = activityOptional.get();

        // Find organizer
        Optional<User> organizerOptional = userRepository.findByUsername(organizerUsername);
        if (organizerOptional.isEmpty()) {
            log.warn("Organizer not found: {}", organizerUsername);
            throw new BusinessException(404, "Organizer not found");
        }

        User organizer = organizerOptional.get();

        // Check authorization - only the organizer who created the activity can delete it
        if (!activity.getOrganizerId().equals(organizer.getId())) {
            log.warn("User {} is not authorized to delete activity {}", organizerUsername, activityId);
            throw new BusinessException(403, "You are not authorized to delete this activity");
        }

        // Delete activity
        activityRepository.delete(activity);
        log.info("Activity deleted successfully: {}", activityId);
    }

    /**
     * Get activity details by ID
     * 
     * @param activityId Activity ID
     * @return ActivityDTO
     * @throws BusinessException if activity not found
     */
    public ActivityDTO getActivityById(Long activityId) {
        log.info("Fetching activity: {}", activityId);

        Optional<Activity> activityOptional = activityRepository.findById(activityId);
        if (activityOptional.isEmpty()) {
            log.warn("Activity not found: {}", activityId);
            throw new BusinessException(404, "Activity not found");
        }

        ActivityDTO activityDTO = ActivityDTO.fromEntity(activityOptional.get());
        enrichActivityDTOWithCounts(activityDTO);
        return activityDTO;
    }

    /**
     * Enrich ActivityDTO with registration and crowdfunding counts
     * 
     * @param activityDTO Activity DTO to enrich
     */
    private void enrichActivityDTOWithCounts(ActivityDTO activityDTO) {
        if (activityDTO == null || activityDTO.getId() == null) {
            return;
        }

        // Get registration count
        long registrationCount = registrationRepository.countByActivityIdAndStatus(
                activityDTO.getId(), 
                Registration.RegistrationStatus.REGISTERED
        );
        activityDTO.setRegistrationCount(registrationCount);

        // Get crowdfunding amount if enabled
        if (activityDTO.getEnableCrowdfunding() != null && activityDTO.getEnableCrowdfunding()) {
            activityDTO.setCrowdfundingAmount(
                    crowdfundingSupportRepository.getTotalAmountByActivityId(activityDTO.getId())
            );
        }
    }

    /**
     * Get activity list with filtering and pagination
     * Only returns approved activities
     * 
     * @param page Page number (0-indexed)
     * @param size Page size
     * @param type Activity type filter (optional)
     * @param sortBy Sort field: "startTime", "popularity", "recent" (default: "startTime")
     * @param startDate Start date filter (optional)
     * @param endDate End date filter (optional)
     * @return Page of ActivityDTO
     */
    public Page<ActivityDTO> getActivityList(int page, int size, String type, String sortBy, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching activity list - page: {}, size: {}, type: {}, sortBy: {}", page, size, type, sortBy);

        // Validate pagination parameters
        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 10;

        // Create pageable with sorting
        Pageable pageable = createPageable(page, size, sortBy);

        // Query activities based on filters
        Page<Activity> activityPage;

        if (startDate != null && endDate != null) {
            // Filter by time range
            if (type != null && !type.isEmpty()) {
                // Time range + type filter
                activityPage = activityRepository.findByStatusAndType(Activity.ActivityStatus.APPROVED, type, pageable);
            } else {
                // Time range only
                activityPage = activityRepository.findApprovedActivitiesByTimeRange(
                        Activity.ActivityStatus.APPROVED, startDate, endDate, pageable);
            }
        } else if (type != null && !type.isEmpty()) {
            // Type filter only
            activityPage = activityRepository.findByStatusAndType(Activity.ActivityStatus.APPROVED, type, pageable);
        } else if ("popularity".equals(sortBy)) {
            // Sort by popularity (registration count)
            activityPage = activityRepository.findApprovedActivitiesOrderByPopularity(Activity.ActivityStatus.APPROVED, pageable);
        } else {
            // Default: all approved activities
            activityPage = activityRepository.findByStatus(Activity.ActivityStatus.APPROVED, pageable);
        }

        // Convert to DTO and enrich with counts
        return activityPage.map(activity -> {
            ActivityDTO dto = ActivityDTO.fromEntity(activity);
            enrichActivityDTOWithCounts(dto);
            return dto;
        });
    }

    /**
     * Create pageable with sorting
     * 
     * @param page Page number
     * @param size Page size
     * @param sortBy Sort field
     * @return Pageable
     */
    private Pageable createPageable(int page, int size, String sortBy) {
        Sort sort;

        if ("popularity".equals(sortBy)) {
            // Popularity sorting is handled in query
            sort = Sort.by(Sort.Direction.DESC, "id");
        } else if ("recent".equals(sortBy)) {
            // Recent activities (newest first)
            sort = Sort.by(Sort.Direction.DESC, "createdAt");
        } else {
            // Default: sort by start time ascending
            sort = Sort.by(Sort.Direction.ASC, "startTime");
        }

        return PageRequest.of(page, size, sort);
    }

}
