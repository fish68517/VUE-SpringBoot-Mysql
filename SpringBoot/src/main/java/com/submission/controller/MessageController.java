package com.submission.controller;

import com.submission.dto.MessageDTO;
import com.submission.service.MessageService;
import com.submission.vo.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

/**
 * Message Controller - Handles message operations
 */
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    /**
     * Get all messages for the current user
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<MessageDTO>>> getMessages(HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            List<MessageDTO> messages = messageService.getMessagesByRecipient(userId);
            return ResponseEntity.ok(ApiResponse.success("Messages retrieved", messages));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Mark a message as read
     */
    @PutMapping("/{messageId}/read")
    public ResponseEntity<ApiResponse<Void>> markMessageAsRead(
            @PathVariable Long messageId,
            HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            messageService.markMessageAsRead(messageId);
            return ResponseEntity.ok(ApiResponse.success("Message marked as read", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get unread message count
     */
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Integer>> getUnreadCount(HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            int unreadCount = messageService.getUnreadMessageCount(userId);
            return ResponseEntity.ok(ApiResponse.success("Unread count retrieved", unreadCount));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Send a message (generic endpoint)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<MessageDTO>> sendMessage(
            @RequestBody SendMessageRequest request,
            HttpSession session) {
        try {
            Long senderId = (Long) session.getAttribute("userId");
            System.out.println("Sender ID: " + senderId);
            System.out.println("Recipient : " + request);
            if (senderId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            MessageDTO message = messageService.sendMessage(
                    senderId,
                    request.getRecipientId(),
                    request.getContent(),
                    request.getType() != null ? request.getType() : "COMMUNICATION",
                    request.getManuscriptId()
            );
            return ResponseEntity.ok(ApiResponse.success("Message sent successfully", message));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get messages with a specific author (for editor)
     */
    @GetMapping("/with-author/{authorId}")
    public ResponseEntity<ApiResponse<List<MessageDTO>>> getMessagesWithAuthor(
            @PathVariable Long authorId,
            HttpSession session) {
        try {
            Long editorId = (Long) session.getAttribute("userId");
            if (editorId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            List<MessageDTO> messages = messageService.getMessagesWithAuthor(editorId, authorId);
            return ResponseEntity.ok(ApiResponse.success("Messages retrieved", messages));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get messages with a specific reviewer (for editor)
     */
    @GetMapping("/with-reviewer/{reviewerId}")
    public ResponseEntity<ApiResponse<List<MessageDTO>>> getMessagesWithReviewer(
            @PathVariable Long reviewerId,
            HttpSession session) {
        try {
            Long editorId = (Long) session.getAttribute("userId");
            if (editorId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            List<MessageDTO> messages = messageService.getMessagesWithReviewer(editorId, reviewerId);
            return ResponseEntity.ok(ApiResponse.success("Messages retrieved", messages));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Send message request DTO
     */
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class SendMessageRequest {
        private Long recipientId;
        private String content;
        private String type; // NOTIFICATION, COMMUNICATION
        private Long manuscriptId;

        @Override
        public String toString() {
            return "SendMessageRequest{" +
                    "recipientId=" + recipientId +
                    ", content='" + content + '\'' +
                    ", type='" + type + '\'' +
                    ", manuscriptId=" + manuscriptId +
                    '}';
        }
    }
}
