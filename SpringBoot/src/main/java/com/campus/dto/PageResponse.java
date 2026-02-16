package com.campus.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Paginated Response DTO
 * Used for returning paginated data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResponse<T> {

    /**
     * Current page number (0-indexed)
     */
    private Integer pageNumber;

    /**
     * Page size
     */
    private Integer pageSize;

    /**
     * Total number of elements
     */
    private Long totalElements;

    /**
     * Total number of pages
     */
    private Integer totalPages;

    /**
     * Whether this is the first page
     */
    private Boolean isFirst;

    /**
     * Whether this is the last page
     */
    private Boolean isLast;

    /**
     * List of items in current page
     */
    private List<T> content;

    /**
     * Create PageResponse from Spring Data Page
     */
    public static <T> PageResponse<T> fromPage(Page<T> page) {
        return PageResponse.<T>builder()
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .content(page.getContent())
                .build();
    }

}
