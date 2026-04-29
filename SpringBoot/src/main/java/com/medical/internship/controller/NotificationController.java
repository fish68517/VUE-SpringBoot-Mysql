package com.medical.internship.controller;

import com.medical.internship.common.AccessDeniedException;
import com.medical.internship.common.ApiResponse;
import com.medical.internship.common.ResourceNotFoundException;
import com.medical.internship.common.SessionContext;
import com.medical.internship.dto.NotificationCreateRequest;
import com.medical.internship.dto.NotificationResponse;
import com.medical.internship.entity.User;
import com.medical.internship.repository.UserRepository;
import com.medical.internship.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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

    //  新增依赖
    @Autowired
    private UserRepository userRepository;



    
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



    /**
     * 👇 新增接口：系统管理员手动发布通知
     * POST /api/notifications
     */
    @PostMapping
    public ApiResponse<String> createNotification(@Valid @RequestBody NotificationCreateRequest request) {
        // 1. 验证当前操作人的权限，只允许 ADMIN 发送
        String currentUserRole = SessionContext.getCurrentUserRole();
        if (!"ADMIN".equals(currentUserRole)) {
            throw new AccessDeniedException("权限不足，只有系统管理员可以发布通知");
        }

        // 2. 查找接收通知的目标用户
        User targetUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("发送失败：未找到ID为 " + request.getUserId() + " 的用户"));

        // 3. 调用原有的 Service 生成通知入库
        notificationService.createNotification(targetUser, request.getType(), request.getContent());
        log.info("管理员 {} 给用户 {} 发布了类型为 {} 的通知", SessionContext.getCurrentUserId(), targetUser.getId(), request.getType());

        return ApiResponse.success("通知发布成功", null);
    }
}
