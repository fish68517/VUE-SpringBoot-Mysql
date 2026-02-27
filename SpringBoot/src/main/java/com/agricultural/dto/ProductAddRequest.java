package com.agricultural.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductAddRequest {
    @NotBlank(message = "产品名称不能为空")
    private String productName;

    @NotBlank(message = "产品类别不能为空")
    private String category;

    private String description;

    @NotNull(message = "产品价格不能为空")
    private BigDecimal price;

    @NotNull(message = "库存数量不能为空")
    private Integer stock;

    @NotNull(message = "商家ID不能为空")
    private Long merchantId;

    private String applicableWeather;
}