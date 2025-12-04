package com.shenyang.musicfestival.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Data Transfer Object for Shopping Cart
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDTO {

    private List<CartItemDTO> items;

    private BigDecimal totalPrice;

    private Integer totalQuantity;

}
