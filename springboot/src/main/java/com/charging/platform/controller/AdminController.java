package com.charging.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.charging.platform.common.PageResult;
import com.charging.platform.common.Result;
import com.charging.platform.entity.ChargingUsageRecord;
import com.charging.platform.entity.UserFeedback;
import com.charging.platform.mapper.ChargingUsageRecordMapper;
import com.charging.platform.mapper.UserFeedbackMapper;
import com.charging.platform.service.AdminCrudService;
import com.charging.platform.service.HomeService;
import com.charging.platform.service.SimulationService;
import com.charging.platform.service.StatisticsGenerationService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminCrudService crudService;
    private final HomeService homeService;
    private final UserFeedbackMapper feedbackMapper;
    private final ChargingUsageRecordMapper usageRecordMapper;
    private final SimulationService simulationService;
    private final StatisticsGenerationService statisticsGenerationService;

    public AdminController(AdminCrudService crudService,
                           HomeService homeService,
                           UserFeedbackMapper feedbackMapper,
                           ChargingUsageRecordMapper usageRecordMapper,
                           SimulationService simulationService,
                           StatisticsGenerationService statisticsGenerationService) {
        this.crudService = crudService;
        this.homeService = homeService;
        this.feedbackMapper = feedbackMapper;
        this.usageRecordMapper = usageRecordMapper;
        this.simulationService = simulationService;
        this.statisticsGenerationService = statisticsGenerationService;
    }

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("overview", homeService.getOverview());
        data.put("pendingFeedbackCount", feedbackMapper.selectCount(
                new QueryWrapper<UserFeedback>().eq("process_status", 0)));
        data.put("todayUsageCount", usageRecordMapper.selectCount(
                new QueryWrapper<ChargingUsageRecord>().ge("start_time", LocalDate.now().atStartOfDay())
                        .lt("start_time", LocalDate.now().plusDays(1).atStartOfDay())));
        data.put("serverTime", LocalDateTime.now());
        return Result.success(data);
    }

    @GetMapping("/modules")
    public Result<String[]> modules() {
        return Result.success(crudService.supportedModules());
    }

    @GetMapping("/{module}")
    public Result<PageResult<?>> page(@PathVariable String module,
                                      @RequestParam(defaultValue = "1") long pageNum,
                                      @RequestParam(defaultValue = "10") long pageSize,
                                      @RequestParam(required = false) String keyword) {
        return Result.success(crudService.page(module, pageNum, pageSize, keyword));
    }

    @GetMapping("/{module}/{id}")
    public Result<Object> get(@PathVariable String module, @PathVariable Long id) {
        return Result.success(crudService.get(module, id));
    }

    @PostMapping("/{module}")
    public Result<Object> create(@PathVariable String module, @RequestBody JsonNode body) {
        return Result.success(crudService.create(module, body));
    }

    @PutMapping("/{module}/{id}")
    public Result<Object> update(@PathVariable String module, @PathVariable Long id,
                                 @RequestBody JsonNode body) {
        return Result.success(crudService.update(module, id, body));
    }

    @DeleteMapping("/{module}/{id}")
    public Result<Void> delete(@PathVariable String module, @PathVariable Long id) {
        crudService.delete(module, id);
        return Result.success();
    }

    @PostMapping("/simulation/run")
    public Result<Map<String, Object>> runSimulation() {
        return Result.success(simulationService.runOnce());
    }

    @PostMapping("/statistics/regenerate")
    public Result<Map<String, Object>> regenerateStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate statDate) {
        LocalDate targetDate = statDate == null ? LocalDate.now() : statDate;
        int rows = statisticsGenerationService.regenerate(targetDate);
        return Result.success(Map.of("statDate", targetDate, "affectedRows", rows));
    }
}
