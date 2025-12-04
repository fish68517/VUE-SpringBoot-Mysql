package com.shenyang.musicfestival.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for Order
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {

    private Long id;

    private Long userId;

    private String orderType;

    private BigDecimal totalAmount;

    private String status;

    private String shippingAddress;

    private String trackingNumber;

    private List<OrderItemDTO> items;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
