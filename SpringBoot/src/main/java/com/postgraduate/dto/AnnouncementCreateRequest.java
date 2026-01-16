package com.postgraduate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new announcement.
 * Contains all required fields for announcement creation with validation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnouncementCreateRequest {

    @NotBlank(message = "Announcement title is required")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;

    @NotBlank(message = "Announcement content is required")
    @Size(min = 1, max = 65535, message = "Content must be between 1 and 65535 characters")
    private String content;

    @NotBlank(message = "Announcement status is required")
    private String status;

    @NotNull(message = "Sort order is required")
    private Integer sortOrder;
}
