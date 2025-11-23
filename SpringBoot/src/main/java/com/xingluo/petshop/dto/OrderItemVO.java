package com.xingluo.petshop.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 订单明细VO
 */
@Data
public class OrderItemVO {
    private Long id;
    private Long orderId;
    private Long productId;
    private String productName;
    private String productImage;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
}