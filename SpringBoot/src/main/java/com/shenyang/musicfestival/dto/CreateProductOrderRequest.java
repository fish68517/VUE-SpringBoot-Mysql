package com.shenyang.musicfestival.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for creating a product order
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductOrderRequest {

    /**
     * List of cart item IDs to order
     */
    private List<Long> cartItemIds;

    /**
     * Shipping address
     */
    private String shippingAddress;

}
