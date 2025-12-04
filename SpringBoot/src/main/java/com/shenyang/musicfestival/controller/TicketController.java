package com.shenyang.musicfestival.controller;

import com.shenyang.musicfestival.dto.TicketOrderRequest;
import com.shenyang.musicfestival.dto.TicketOrderResponse;
import com.shenyang.musicfestival.dto.TicketSessionDTO;
import com.shenyang.musicfestival.dto.TicketZoneDTO;
import com.shenyang.musicfestival.entity.TicketSession;
import com.shenyang.musicfestival.entity.TicketZone;
import com.shenyang.musicfestival.exception.UnauthorizedException;
import com.shenyang.musicfestival.service.TicketService;
import com.shenyang.musicfestival.util.ApiResponse;
import com.shenyang.musicfestival.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for Ticket operations
 */
@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final JwtUtil jwtUtil;

    /**
     * Get all ticket sessions
     * GET /api/tickets/sessions
     */
    @GetMapping("/sessions")
    public ResponseEntity<ApiResponse<List<TicketSessionDTO>>> getAllSessions() {
        List<TicketSession> sessions = ticketService.getAllSessions();
        List<TicketSessionDTO> sessionDTOs = sessions.stream()
            .map(this::convertSessionToDTO)
            .toList();
        return ResponseEntity.ok(ApiResponse.success(sessionDTOs, "获取场次列表成功"));
    }

    /**
     * Get all ticket zones
     * GET /api/tickets/zones
     */
    @GetMapping("/zones")
    public ResponseEntity<ApiResponse<List<TicketZoneDTO>>> getAllZones() {
        List<TicketZone> zones = ticketService.getAllZones();
        List<TicketZoneDTO> zoneDTOs = zones.stream()
            .map(this::convertZoneToDTO)
            .toList();
        return ResponseEntity.ok(ApiResponse.success(zoneDTOs, "获取分区列表成功"));
    }

    /**
     * Get ticket zone by ID with inventory and price details
     * GET /api/tickets/zones/:id
     */
    @GetMapping("/zones/{id}")
    public ResponseEntity<ApiResponse<TicketZoneDTO>> getZoneById(@PathVariable Long id) {
        var zone = ticketService.getZoneById(id);
        if (zone.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("分区不存在"));
        }
        return ResponseEntity.ok(ApiResponse.success(convertZoneToDTO(zone.get()), "获取分区详情成功"));
    }

    /**
     * Create ticket order
     * POST /api/tickets/order
     */
    @PostMapping("/order")
    public ResponseEntity<ApiResponse<TicketOrderResponse>> createTicketOrder(
            @RequestHeader("Authorization") String token,
            @RequestBody TicketOrderRequest request) {
        // Extract user ID from token
        Long userId = extractUserIdFromToken(token);
        
        TicketOrderResponse response = ticketService.createTicketOrder(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(response, "购票成功"));
    }

    /**
     * Extract user ID from JWT token
     */
    private Long extractUserIdFromToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new UnauthorizedException("未授权");
        }
        
        // Remove "Bearer " prefix if present
        String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        
        if (!jwtUtil.validateToken(actualToken)) {
            throw new UnauthorizedException("无效的令牌");
        }
        
        return jwtUtil.extractUserId(actualToken);
    }

    /**
     * Convert TicketSession entity to TicketSessionDTO
     */
    private TicketSessionDTO convertSessionToDTO(TicketSession session) {
        return TicketSessionDTO.builder()
            .id(session.getId())
            .festivalId(session.getFestivalId())
            .name(session.getName())
            .startTime(session.getStartTime())
            .endTime(session.getEndTime())
            .status(session.getStatus())
            .build();
    }

    /**
     * Convert TicketZone entity to TicketZoneDTO
     */
    private TicketZoneDTO convertZoneToDTO(TicketZone zone) {
        Integer remainingCapacity = zone.getTotalCapacity() - zone.getSoldCount();
        return TicketZoneDTO.builder()
            .id(zone.getId())
            .sessionId(zone.getSessionId())
            .name(zone.getName())
            .totalCapacity(zone.getTotalCapacity())
            .soldCount(zone.getSoldCount())
            .remainingCapacity(remainingCapacity)
            .price(zone.getPrice())
            .build();
    }

}
