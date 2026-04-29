package com.medical.internship.repository;

import com.medical.internship.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 通知数据访问接口
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    /**
     * 根据用户ID查询通知列表
     */
    List<Notification> findByUserId(Long userId);
    
    /**
     * 根据用户ID和是否已读查询通知列表
     */
    List<Notification> findByUserIdAndIsRead(Long userId, Boolean isRead);
    
    /**
     * 根据通知类型查询通知列表
     */
    List<Notification> findByType(String type);
}
