package com.shenyang.musicfestival.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for creating ticket orders
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketOrderRequest {

    /**
     * Session ID
     */
    private Long sessionId;

    /**
     * Zone ID
     */
    private Long zoneId;

    /**
     * List of buyers (support 1-4 people)
     */
    private List<BuyerInfo> buyers;

    /**
     * Buyer information
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BuyerInfo {
        /**
         * Buyer's ID number (身份证号)
         */
        private String idNumber;

        /**
         * Buyer's name
         */
        private String name;
    }

}
