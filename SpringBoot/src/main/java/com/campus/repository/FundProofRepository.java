package com.campus.repository;

import com.campus.entity.FundProof;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

/**
 * Fund Proof Repository
 * Provides data access methods for FundProof entity
 */
@Repository
public interface FundProofRepository extends BaseRepository<FundProof> {

    /**
     * Find fund proofs by activity ID
     */
    Page<FundProof> findByActivityId(Long activityId, Pageable pageable);

    /**
     * Find fund proofs by organizer ID
     */
    Page<FundProof> findByOrganizerId(Long organizerId, Pageable pageable);

    /**
     * Find fund proofs by status
     */
    Page<FundProof> findByStatus(FundProof.ProofStatus status, Pageable pageable);

    /**
     * Count fund proofs by status
     */
    long countByStatus(FundProof.ProofStatus status);

}
