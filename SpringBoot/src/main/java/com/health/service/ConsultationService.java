package com.health.service;

import com.health.entity.AuditLog;
import com.health.entity.Consultation;
import com.health.repository.AuditLogRepository;
import com.health.repository.ConsultationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 咨询服务类
 * 处理用户与医师之间的咨询相关业务逻辑
 */
@Service
public class ConsultationService {

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    /**
     * 提交咨询问题
     * 用户提交健康咨询问题
     *
     * @param userId 用户ID
     * @param question 咨询问题
     * @return 创建的咨询记录
     * @throws IllegalArgumentException 如果问题为空
     */
    public Consultation submitConsultation(Long userId, String question) {
        // 验证问题不为空
        if (question == null || question.trim().isEmpty()) {
            throw new IllegalArgumentException("咨询问题不能为空");
        }

        // 创建咨询记录
        Consultation consultation = new Consultation();
        consultation.setUserId(userId);
        consultation.setQuestion(question);
        consultation.setStatus("PENDING");

        // 保存咨询到数据库
        Consultation savedConsultation = consultationRepository.save(consultation);

        // 记录审计日志
        logAuditAction(userId, "提交咨询", "Consultation", "用户提交了咨询问题，问题ID: " + savedConsultation.getId());

        return savedConsultation;
    }

    /**
     * 医师回复咨询
     * 医师对用户的咨询问题进行回复
     *
     * @param consultationId 咨询ID
     * @param doctorId 医师ID
     * @param answer 回复内容
     * @return 更新后的咨询记录
     * @throws IllegalArgumentException 如果咨询不存在或回复为空
     */
    public Consultation replyConsultation(Long consultationId, Long doctorId, String answer) {
        // 验证回复不为空
        if (answer == null || answer.trim().isEmpty()) {
            throw new IllegalArgumentException("回复内容不能为空");
        }

        // 查询咨询记录
        Optional<Consultation> consultationOptional = consultationRepository.findById(consultationId);
        if (!consultationOptional.isPresent()) {
            throw new IllegalArgumentException("咨询记录不存在");
        }

        Consultation consultation = consultationOptional.get();

        // 更新咨询记录
        consultation.setDoctorId(doctorId);
        consultation.setAnswer(answer);
        consultation.setStatus("ANSWERED");
        consultation.setAnsweredAt(LocalDateTime.now());

        // 保存更新
        Consultation updatedConsultation = consultationRepository.save(consultation);

        // 记录审计日志
        logAuditAction(doctorId, "回复咨询", "Consultation", "医师回复了咨询问题，咨询ID: " + consultationId);

        return updatedConsultation;
    }

    /**
     * 获取咨询历史
     * 获取用户的所有咨询记录
     *
     * @param userId 用户ID
     * @return 咨询记录列表
     */
    public List<Consultation> getConsultationHistory(Long userId) {
        return consultationRepository.findByUserId(userId);
    }

    /**
     * 获取待回答的咨询
     * 获取医师的所有待回答咨询
     *
     * @param doctorId 医师ID
     * @return 待回答的咨询列表
     */
    public List<Consultation> getPendingConsultations(Long doctorId) {
        return consultationRepository.findByDoctorIdAndStatus(doctorId, "PENDING");
    }

    /**
     * 获取已回答的咨询
     * 获取医师的所有已回答咨询
     *
     * @param doctorId 医师ID
     * @return 已回答的咨询列表
     */
    public List<Consultation> getAnsweredConsultations(Long doctorId) {
        return consultationRepository.findByDoctorIdAndStatus(doctorId, "ANSWERED");
    }

    /**
     * 获取咨询详情
     * 获取单个咨询的详细信息
     *
     * @param consultationId 咨询ID
     * @return 咨询记录
     * @throws IllegalArgumentException 如果咨询不存在
     */
    public Consultation getConsultationDetail(Long consultationId) {
        Optional<Consultation> consultationOptional = consultationRepository.findById(consultationId);
        if (!consultationOptional.isPresent()) {
            throw new IllegalArgumentException("咨询记录不存在");
        }
        return consultationOptional.get();
    }

    /**
     * 记录审计日志
     * 记录咨询相关的操作到审计日志
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
