package com.submission.controller;

import com.submission.dto.MessageDTO;
import com.submission.service.MessageService;
import com.submission.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Notification Controller - API endpoints for notification operations
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final MessageService messageService;
    private final NotificationService notificationService;

    /**
     * Get all notifications for current user
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getNotifications(@SessionAttribute("userId") Long userId) {
        try {
            List<MessageDTO> messages = messageService.getMessagesByRecipient(userId);
            int unreadCount = messageService.getUnreadMessageCount(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "Notifications retrieved successfully");
            response.put("data", messages);
            response.put("unreadCount", unreadCount);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "Error retrieving notifications: " + e.getMessage());
            response.put("data", null);

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Get unread notification count
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadCount(@SessionAttribute("userId") Long userId) {
        try {
            int unreadCount = messageService.getUnreadMessageCount(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "Unread count retrieved successfully");
            response.put("data", unreadCount);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "Error retrieving unread count: " + e.getMessage());
            response.put("data", null);

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Mark notification as read
     */
    @PostMapping("/{notificationId}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable Long notificationId) {
        try {
            messageService.markMessageAsRead(notificationId);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "Notification marked as read");
            response.put("data", null);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "Error marking notification as read: " + e.getMessage());
            response.put("data", null);

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Get notification by id
     */
    @GetMapping("/{notificationId}")
    public ResponseEntity<Map<String, Object>> getNotification(@PathVariable Long notificationId) {
        try {
            MessageDTO message = messageService.getMessageById(notificationId);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "Notification retrieved successfully");
            response.put("data", message);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "Error retrieving notification: " + e.getMessage());
            response.put("data", null);

            return ResponseEntity.status(500).body(response);
        }
    }
}
