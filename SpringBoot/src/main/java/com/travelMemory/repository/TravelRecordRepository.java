package com.travelMemory.repository;

import com.travelMemory.entity.TravelRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TravelRecordRepository extends JpaRepository<TravelRecord, Long> {

    /**
     * Find travel records by user ID with pagination
     * @param userId the user ID
     * @param pageable pagination information
     * @return Page of travel records
     */
    Page<TravelRecord> findByUserId(Long userId, Pageable pageable);

    /**
     * Find a travel record by ID and user ID
     * @param id the record ID
     * @param userId the user ID
     * @return Optional containing the travel record if found
     */
    Optional<TravelRecord> findByIdAndUserId(Long id, Long userId);

    /**
     * Find public travel records with pagination
     * @param pageable pagination information
     * @return Page of public travel records
     */
    Page<TravelRecord> findByIsPublicTrue(Pageable pageable);

    /**
     * Count travel records by user ID
     * @param userId the user ID
     * @return the number of travel records
     */
    long countByUserId(Long userId);
}
