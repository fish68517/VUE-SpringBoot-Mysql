package com.campus.repository;

import com.campus.entity.Registration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Registration Repository
 * Provides data access methods for Registration entity
 */
@Repository
public interface RegistrationRepository extends BaseRepository<Registration> {

    /**
     * Find registration by activity ID and user ID
     */
    Optional<Registration> findByActivityIdAndUserId(Long activityId, Long userId);

    /**
     * Find registrations by activity ID
     */
    Page<Registration> findByActivityId(Long activityId, Pageable pageable);

    /**
     * Find registrations by user ID
     */
    Page<Registration> findByUserId(Long userId, Pageable pageable);

    /**
     * Count registrations for an activity
     */
    long countByActivityIdAndStatus(Long activityId, Registration.RegistrationStatus status);

    /**
     * Check if user is registered for activity
     */
    boolean existsByActivityIdAndUserIdAndStatus(Long activityId, Long userId, Registration.RegistrationStatus status);

    /**
     * Count registrations by status
     */
    long countByStatus(Registration.RegistrationStatus status);

}
