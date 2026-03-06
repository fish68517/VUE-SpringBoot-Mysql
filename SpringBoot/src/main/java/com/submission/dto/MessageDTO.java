package com.submission.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Message Data Transfer Object - For API requests and responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDTO {
    private Long id;
    private Long senderId;
    private String senderName;
    private Long recipientId;
    private Long manuscriptId;
    private String content;
    private String type; // NOTIFICATION, COMMUNICATION
    private Boolean isRead;
    private LocalDateTime createdAt;
}
