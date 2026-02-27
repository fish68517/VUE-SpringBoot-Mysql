package com.agricultural.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderCreateRequest {
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "产品ID不能为空")
    private Long productId;

    @NotNull(message = "购买数量不能为空")
    private Integer quantity;

    private String deliveryAddress;
}