package com.postgraduate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for feedback submission requests.
 * Contains the feedback type and content submitted by users.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackSubmitRequest {

    private String type;
    private String content;
}
