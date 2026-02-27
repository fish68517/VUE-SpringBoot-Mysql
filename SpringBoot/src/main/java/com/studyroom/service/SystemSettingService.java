package com.studyroom.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.studyroom.dto.Result;
import com.studyroom.entity.SystemSetting;
import com.studyroom.mapper.SystemSettingMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SystemSettingService {

    private final SystemSettingMapper settingMapper;

    public SystemSettingService(SystemSettingMapper settingMapper) {
        this.settingMapper = settingMapper;
    }

    public String getValue(String key) {
        return settingMapper.getValueByKey(key);
    }

    public String getValue(String key, String defaultValue) {
        String value = getValue(key);
        return value != null ? value : defaultValue;
    }

    public int getIntValue(String key, int defaultValue) {
        String value = getValue(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public Result<Map<String, String>> getAllSettings() {
        List<SystemSetting> settings = settingMapper.selectList(null);
        Map<String, String> result = new HashMap<>();
        for (SystemSetting s : settings) {
            result.put(s.getSettingKey(), s.getSettingValue());
        }
        return Result.success(result);
    }

    public Result<List<SystemSetting>> getSettingsList() {
        List<SystemSetting> settings = settingMapper.selectList(
                new LambdaQueryWrapper<SystemSetting>().orderByAsc(SystemSetting::getId)
        );
        return Result.success(settings);
    }

    public Result<String> updateSetting(String key, String value) {
        SystemSetting setting = settingMapper.selectOne(
                new LambdaQueryWrapper<SystemSetting>().eq(SystemSetting::getSettingKey, key)
        );
        if (setting == null) {
            return Result.error("设置项不存在");
        }
        setting.setSettingValue(value);
        settingMapper.updateById(setting);
        return Result.success("更新成功");
    }

    public Result<String> batchUpdateSettings(Map<String, String> settings) {
        for (Map.Entry<String, String> entry : settings.entrySet()) {
            SystemSetting setting = settingMapper.selectOne(
                    new LambdaQueryWrapper<SystemSetting>().eq(SystemSetting::getSettingKey, entry.getKey())
            );
            if (setting != null) {
                setting.setSettingValue(entry.getValue());
                settingMapper.updateById(setting);
            }
        }
        return Result.success("批量更新成功");
    }
}
