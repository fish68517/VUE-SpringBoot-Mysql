package com.shenyang.musicfestival.repository;

import com.shenyang.musicfestival.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Ticket entity
 */
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    /**
     * Find all tickets by user ID
     */
    List<Ticket> findByUserId(Long userId);

    /**
     * Find all tickets by session ID
     */
    List<Ticket> findBySessionId(Long sessionId);

    /**
     * Find ticket by session ID and buyer ID number (for anti-scalping check)
     */
    Optional<Ticket> findBySessionIdAndBuyerIdNumber(Long sessionId, String buyerIdNumber);

    /**
     * Check if a ticket exists for the given session and buyer ID number
     */
    boolean existsBySessionIdAndBuyerIdNumber(Long sessionId, String buyerIdNumber);

}
