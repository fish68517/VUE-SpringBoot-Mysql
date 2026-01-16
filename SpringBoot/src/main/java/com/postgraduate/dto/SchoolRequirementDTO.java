package com.postgraduate.dto;

import com.postgraduate.entity.SchoolRequirement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for SchoolRequirement information in API responses.
 * Contains reexamination requirement details for schools and majors.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolRequirementDTO {

    private Long id;
    private Long schoolId;
    private Long majorId;
    private String content;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static SchoolRequirementDTO fromEntity(SchoolRequirement requirement) {
        if (requirement == null) {
            return null;
        }
        return SchoolRequirementDTO.builder()
                .id(requirement.getId())
                .schoolId(requirement.getSchoolId())
                .majorId(requirement.getMajorId())
                .content(requirement.getContent())
                .updatedBy(requirement.getUpdatedBy())
                .createdAt(requirement.getCreatedAt())
                .updatedAt(requirement.getUpdatedAt())
                .build();
    }

}
