package com.submission.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Message Entity - Represents a message in the system
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    private Long id;
    private Long senderId;
    private Long recipientId;
    private Long manuscriptId;
    private String content;
    private String type; // NOTIFICATION, COMMUNICATION
    private Boolean isRead;
    private LocalDateTime createdAt;
}
