package com.shenyang.musicfestival.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for TicketSession
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketSessionDTO {

    private Long id;

    private Long festivalId;

    private String name;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String status;

}
