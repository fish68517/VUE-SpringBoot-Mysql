package com.studyroom.controller;

import com.studyroom.dto.Result;
import com.studyroom.entity.SystemSetting;
import com.studyroom.service.SystemSettingService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/setting")
public class SettingController {

    private final SystemSettingService settingService;

    public SettingController(SystemSettingService settingService) {
        this.settingService = settingService;
    }

    @GetMapping("/public")
    public Result<Map<String, String>> getPublicSettings() {
        return settingService.getAllSettings();
    }

    @GetMapping("/list")
    public Result<List<SystemSetting>> getSettingsList(HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("role");
        if (role == null || role != 1) {
            return Result.error(403, "无权限");
        }
        return settingService.getSettingsList();
    }

    @PutMapping("/update")
    public Result<String> updateSetting(
            HttpServletRequest request,
            @RequestParam String key,
            @RequestParam String value) {
        Integer role = (Integer) request.getAttribute("role");
        if (role == null || role != 1) {
            return Result.error(403, "无权限");
        }
        return settingService.updateSetting(key, value);
    }

    @PutMapping("/batch")
    public Result<String> batchUpdateSettings(
            HttpServletRequest request,
            @RequestBody Map<String, String> settings) {
        Integer role = (Integer) request.getAttribute("role");
        if (role == null || role != 1) {
            return Result.error(403, "无权限");
        }
        return settingService.batchUpdateSettings(settings);
    }
}
