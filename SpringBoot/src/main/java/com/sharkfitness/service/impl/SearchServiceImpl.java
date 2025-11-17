package com.sharkfitness.service.impl;

import com.sharkfitness.entity.Dynamic;
import com.sharkfitness.entity.FitnessResource;
import com.sharkfitness.entity.User;
import com.sharkfitness.repository.DynamicRepository;
import com.sharkfitness.repository.ResourceRepository;
import com.sharkfitness.repository.UserRepository;
import com.sharkfitness.service.SearchService;
import com.sharkfitness.vo.SearchResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of SearchService
 */
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    
    private final ResourceRepository resourceRepository;
    private final DynamicRepository dynamicRepository;
    private final UserRepository userRepository;
    
    @Override
    public List<SearchResultVO> searchAll(String query, String type) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<SearchResultVO> results = new ArrayList<>();
        String searchQuery = query.trim();
        
        // Search based on type filter
        if ("all".equalsIgnoreCase(type) || "resource".equalsIgnoreCase(type)) {
            results.addAll(searchResources(searchQuery));
        }
        
        if ("all".equalsIgnoreCase(type) || "post".equalsIgnoreCase(type)) {
            results.addAll(searchPosts(searchQuery));
        }
        
        if ("all".equalsIgnoreCase(type) || "coach".equalsIgnoreCase(type)) {
            results.addAll(searchCoaches(searchQuery));
        }
        
        // Sort by relevance (higher relevance first)
        results.sort(Comparator.comparing(SearchResultVO::getRelevance).reversed());
        
        return results;
    }
    
    /**
     * Search fitness resources by title and description
     */
    private List<SearchResultVO> searchResources(String query) {
        List<FitnessResource> resources = resourceRepository
                .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
        
        return resources.stream()
                .map(resource -> {
                    int relevance = calculateRelevance(query, resource.getTitle(), resource.getDescription());
                    
                    return SearchResultVO.builder()
                            .type("resource")
                            .id(resource.getId())
                            .title(resource.getTitle())
                            .description(truncateDescription(resource.getDescription()))
                            .imageUrl(resource.getFileUrl())
                            .relevance(relevance)
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Search dynamic posts by content
     */
    private List<SearchResultVO> searchPosts(String query) {
        // Only search approved posts
        List<Dynamic> dynamics = dynamicRepository
                .findByContentContainingIgnoreCaseAndStatus(query, "approved");
        
        return dynamics.stream()
                .map(dynamic -> {
                    int relevance = calculateRelevance(query, dynamic.getContent(), null);
                    
                    // Get first image URL if available
                    String imageUrl = null;
                    if (dynamic.getImageUrls() != null && !dynamic.getImageUrls().isEmpty()) {
                        String[] urls = dynamic.getImageUrls().split(",");
                        if (urls.length > 0) {
                            imageUrl = urls[0].trim();
                        }
                    }
                    
                    return SearchResultVO.builder()
                            .type("post")
                            .id(dynamic.getId())
                            .title("Post by " + dynamic.getUser().getUsername())
                            .description(truncateDescription(dynamic.getContent()))
                            .imageUrl(imageUrl)
                            .relevance(relevance)
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Search coaches by username and intro
     */
    private List<SearchResultVO> searchCoaches(String query) {
        // Search only users with "coach" role
        List<User> coaches = userRepository
                .findByRoleAndUsernameContainingIgnoreCaseOrRoleAndIntroContainingIgnoreCase(
                        "coach", query, "coach", query);
        
        return coaches.stream()
                .map(coach -> {
                    int relevance = calculateRelevance(query, coach.getUsername(), coach.getIntro());
                    
                    return SearchResultVO.builder()
                            .type("coach")
                            .id(coach.getId())
                            .title(coach.getUsername())
                            .description(truncateDescription(coach.getIntro()))
                            .imageUrl(coach.getAvatar())
                            .relevance(relevance)
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Calculate relevance score based on match quality
     * Exact matches get higher scores than partial matches
     */
    private int calculateRelevance(String query, String field1, String field2) {
        int relevance = 0;
        String lowerQuery = query.toLowerCase();
        
        // Check field1 (title/username/content)
        if (field1 != null) {
            String lowerField1 = field1.toLowerCase();
            if (lowerField1.equals(lowerQuery)) {
                relevance += 100; // Exact match
            } else if (lowerField1.startsWith(lowerQuery)) {
                relevance += 50; // Starts with query
            } else if (lowerField1.contains(lowerQuery)) {
                relevance += 25; // Contains query
            }
        }
        
        // Check field2 (description/intro)
        if (field2 != null) {
            String lowerField2 = field2.toLowerCase();
            if (lowerField2.equals(lowerQuery)) {
                relevance += 50; // Exact match in secondary field
            } else if (lowerField2.startsWith(lowerQuery)) {
                relevance += 25; // Starts with query
            } else if (lowerField2.contains(lowerQuery)) {
                relevance += 10; // Contains query
            }
        }
        
        // Default relevance if no match (shouldn't happen due to query)
        return relevance > 0 ? relevance : 1;
    }
    
    /**
     * Truncate description to a reasonable length for preview
     */
    private String truncateDescription(String description) {
        if (description == null) {
            return "";
        }
        
        int maxLength = 150;
        if (description.length() <= maxLength) {
            return description;
        }
        
        return description.substring(0, maxLength) + "...";
    }
}
