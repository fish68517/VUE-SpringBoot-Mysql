package com.travelMemory.repository;

import com.travelMemory.entity.MapFootprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MapFootprintRepository extends JpaRepository<MapFootprint, Long> {

    /**
     * Find all map footprints for a travel record
     * @param travelRecordId the travel record ID
     * @return List of map footprints
     */
    List<MapFootprint> findByTravelRecordId(Long travelRecordId);

    /**
     * Delete all map footprints for a travel record
     * @param travelRecordId the travel record ID
     */
    void deleteByTravelRecordId(Long travelRecordId);

    /**
     * Count map footprints by user ID (through travel records)
     * @param userId the user ID
     * @return the number of map footprints
     */
    @Query("SELECT COUNT(mf) FROM MapFootprint mf JOIN TravelRecord tr ON mf.travelRecordId = tr.id WHERE tr.userId = :userId")
    long countByUserId(@Param("userId") Long userId);
}
