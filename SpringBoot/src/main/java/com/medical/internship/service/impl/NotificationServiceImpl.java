package com.medical.internship.service.impl;

import com.medical.internship.common.BusinessException;
import com.medical.internship.common.ResourceNotFoundException;
import com.medical.internship.common.SessionContext;
import com.medical.internship.dto.NotificationResponse;
import com.medical.internship.entity.Notification;
import com.medical.internship.entity.User;
import com.medical.internship.repository.NotificationRepository;
import com.medical.internship.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 通知服务实现类
 */
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    
    private final NotificationRepository notificationRepository;
    
    @Override
    public List<NotificationResponse> getNotificationList() {
        Long userId = SessionContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        return notifications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public NotificationResponse markAsRead(Long notificationId) {
        Long userId = SessionContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("通知不存在"));
        
        // 验证权限 - 只能标记自己的通知
        if (!notification.getUser().getId().equals(userId)) {
            throw new BusinessException("无权限操作该通知");
        }
        
        notification.setIsRead(true);
        Notification updatedNotification = notificationRepository.save(notification);
        return convertToResponse(updatedNotification);
    }
    
    @Override
    @Transactional
    public Notification createNotification(User user, String type, String content) {
        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .content(content)
                .isRead(false)
                .build();
        
        return notificationRepository.save(notification);
    }
    
    @Override
    @Transactional
    public void createApplicationResultNotification(User student, String applicationStatus, String opinion) {
        String content;
        if ("APPROVED".equals(applicationStatus)) {
            content = "您的岗位申请已通过审批。" + (opinion != null ? "审批意见：" + opinion : "");
        } else if ("REJECTED".equals(applicationStatus)) {
            content = "您的岗位申请已被驳回。" + (opinion != null ? "驳回原因：" + opinion : "");
        } else {
            content = "您的岗位申请状态已更新。";
        }
        
        createNotification(student, "APPLICATION_RESULT", content);
    }
    
    @Override
    @Transactional
    public void createWeeklyReportRejectedNotification(User student, String comment) {
        String content = "您的周记已被打回。" + (comment != null ? "评语：" + comment : "");
        createNotification(student, "WEEKLY_REJECTED", content);
    }
    
    @Override
    @Transactional
    public void createTaskDeadlineNotification(User student, String taskTitle) {
        String content = "任务 \"" + taskTitle + "\" 即将截止，请及时完成。";
        createNotification(student, "TASK_DEADLINE", content);
    }
    
    /**
     * 将Notification实体转换为NotificationResponse DTO
     */
    private NotificationResponse convertToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .content(notification.getContent())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
