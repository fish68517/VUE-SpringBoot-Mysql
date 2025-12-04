package com.shenyang.musicfestival.service.impl;

import com.shenyang.musicfestival.dto.TicketOrderRequest;
import com.shenyang.musicfestival.dto.TicketOrderResponse;
import com.shenyang.musicfestival.entity.Order;
import com.shenyang.musicfestival.entity.Ticket;
import com.shenyang.musicfestival.entity.TicketSession;
import com.shenyang.musicfestival.entity.TicketZone;
import com.shenyang.musicfestival.exception.BusinessException;
import com.shenyang.musicfestival.repository.OrderRepository;
import com.shenyang.musicfestival.repository.TicketRepository;
import com.shenyang.musicfestival.repository.TicketSessionRepository;
import com.shenyang.musicfestival.repository.TicketZoneRepository;
import com.shenyang.musicfestival.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of TicketService
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketServiceImpl implements TicketService {

    private final TicketSessionRepository ticketSessionRepository;
    private final TicketZoneRepository ticketZoneRepository;
    private final TicketRepository ticketRepository;
    private final OrderRepository orderRepository;

    @Override
    public List<TicketSession> getAllSessions() {
        return ticketSessionRepository.findAll();
    }

    @Override
    public Optional<TicketSession> getSessionById(Long id) {
        return ticketSessionRepository.findById(id);
    }

    @Override
    public List<TicketSession> getSessionsByFestivalId(Long festivalId) {
        return ticketSessionRepository.findByFestivalId(festivalId);
    }

    @Override
    public List<TicketZone> getAllZones() {
        return ticketZoneRepository.findAll();
    }

    @Override
    public Optional<TicketZone> getZoneById(Long id) {
        return ticketZoneRepository.findById(id);
    }

    @Override
    public List<TicketZone> getZonesBySessionId(Long sessionId) {
        return ticketZoneRepository.findBySessionId(sessionId);
    }

    @Override
    @Transactional
    public TicketOrderResponse createTicketOrder(Long userId, TicketOrderRequest request) {
        // Validate buyer count (1-4 people)
        if (request.getBuyers() == null || request.getBuyers().isEmpty() || request.getBuyers().size() > 4) {
            throw new BusinessException("购票人数必须在1-4人之间");
        }

        // Get session and zone
        TicketSession session = ticketSessionRepository.findById(request.getSessionId())
            .orElseThrow(() -> new BusinessException("场次不存在"));

        TicketZone zone = ticketZoneRepository.findById(request.getZoneId())
            .orElseThrow(() -> new BusinessException("分区不存在"));

        // Validate zone belongs to session
        if (!zone.getSessionId().equals(request.getSessionId())) {
            throw new BusinessException("分区不属于该场次");
        }

        // Check inventory for all buyers
        int buyerCount = request.getBuyers().size();
        int remainingCapacity = zone.getTotalCapacity() - zone.getSoldCount();
        if (remainingCapacity < buyerCount) {
            throw new BusinessException(409, "该分区票量不足，剩余票数：" + remainingCapacity);
        }

        // Anti-scalping check: each ID number can only buy one ticket per session
        for (TicketOrderRequest.BuyerInfo buyer : request.getBuyers()) {
            if (ticketRepository.existsBySessionIdAndBuyerIdNumber(request.getSessionId(), buyer.getIdNumber())) {
                throw new BusinessException(409, "该身份证号已购票");
            }
        }

        // Create order
        BigDecimal totalAmount = zone.getPrice().multiply(BigDecimal.valueOf(buyerCount));
        Order order = Order.builder()
            .userId(userId)
            .orderType("ticket")
            .totalAmount(totalAmount)
            .status("paid")
            .build();
        order = orderRepository.save(order);

        // Create tickets for each buyer
        List<Long> ticketIds = new ArrayList<>();
        for (TicketOrderRequest.BuyerInfo buyer : request.getBuyers()) {
            Ticket ticket = Ticket.builder()
                .userId(userId)
                .sessionId(request.getSessionId())
                .zoneId(request.getZoneId())
                .buyerIdNumber(buyer.getIdNumber())
                .buyerName(buyer.getName())
                .status("valid")
                .build();
            ticket = ticketRepository.save(ticket);
            ticketIds.add(ticket.getId());
        }

        // Update zone inventory
        zone.setSoldCount(zone.getSoldCount() + buyerCount);
        ticketZoneRepository.save(zone);

        return TicketOrderResponse.builder()
            .orderId(order.getId())
            .ticketIds(ticketIds)
            .totalAmount(totalAmount)
            .ticketCount(buyerCount)
            .status("paid")
            .message("购票成功")
            .build();
    }

}
