package com.shenyang.musicfestival.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Data Transfer Object for Product
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {

    private Long id;

    private Long categoryId;

    private String name;

    private String description;

    private List<String> images;

    private BigDecimal originalPrice;

    private BigDecimal currentPrice;

    private Integer stock;

    @JsonProperty("specs")
    private Object specs;  // Can be any JSON object

    private Boolean isActive;

}
