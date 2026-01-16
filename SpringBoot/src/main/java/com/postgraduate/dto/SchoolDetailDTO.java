package com.postgraduate.dto;

import com.postgraduate.entity.School;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for detailed School information in API responses.
 * Contains school information along with associated majors, exam subjects, and requirements.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolDetailDTO {

    private Long id;
    private String name;
    private String city;
    private String tier;
    private String website;
    private String intro;
    private List<MajorDTO> majors;
    private List<ExamSubjectDTO> examSubjects;
    private List<SchoolRequirementDTO> requirements;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static SchoolDetailDTO fromEntity(School school, List<MajorDTO> majors,
                                             List<ExamSubjectDTO> examSubjects,
                                             List<SchoolRequirementDTO> requirements) {
        if (school == null) {
            return null;
        }
        return SchoolDetailDTO.builder()
                .id(school.getId())
                .name(school.getName())
                .city(school.getCity())
                .tier(school.getTier())
                .website(school.getWebsite())
                .intro(school.getIntro())
                .majors(majors)
                .examSubjects(examSubjects)
                .requirements(requirements)
                .createdAt(school.getCreatedAt())
                .updatedAt(school.getUpdatedAt())
                .build();
    }

}
