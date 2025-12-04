package com.shenyang.musicfestival.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for TicketZone
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketZoneDTO {

    private Long id;

    private Long sessionId;

    private String name;

    private Integer totalCapacity;

    private Integer soldCount;

    private Integer remainingCapacity;

    private BigDecimal price;

}
