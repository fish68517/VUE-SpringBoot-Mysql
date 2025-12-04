package com.shenyang.musicfestival.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response DTO for ticket order creation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketOrderResponse {

    /**
     * Order ID
     */
    private Long orderId;

    /**
     * List of created ticket IDs
     */
    private List<Long> ticketIds;

    /**
     * Total amount
     */
    private BigDecimal totalAmount;

    /**
     * Number of tickets
     */
    private Integer ticketCount;

    /**
     * Order status
     */
    private String status;

    /**
     * Message
     */
    private String message;

}
