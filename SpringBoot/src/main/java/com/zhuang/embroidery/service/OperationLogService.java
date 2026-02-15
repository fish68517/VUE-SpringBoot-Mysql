package com.zhuang.embroidery.service;

import com.zhuang.embroidery.dto.OperationLogListResponse;
import com.zhuang.embroidery.dto.OperationLogResponse;
import com.zhuang.embroidery.entity.OperationLog;
import com.zhuang.embroidery.repository.OperationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

/**
 * 操作日志业务逻辑服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OperationLogService {

    private final OperationLogRepository operationLogRepository;

    /**
     * 获取用户行为日志
     *
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 操作日志列表响应
     */
    public OperationLogListResponse getUserOperationLogs(Long userId, Integer pageNum, Integer pageSize) {
        log.info("获取用户行为日志: userId={}, pageNum={}, pageSize={}", userId, pageNum, pageSize);

        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<OperationLog> page = operationLogRepository.findByUserId(userId, pageable);

        OperationLogListResponse response = OperationLogListResponse.builder()
                .logs(page.getContent().stream()
                        .map(OperationLogResponse::fromOperationLog)
                        .collect(Collectors.toList()))
                .total(page.getTotalElements())
                .pageNum(pageNum)
                .pageSize(pageSize)
                .build();

        log.info("成功获取用户行为日志: userId={}, total={}", userId, page.getTotalElements());
        return response;
    }

    /**
     * 记录操作日志
     *
     * @param userId 用户ID
     * @param action 操作类型
     * @param targetType 目标类型
     * @param targetId 目标ID
     */
    @Transactional
    public void recordOperationLog(Long userId, String action, String targetType, Long targetId) {
        log.info("记录操作日志: userId={}, action={}, targetType={}, targetId={}", userId, action, targetType, targetId);


        OperationLog logInfo = OperationLog.builder()
                .userId(userId)
                .action(action)
                .targetType(targetType)
                .targetId(targetId)
                .build();

        operationLogRepository.save(logInfo);

        log.info("操作日志记录成功");
    }

}
