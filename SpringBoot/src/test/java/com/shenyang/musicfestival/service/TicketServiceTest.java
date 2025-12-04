package com.shenyang.musicfestival.service;

import com.shenyang.musicfestival.dto.TicketOrderRequest;
import com.shenyang.musicfestival.dto.TicketOrderResponse;
import com.shenyang.musicfestival.entity.Festival;
import com.shenyang.musicfestival.entity.TicketSession;
import com.shenyang.musicfestival.entity.TicketZone;
import com.shenyang.musicfestival.exception.BusinessException;
import com.shenyang.musicfestival.repository.FestivalRepository;
import com.shenyang.musicfestival.repository.TicketRepository;
import com.shenyang.musicfestival.repository.TicketSessionRepository;
import com.shenyang.musicfestival.repository.TicketZoneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for TicketService
 */
@SpringBootTest
class TicketServiceTest {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketSessionRepository ticketSessionRepository;

    @Autowired
    private TicketZoneRepository ticketZoneRepository;

    @Autowired
    private FestivalRepository festivalRepository;

    private TicketSession testSession;
    private TicketZone testZone;
    private Long testUserId = 1L;

    @BeforeEach
    void setUp() {
        // Clean up
        ticketRepository.deleteAll();
        ticketZoneRepository.deleteAll();
        ticketSessionRepository.deleteAll();
        festivalRepository.deleteAll();

        // Create test festival
        Festival festival = Festival.builder()
            .name("沈阳音乐节")
            .startDate(LocalDateTime.now().plusDays(1))
            .endDate(LocalDateTime.now().plusDays(3))
            .location("沈阳")
            .status("ongoing")
            .build();
        festival = festivalRepository.save(festival);

        // Create test session
        testSession = TicketSession.builder()
            .festivalId(festival.getId())
            .name("第一天")
            .startTime(LocalDateTime.now().plusDays(1))
            .endTime(LocalDateTime.now().plusDays(1).plusHours(8))
            .status("available")
            .build();
        testSession = ticketSessionRepository.save(testSession);

        // Create test zone
        testZone = TicketZone.builder()
            .sessionId(testSession.getId())
            .name("A区")
            .totalCapacity(100)
            .soldCount(0)
            .price(BigDecimal.valueOf(299.00))
            .build();
        testZone = ticketZoneRepository.save(testZone);
    }

    @Test
    void testCreateTicketOrderSuccess() {
        // Create request with 2 buyers
        TicketOrderRequest request = TicketOrderRequest.builder()
            .sessionId(testSession.getId())
            .zoneId(testZone.getId())
            .buyers(List.of(
                TicketOrderRequest.BuyerInfo.builder()
                    .idNumber("110101199003071234")
                    .name("张三")
                    .build(),
                TicketOrderRequest.BuyerInfo.builder()
                    .idNumber("110101199003071235")
                    .name("李四")
                    .build()
            ))
            .build();

        TicketOrderResponse response = ticketService.createTicketOrder(testUserId, request);

        assertNotNull(response);
        assertEquals(2, response.getTicketCount());
        assertEquals(BigDecimal.valueOf(598.00), response.getTotalAmount());
        assertEquals("paid", response.getStatus());
        assertEquals(2, response.getTicketIds().size());

        // Verify zone inventory updated
        TicketZone updatedZone = ticketZoneRepository.findById(testZone.getId()).get();
        assertEquals(2, updatedZone.getSoldCount());
    }

    @Test
    void testCreateTicketOrderAntiScalping() {
        // First purchase
        TicketOrderRequest request1 = TicketOrderRequest.builder()
            .sessionId(testSession.getId())
            .zoneId(testZone.getId())
            .buyers(List.of(
                TicketOrderRequest.BuyerInfo.builder()
                    .idNumber("110101199003071234")
                    .name("张三")
                    .build()
            ))
            .build();

        ticketService.createTicketOrder(testUserId, request1);

        // Second purchase with same ID number should fail
        TicketOrderRequest request2 = TicketOrderRequest.builder()
            .sessionId(testSession.getId())
            .zoneId(testZone.getId())
            .buyers(List.of(
                TicketOrderRequest.BuyerInfo.builder()
                    .idNumber("110101199003071234")
                    .name("张三")
                    .build()
            ))
            .build();

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            ticketService.createTicketOrder(testUserId, request2);
        });

        assertEquals("该身份证号已购票", exception.getMessage());
        assertEquals(409, exception.getCode());
    }

    @Test
    void testCreateTicketOrderInsufficientInventory() {
        // Create zone with limited capacity
        TicketZone limitedZone = TicketZone.builder()
            .sessionId(testSession.getId())
            .name("B区")
            .totalCapacity(2)
            .soldCount(1)
            .price(BigDecimal.valueOf(199.00))
            .build();
        limitedZone = ticketZoneRepository.save(limitedZone);

        // Try to buy 2 tickets when only 1 is available
        TicketOrderRequest request = TicketOrderRequest.builder()
            .sessionId(testSession.getId())
            .zoneId(limitedZone.getId())
            .buyers(List.of(
                TicketOrderRequest.BuyerInfo.builder()
                    .idNumber("110101199003071234")
                    .name("张三")
                    .build(),
                TicketOrderRequest.BuyerInfo.builder()
                    .idNumber("110101199003071235")
                    .name("李四")
                    .build()
            ))
            .build();

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            ticketService.createTicketOrder(testUserId, request);
        });

        assertTrue(exception.getMessage().contains("票量不足"));
        assertEquals(409, exception.getCode());
    }

    @Test
    void testCreateTicketOrderMaxBuyersExceeded() {
        // Try to buy 5 tickets (max is 4)
        List<TicketOrderRequest.BuyerInfo> buyers = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            buyers.add(TicketOrderRequest.BuyerInfo.builder()
                .idNumber("11010119900307123" + i)
                .name("购票人" + i)
                .build());
        }

        TicketOrderRequest request = TicketOrderRequest.builder()
            .sessionId(testSession.getId())
            .zoneId(testZone.getId())
            .buyers(buyers)
            .build();

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            ticketService.createTicketOrder(testUserId, request);
        });

        assertEquals("购票人数必须在1-4人之间", exception.getMessage());
    }

    @Test
    void testCreateTicketOrderNoBuyers() {
        TicketOrderRequest request = TicketOrderRequest.builder()
            .sessionId(testSession.getId())
            .zoneId(testZone.getId())
            .buyers(List.of())
            .build();

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            ticketService.createTicketOrder(testUserId, request);
        });

        assertEquals("购票人数必须在1-4人之间", exception.getMessage());
    }

    @Test
    void testCreateTicketOrderInvalidSession() {
        TicketOrderRequest request = TicketOrderRequest.builder()
            .sessionId(99999L)
            .zoneId(testZone.getId())
            .buyers(List.of(
                TicketOrderRequest.BuyerInfo.builder()
                    .idNumber("110101199003071234")
                    .name("张三")
                    .build()
            ))
            .build();

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            ticketService.createTicketOrder(testUserId, request);
        });

        assertEquals("场次不存在", exception.getMessage());
    }

    @Test
    void testCreateTicketOrderInvalidZone() {
        TicketOrderRequest request = TicketOrderRequest.builder()
            .sessionId(testSession.getId())
            .zoneId(99999L)
            .buyers(List.of(
                TicketOrderRequest.BuyerInfo.builder()
                    .idNumber("110101199003071234")
                    .name("张三")
                    .build()
            ))
            .build();

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            ticketService.createTicketOrder(testUserId, request);
        });

        assertEquals("分区不存在", exception.getMessage());
    }

}
