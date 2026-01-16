package com.postgraduate.dto;

import com.postgraduate.entity.Announcement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for displaying announcement information.
 * Used for both admin and public announcement endpoints.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnouncementDTO {

    private Long id;

    private String title;

    private String content;

    private String status;

    private Integer sortOrder;

    private LocalDateTime publishAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /**
     * Convert Announcement entity to AnnouncementDTO.
     *
     * @param announcement the announcement entity
     * @return AnnouncementDTO containing announcement information
     */
    public static AnnouncementDTO fromEntity(Announcement announcement) {
        return AnnouncementDTO.builder()
                .id(announcement.getId())
                .title(announcement.getTitle())
                .content(announcement.getContent())
                .status(announcement.getStatus().toString())
                .sortOrder(announcement.getSortOrder())
                .publishAt(announcement.getPublishAt())
                .createdAt(announcement.getCreatedAt())
                .updatedAt(announcement.getUpdatedAt())
                .build();
    }
}
