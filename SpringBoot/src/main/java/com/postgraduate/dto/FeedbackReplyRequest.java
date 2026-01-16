package com.postgraduate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for admin feedback reply requests.
 * Contains the status update and admin reply for feedback management.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackReplyRequest {

    private String status;
    private String adminReply;
}
