package com.graduation.service;

import com.graduation.entity.SystemLogs;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SystemLogsService extends IService<SystemLogs> {
    // 记录日志的便捷方法
    void log(Integer operatorId, String operatorName, String action, String targetId, String details);
}