package com.shenyang.musicfestival.service;

import com.shenyang.musicfestival.dto.TicketOrderRequest;
import com.shenyang.musicfestival.dto.TicketOrderResponse;
import com.shenyang.musicfestival.entity.TicketSession;
import com.shenyang.musicfestival.entity.TicketZone;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for Ticket operations
 */
public interface TicketService {

    /**
     * Get all ticket sessions
     */
    List<TicketSession> getAllSessions();

    /**
     * Get ticket session by ID
     */
    Optional<TicketSession> getSessionById(Long id);

    /**
     * Get all ticket sessions by festival ID
     */
    List<TicketSession> getSessionsByFestivalId(Long festivalId);

    /**
     * Get all ticket zones
     */
    List<TicketZone> getAllZones();

    /**
     * Get ticket zone by ID
     */
    Optional<TicketZone> getZoneById(Long id);

    /**
     * Get all ticket zones by session ID
     */
    List<TicketZone> getZonesBySessionId(Long sessionId);

    /**
     * Create ticket order
     * Validates anti-scalping (one ID number per session), inventory, and buyer count (max 4)
     */
    TicketOrderResponse createTicketOrder(Long userId, TicketOrderRequest request);

}
