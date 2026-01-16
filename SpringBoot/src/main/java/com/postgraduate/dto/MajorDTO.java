package com.postgraduate.dto;

import com.postgraduate.entity.Major;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Major information in API responses.
 * Contains major details including name and direction.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MajorDTO {

    private Long id;
    private Long schoolId;
    private String name;
    private String direction;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MajorDTO fromEntity(Major major) {
        if (major == null) {
            return null;
        }
        return MajorDTO.builder()
                .id(major.getId())
                .schoolId(major.getSchoolId())
                .name(major.getName())
                .direction(major.getDirection())
                .createdAt(major.getCreatedAt())
                .updatedAt(major.getUpdatedAt())
                .build();
    }

}
