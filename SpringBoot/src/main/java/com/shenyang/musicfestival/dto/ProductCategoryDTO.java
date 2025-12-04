package com.shenyang.musicfestival.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for ProductCategory
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCategoryDTO {

    private Long id;

    private String name;

    private String description;

}
