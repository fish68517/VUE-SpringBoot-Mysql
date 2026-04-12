package com.travelMemory.repository;

import com.travelMemory.entity.MultimediaFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MultimediaFileRepository extends JpaRepository<MultimediaFile, Long> {

    /**
     * Find all multimedia files for a travel record
     * @param travelRecordId the travel record ID
     * @return List of multimedia files
     */
    List<MultimediaFile> findByTravelRecordId(Long travelRecordId);

    /**
     * Delete all multimedia files for a travel record
     * @param travelRecordId the travel record ID
     */
    void deleteByTravelRecordId(Long travelRecordId);

    /**
     * Count multimedia files by user ID (through travel records)
     * @param userId the user ID
     * @return the number of multimedia files
     */
    @Query("SELECT COUNT(mf) FROM MultimediaFile mf JOIN TravelRecord tr ON mf.travelRecordId = tr.id WHERE tr.userId = :userId")
    long countByUserId(@Param("userId") Long userId);
}
