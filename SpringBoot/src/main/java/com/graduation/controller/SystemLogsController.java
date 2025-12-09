package com.graduation.controller;

import com.graduation.entity.SystemLogs;
import com.graduation.service.SystemLogsService;
import com.graduation.common.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/system-logs")
public class SystemLogsController extends BaseController<SystemLogsService, SystemLogs> {

    // 重写 list 方法，按时间倒序排列
    @Override
    @GetMapping("/list")
    public List<SystemLogs> list() {
        QueryWrapper<SystemLogs> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("created_at");
        return service.list(queryWrapper);
    }
}