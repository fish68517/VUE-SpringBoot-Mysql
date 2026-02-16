package com.campus.repository;

import com.campus.entity.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Activity Repository
 * Provides data access methods for Activity entity
 */
@Repository
public interface ActivityRepository extends BaseRepository<Activity> {

    /**
     * Find activities by organizer ID
     */
    Page<Activity> findByOrganizerId(Long organizerId, Pageable pageable);

    /**
     * Find activities by status
     */
    Page<Activity> findByStatus(Activity.ActivityStatus status, Pageable pageable);

    /**
     * Find activities by type
     */
    Page<Activity> findByType(String type, Pageable pageable);

    /**
     * Find activities by title containing
     */
    Page<Activity> findByTitleContaining(String title, Pageable pageable);

    /**
     * Find activities within time range
     */
    @Query("SELECT a FROM Activity a WHERE a.startTime >= :startTime AND a.startTime <= :endTime")
    Page<Activity> findActivitiesByTimeRange(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable);

    /**
     * Find approved activities
     */
    List<Activity> findByStatus(Activity.ActivityStatus status);

    /**
     * Find activities by status and type
     */
    Page<Activity> findByStatusAndType(Activity.ActivityStatus status, String type, Pageable pageable);

    /**
     * Find activities by status and title containing
     */
    Page<Activity> findByStatusAndTitleContaining(Activity.ActivityStatus status, String title, Pageable pageable);

    /**
     * Find activities by status within time range
     */
    @Query("SELECT a FROM Activity a WHERE a.status = :status AND a.startTime >= :startTime AND a.startTime <= :endTime ORDER BY a.startTime ASC")
    Page<Activity> findApprovedActivitiesByTimeRange(
            @Param("status") Activity.ActivityStatus status,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable);

    /**
     * Find approved activities ordered by registration count (popularity)
     */
    @Query("SELECT a FROM Activity a LEFT JOIN Registration r ON a.id = r.activityId WHERE a.status = :status GROUP BY a.id ORDER BY COUNT(r.id) DESC")
    Page<Activity> findApprovedActivitiesOrderByPopularity(
            @Param("status") Activity.ActivityStatus status,
            Pageable pageable);

    /**
     * Count activities by status
     */
    long countByStatus(Activity.ActivityStatus status);

}
