package com.graduation.service.impl;

import com.graduation.entity.SystemLogs;
import com.graduation.mapper.SystemLogsMapper;
import com.graduation.service.SystemLogsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class SystemLogsServiceImpl extends ServiceImpl<SystemLogsMapper, SystemLogs> implements SystemLogsService {

    @Override
    public void log(Integer operatorId, String operatorName, String action, String targetId, String details) {
        SystemLogs log = new SystemLogs();
        log.setOperatorId(operatorId);
        log.setOperatorName(operatorName);
        log.setAction(action);
        log.setTargetId(targetId);
        log.setDetails(details);
        log.setCreatedAt(LocalDateTime.now());
        // 在实际生产中，IP地址可以通过 RequestContextHolder 获取
        log.setIpAddress("127.0.0.1");

        this.save(log);
    }
}