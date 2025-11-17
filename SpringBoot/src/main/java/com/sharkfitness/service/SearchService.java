package com.sharkfitness.service;

import com.sharkfitness.vo.SearchResultVO;

import java.util.List;

/**
 * Service interface for search functionality
 */
public interface SearchService {
    
    /**
     * Search across all content types or filter by specific type
     * @param query the search query string
     * @param type the content type filter: "resource", "post", "coach", or "all"
     * @return list of search results organized by relevance
     */
    List<SearchResultVO> searchAll(String query, String type);
}
