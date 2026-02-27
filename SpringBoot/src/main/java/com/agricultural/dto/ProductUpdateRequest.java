package com.agricultural.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductUpdateRequest {
    private String productName;
    private String description;
    private BigDecimal price;
    private String applicableWeather;
}