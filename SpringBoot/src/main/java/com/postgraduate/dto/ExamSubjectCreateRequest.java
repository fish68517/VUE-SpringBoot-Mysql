package com.postgraduate.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new exam subject.
 * Contains required fields for exam subject creation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamSubjectCreateRequest {

    private Long majorId;

    @NotBlank(message = "Subject name cannot be blank")
    private String subjectName;

    private String subjectCode;

}
