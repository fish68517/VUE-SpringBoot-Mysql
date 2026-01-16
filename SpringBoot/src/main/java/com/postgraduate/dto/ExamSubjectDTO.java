package com.postgraduate.dto;

import com.postgraduate.entity.ExamSubject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for ExamSubject information in API responses.
 * Contains exam subject details including subject name and code.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamSubjectDTO {

    private Long id;
    private Long schoolId;
    private Long majorId;
    private String subjectName;
    private String subjectCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ExamSubjectDTO fromEntity(ExamSubject examSubject) {
        if (examSubject == null) {
            return null;
        }
        return ExamSubjectDTO.builder()
                .id(examSubject.getId())
                .schoolId(examSubject.getSchoolId())
                .majorId(examSubject.getMajorId())
                .subjectName(examSubject.getSubjectName())
                .subjectCode(examSubject.getSubjectCode())
                .createdAt(examSubject.getCreatedAt())
                .updatedAt(examSubject.getUpdatedAt())
                .build();
    }

}
