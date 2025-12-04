package com.shenyang.musicfestival.repository;

import com.shenyang.musicfestival.entity.TicketSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for TicketSession entity
 */
@Repository
public interface TicketSessionRepository extends JpaRepository<TicketSession, Long> {

    List<TicketSession> findByFestivalId(Long festivalId);

    List<TicketSession> findByStatus(String status);

}
