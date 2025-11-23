package com.xingluo.petshop.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单VO
 */
@Data
public class OrderVO {
    private Long id;
    private String orderNo;
    private Long userId;
    private Long shopId;
    private String shopName; // 额外字段
    private BigDecimal totalAmount;
    private Integer status;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String remark;
    private LocalDateTime payTime;
    private LocalDateTime shipTime;
    private LocalDateTime createTime;

    // 包含订单明细列表
    private List<OrderItemVO> orderItems;
}