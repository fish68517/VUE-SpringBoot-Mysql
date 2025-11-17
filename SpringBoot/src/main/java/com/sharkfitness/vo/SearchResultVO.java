package com.sharkfitness.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Value object for search results
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultVO {
    
    /**
     * Type of the result: "resource", "post", or "coach"
     */
    private String type;
    
    /**
     * ID of the result item
     */
    private Long id;
    
    /**
     * Title or name of the result
     */
    private String title;
    
    /**
     * Description or content preview
     */
    private String description;
    
    /**
     * Image URL (avatar for coaches, thumbnail for resources, first image for posts)
     */
    private String imageUrl;
    
    /**
     * Relevance score for ranking (higher is more relevant)
     */
    private Integer relevance;
}
