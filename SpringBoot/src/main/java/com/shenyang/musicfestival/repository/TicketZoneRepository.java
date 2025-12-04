package com.shenyang.musicfestival.repository;

import com.shenyang.musicfestival.entity.TicketZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for TicketZone entity
 */
@Repository
public interface TicketZoneRepository extends JpaRepository<TicketZone, Long> {

    List<TicketZone> findBySessionId(Long sessionId);

}
