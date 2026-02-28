package com.health.util;

import com.health.entity.AuditLog;
import com.health.entity.User;
import com.health.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 审计日志工具类
 * 记录所有操作到审计日志表
 */
@Component
public class AuditLogUtil {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogUtil.class);

    @Autowired
    private AuditLogRepository auditLogRepository;

    /**
     * 记录用户操作
     *
     * @param user 操作用户
     * @param action 操作类型
     * @param resource 操作资源
     * @param details 操作详情
     */
    public void logAction(User user, String action, String resource, String details) {
        try {
            AuditLog auditLog = new AuditLog();
            if (user != null) {
                auditLog.setUserId(user.getId());
            }
            auditLog.setAction(action);
            auditLog.setResource(resource);
            auditLog.setDetails(details);
            auditLog.setTimestamp(LocalDateTime.now());
            
            auditLogRepository.save(auditLog);
            logger.info("审计日志记录: 用户={}, 操作={}, 资源={}", 
                    user != null ? user.getUsername() : "系统", action, resource);
        } catch (Exception e) {
            logger.error("记录审计日志失败", e);
        }
    }

    /**
     * 记录用户操作（不包含用户信息）
     *
     * @param action 操作类型
     * @param resource 操作资源
     * @param details 操作详情
     */
    public void logAction(String action, String resource, String details) {
        logAction(null, action, resource, details);
    }

    /**
     * 记录用户登录
     */
    public void logLogin(User user) {
        logAction(user, "登录", "用户认证", "用户登录成功");
    }

    /**
     * 记录用户登出
     */
    public void logLogout(User user) {
        logAction(user, "登出", "用户认证", "用户登出");
    }

    /**
     * 记录用户注册
     */
    public void logRegister(User user) {
        logAction(user, "注册", "用户管理", "新用户注册: " + user.getUsername());
    }

    /**
     * 记录用户信息更新
     */
    public void logUserUpdate(User user, String details) {
        logAction(user, "更新", "用户信息", "用户信息更新: " + details);
    }

    /**
     * 记录健康数据提交
     */
    public void logHealthDataSubmit(User user, String details) {
        logAction(user, "提交", "健康数据", "健康数据提交: " + details);
    }

    /**
     * 记录咨询提交
     */
    public void logConsultationSubmit(User user, String details) {
        logAction(user, "提交", "在线咨询", "咨询提交: " + details);
    }

    /**
     * 记录咨询回复
     */
    public void logConsultationAnswer(User user, String details) {
        logAction(user, "回复", "在线咨询", "咨询回复: " + details);
    }

    /**
     * 记录健康建议创建
     */
    public void logHealthAdviceCreate(User user, String details) {
        logAction(user, "创建", "健康建议", "健康建议创建: " + details);
    }

    /**
     * 记录权限修改
     */
    public void logPermissionChange(User operator, String targetUser, String details) {
        logAction(operator, "修改权限", "用户权限", "修改用户 " + targetUser + " 的权限: " + details);
    }

    /**
     * 记录用户禁用
     */
    public void logUserDisable(User operator, String targetUser) {
        logAction(operator, "禁用", "用户账户", "禁用用户: " + targetUser);
    }

    /**
     * 记录医师删除
     */
    public void logDoctorDelete(User operator, String doctorName) {
        logAction(operator, "删除", "医师账户", "删除医师: " + doctorName);
    }

    /**
     * 记录数据访问
     */
    public void logDataAccess(User user, String resource, String details) {
        logAction(user, "访问", resource, "数据访问: " + details);
    }

    /**
     * 记录异常操作
     */
    public void logException(User user, String action, String resource, String errorMessage) {
        logAction(user, action + "_异常", resource, "异常: " + errorMessage);
    }
}
