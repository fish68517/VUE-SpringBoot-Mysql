package com.powerinspection.controller;

import com.powerinspection.common.ApiResponse;
import com.powerinspection.common.BusinessException;
import com.powerinspection.config.AppProperties;
import com.powerinspection.dto.DeviceRequest;
import com.powerinspection.entity.Device;
import com.powerinspection.entity.DeviceCategory;
import com.powerinspection.entity.UserRole;
import com.powerinspection.logging.OperationLogRecord;
import com.powerinspection.repository.DeviceCategoryRepository;
import com.powerinspection.repository.DeviceRepository;
import com.powerinspection.security.AuthContext;
import com.powerinspection.service.ViewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceRepository deviceRepository;
    private final DeviceCategoryRepository deviceCategoryRepository;
    private final ViewService viewService;
    private final AppProperties appProperties;

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> list() {
        return ApiResponse.ok(deviceRepository.findAll().stream()
                .map(device -> viewService.deviceMap(device, appProperties.getBaseUrl() + "/images"))
                .toList());
    }

    @GetMapping("/options")
    public ApiResponse<List<Map<String, Object>>> options() {
        List<Map<String, Object>> list = deviceRepository.findAll().stream()
                .map(device -> Map.<String, Object>of(
                        "id", device.getId(),
                        "label", device.getDeviceName() + "(" + device.getDeviceCode() + ")"
                ))
                .toList();
        return ApiResponse.ok(list);
    }

    @PostMapping
    @OperationLogRecord(module = "设备管理", action = "新增设备")
    public ApiResponse<Map<String, Object>> create(@Valid @RequestBody DeviceRequest request) {
        AuthContext.requireRole(UserRole.ADMIN);
        Device device = new Device();
        fill(device, request);
        return ApiResponse.ok("新增成功", viewService.deviceMap(deviceRepository.save(device), appProperties.getBaseUrl() + "/images"));
    }

    @PutMapping("/{id}")
    @OperationLogRecord(module = "设备管理", action = "修改设备")
    public ApiResponse<Map<String, Object>> update(@PathVariable Long id, @Valid @RequestBody DeviceRequest request) {
        AuthContext.requireRole(UserRole.ADMIN);
        Device device = deviceRepository.findById(id).orElseThrow(() -> new BusinessException("设备不存在"));
        fill(device, request);
        return ApiResponse.ok("修改成功", viewService.deviceMap(deviceRepository.save(device), appProperties.getBaseUrl() + "/images"));
    }

    @DeleteMapping("/{id}")
    @OperationLogRecord(module = "设备管理", action = "删除设备")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        AuthContext.requireRole(UserRole.ADMIN);
        deviceRepository.deleteById(id);
        return ApiResponse.ok("删除成功", null);
    }

    private void fill(Device device, DeviceRequest request) {
        DeviceCategory category = deviceCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BusinessException("设备分类不存在"));
        device.setCategory(category);
        device.setDeviceCode(request.getDeviceCode());
        device.setDeviceName(request.getDeviceName());
        device.setLocation(request.getLocation());
        device.setStatus(request.getStatus());
        device.setImageName(request.getImageName());
        device.setRemark(request.getRemark());
    }
}
