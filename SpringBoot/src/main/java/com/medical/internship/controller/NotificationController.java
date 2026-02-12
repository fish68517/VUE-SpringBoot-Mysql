package com.medical.internship.controller;

import com.medical.internship.common.ApiResponse;
import com.medical.internship.common.SessionContext;
import com.medical.internship.dto.NotificationResponse;
import com.medical.internship.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通知控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * 获取通知列表
     * GET /api/notifications
     */
    @GetMapping
    public ApiResponse<List<NotificationResponse>> getNotificationList() {
        Long userId = SessionContext.getCurrentUserId();
        log.info("获取用户通知列表: {}", userId);
        List<NotificationResponse> notifications = notificationService.getNotificationList();
        return ApiResponse.success(notifications);
    }
    
    /**
     * 标记通知为已读
     * PUT /api/notifications/{id}/read
     */
    @PutMapping("/{id}/read")
    public ApiResponse<NotificationResponse> markAsRead(@PathVariable Long id) {
        Long userId = SessionContext.getCurrentUserId();
        log.info("标记通知为已读: {}, 用户: {}", id, userId);
        NotificationResponse notification = notificationService.markAsRead(id);
        return ApiResponse.success("标记成功", notification);
    }
}
