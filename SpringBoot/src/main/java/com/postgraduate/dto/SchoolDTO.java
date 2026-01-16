package com.postgraduate.dto;

import com.postgraduate.entity.School;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for School information in API responses.
 * Contains essential school fields for display in search results and details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolDTO {

    private Long id;
    private String name;
    private String city;
    private String tier;
    private String website;
    private String intro;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static SchoolDTO fromEntity(School school) {
        if (school == null) {
            return null;
        }
        return SchoolDTO.builder()
                .id(school.getId())
                .name(school.getName())
                .city(school.getCity())
                .tier(school.getTier())
                .website(school.getWebsite())
                .intro(school.getIntro())
                .createdAt(school.getCreatedAt())
                .updatedAt(school.getUpdatedAt())
                .build();
    }

}
