package com.naxi.service;

import com.naxi.entity.OperationLog;
import com.naxi.repository.OperationLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OperationLogService {
    @Autowired
    private OperationLogRepository operationLogRepository;

    /**
     * 记录用户操作
     */
    public OperationLog recordOperation(Long userId, String operationType, String targetType, Long targetId, String details) {
        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setOperationType(operationType);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setDetails(details);
        log.setCreatedAt(LocalDateTime.now());
        return operationLogRepository.save(log);
    }

    /**
     * 获取用户操作历史（支持分页）
     */
    public Page<OperationLog> getUserOperationHistory(Long userId, Pageable pageable) {
        return operationLogRepository.findByUserId(userId, pageable);
    }

    /**
     * 按操作类型筛选用户操作历史（支持分页）
     */
    public Page<OperationLog> getUserOperationHistoryByType(Long userId, String operationType, Pageable pageable) {
        return operationLogRepository.findByUserIdAndOperationType(userId, operationType, pageable);
    }
}
