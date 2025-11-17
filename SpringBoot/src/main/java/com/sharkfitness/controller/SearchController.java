package com.sharkfitness.controller;

import com.sharkfitness.service.SearchService;
import com.sharkfitness.vo.ApiResponse;
import com.sharkfitness.vo.SearchResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for search functionality
 */
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {
    
    private final SearchService searchService;
    
    /**
     * Search across content types
     * 
     * @param query the search query string
     * @param type optional filter: "resource", "post", "coach", or "all" (default)
     * @return unified response with results organized by type
     */
    @GetMapping
    public ApiResponse<Map<String, Object>> search(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "all") String type) {
        
        // Handle empty query
        if (query == null || query.trim().isEmpty()) {
            Map<String, Object> emptyResult = new HashMap<>();
            emptyResult.put("query", "");
            emptyResult.put("type", type);
            emptyResult.put("totalResults", 0);
            emptyResult.put("results", List.of());
            emptyResult.put("resultsByType", Map.of(
                    "resources", List.of(),
                    "posts", List.of(),
                    "coaches", List.of()
            ));
            return ApiResponse.success(emptyResult);
        }
        
        // Perform search
        List<SearchResultVO> allResults = searchService.searchAll(query, type);
        
        // Organize results by type
        Map<String, List<SearchResultVO>> resultsByType = new HashMap<>();
        resultsByType.put("resources", allResults.stream()
                .filter(r -> "resource".equals(r.getType()))
                .collect(Collectors.toList()));
        resultsByType.put("posts", allResults.stream()
                .filter(r -> "post".equals(r.getType()))
                .collect(Collectors.toList()));
        resultsByType.put("coaches", allResults.stream()
                .filter(r -> "coach".equals(r.getType()))
                .collect(Collectors.toList()));
        
        // Build response
        Map<String, Object> response = new HashMap<>();
        response.put("query", query.trim());
        response.put("type", type);
        response.put("totalResults", allResults.size());
        response.put("results", allResults);
        response.put("resultsByType", resultsByType);
        
        return ApiResponse.success(response);
    }
}
