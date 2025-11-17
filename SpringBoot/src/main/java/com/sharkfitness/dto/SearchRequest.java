package com.sharkfitness.dto;

import lombok.Data;

/**
 * DTO for search requests
 */
@Data
public class SearchRequest {
    
    /**
     * Search query string
     */
    private String query;
    
    /**
     * Optional filter for content type: "resource", "post", "coach", or "all"
     * Default is "all"
     */
    private String type = "all";
}
