package com.health.service;

import com.health.entity.AuditLog;
import com.health.entity.HealthAdvice;
import com.health.repository.AuditLogRepository;
import com.health.repository.HealthAdviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * 健康建议服务类
 * 处理医师为患者提供健康建议的相关业务逻辑
 */
@Service
public class HealthAdviceService {

    @Autowired
    private HealthAdviceRepository healthAdviceRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    /**
     * 创建健康建议
     * 医师为患者创建个性化的健康建议
     *
     * @param doctorId 医师ID
     * @param userId 患者ID
     * @param adviceContent 建议内容
     * @param recommendation 推荐信息
     * @return 创建的健康建议
     * @throws IllegalArgumentException 如果建议内容为空
     */
    public HealthAdvice createHealthAdvice(Long doctorId, Long userId, String adviceContent, String recommendation) {
        // 验证建议内容不为空
        if (adviceContent == null || adviceContent.trim().isEmpty()) {
            throw new IllegalArgumentException("建议内容不能为空");
        }

        // 创建健康建议
        HealthAdvice healthAdvice = new HealthAdvice();
        healthAdvice.setDoctorId(doctorId);
        healthAdvice.setUserId(userId);
        healthAdvice.setAdviceContent(adviceContent);
        healthAdvice.setRecommendation(recommendation);

        // 保存健康建议到数据库
        HealthAdvice savedAdvice = healthAdviceRepository.save(healthAdvice);

        // 记录审计日志
        logAuditAction(doctorId, "创建健康建议", "HealthAdvice", "医师为患者创建了健康建议，建议ID: " + savedAdvice.getId() + "，患者ID: " + userId);

        return savedAdvice;
    }

    /**
     * 获取患者的健康建议
     * 获取患者收到的所有健康建议
     *
     * @param userId 患者ID
     * @return 健康建议列表
     */
    public List<HealthAdvice> getPatientHealthAdvice(Long userId) {
        return healthAdviceRepository.findByUserId(userId);
    }

    /**
     * 获取医师创建的健康建议
     * 获取医师为所有患者创建的健康建议
     *
     * @param doctorId 医师ID
     * @return 健康建议列表
     */
    public List<HealthAdvice> getDoctorHealthAdvice(Long doctorId) {
        return healthAdviceRepository.findByDoctorId(doctorId);
    }

    /**
     * 获取医师为特定患者的健康建议
     * 获取医师为某个患者创建的所有健康建议
     *
     * @param doctorId 医师ID
     * @param userId 患者ID
     * @return 健康建议列表
     */
    public List<HealthAdvice> getHealthAdviceByDoctorAndPatient(Long doctorId, Long userId) {
        return healthAdviceRepository.findByDoctorIdAndUserId(doctorId, userId);
    }

    /**
     * 发送健康建议通知
     * 向患者发送健康建议（实际发送通知的逻辑）
     *
     * @param adviceId 健康建议ID
     * @return 发送成功的健康建议
     * @throws IllegalArgumentException 如果建议不存在
     */
    public HealthAdvice sendHealthAdviceNotification(Long adviceId) {
        // 查询健康建议
        Optional<HealthAdvice> adviceOptional = healthAdviceRepository.findById(adviceId);
        if (!adviceOptional.isPresent()) {
            throw new IllegalArgumentException("健康建议不存在");
        }

        HealthAdvice healthAdvice = adviceOptional.get();

        // 记录审计日志 - 发送通知
        logAuditAction(healthAdvice.getDoctorId(), "发送健康建议通知", "HealthAdvice", 
                      "医师发送了健康建议通知，建议ID: " + adviceId + "，患者ID: " + healthAdvice.getUserId());

        return healthAdvice;
    }

    /**
     * 更新健康建议
     * 医师可以更新已创建的健康建议
     *
     * @param adviceId 健康建议ID
     * @param adviceContent 新的建议内容
     * @param recommendation 新的推荐信息
     * @return 更新后的健康建议
     * @throws IllegalArgumentException 如果建议不存在或内容为空
     */
    public HealthAdvice updateHealthAdvice(Long adviceId, String adviceContent, String recommendation) {
        // 验证建议内容不为空
        if (adviceContent == null || adviceContent.trim().isEmpty()) {
            throw new IllegalArgumentException("建议内容不能为空");
        }

        // 查询健康建议
        Optional<HealthAdvice> adviceOptional = healthAdviceRepository.findById(adviceId);
        if (!adviceOptional.isPresent()) {
            throw new IllegalArgumentException("健康建议不存在");
        }

        HealthAdvice healthAdvice = adviceOptional.get();

        // 更新建议内容
        healthAdvice.setAdviceContent(adviceContent);
        healthAdvice.setRecommendation(recommendation);

        // 保存更新
        HealthAdvice updatedAdvice = healthAdviceRepository.save(healthAdvice);

        // 记录审计日志
        logAuditAction(healthAdvice.getDoctorId(), "更新健康建议", "HealthAdvice", 
                      "医师更新了健康建议，建议ID: " + adviceId);

        return updatedAdvice;
    }

    /**
     * 获取健康建议详情
     * 获取单个健康建议的详细信息
     *
     * @param adviceId 健康建议ID
     * @return 健康建议
     * @throws IllegalArgumentException 如果建议不存在
     */
    public HealthAdvice getHealthAdviceDetail(Long adviceId) {
        Optional<HealthAdvice> adviceOptional = healthAdviceRepository.findById(adviceId);
        if (!adviceOptional.isPresent()) {
            throw new IllegalArgumentException("健康建议不存在");
        }
        return adviceOptional.get();
    }

    /**
     * 记录审计日志
     * 记录健康建议相关的操作到审计日志
     *
     * @param userId 用户ID
     * @param action 操作类型
     * @param resource 资源类型
     * @param details 操作详情
     */
    private void logAuditAction(Long userId, String action, String resource, String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(userId);
        auditLog.setAction(action);
        auditLog.setResource(resource);
        auditLog.setDetails(details);
        auditLogRepository.save(auditLog);
    }
}
