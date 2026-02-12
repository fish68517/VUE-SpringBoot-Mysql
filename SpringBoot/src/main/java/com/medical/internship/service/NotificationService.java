package com.medical.internship.service;

import com.medical.internship.dto.NotificationResponse;
import com.medical.internship.entity.Notification;
import com.medical.internship.entity.User;

import java.util.List;

/**
 * 通知服务接口
 */
public interface NotificationService {
    
    /**
     * 获取用户通知列表
     */
    List<NotificationResponse> getNotificationList();
    
    /**
     * 标记通知为已读
     */
    NotificationResponse markAsRead(Long notificationId);
    
    /**
     * 创建通知
     */
    Notification createNotification(User user, String type, String content);
    
    /**
     * 创建申请审批结果通知
     */
    void createApplicationResultNotification(User student, String applicationStatus, String opinion);
    
    /**
     * 创建周记打回通知
     */
    void createWeeklyReportRejectedNotification(User student, String comment);
    
    /**
     * 创建任务截止提醒通知
     */
    void createTaskDeadlineNotification(User student, String taskTitle);
}
