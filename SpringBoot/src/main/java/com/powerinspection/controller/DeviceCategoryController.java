package com.powerinspection.controller;

import com.powerinspection.common.ApiResponse;
import com.powerinspection.common.BusinessException;
import com.powerinspection.dto.DeviceCategoryRequest;
import com.powerinspection.entity.DeviceCategory;
import com.powerinspection.entity.UserRole;
import com.powerinspection.logging.OperationLogRecord;
import com.powerinspection.repository.DeviceCategoryRepository;
import com.powerinspection.security.AuthContext;
import com.powerinspection.service.ViewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/device-categories")
@RequiredArgsConstructor
public class DeviceCategoryController {

    private final DeviceCategoryRepository deviceCategoryRepository;
    private final ViewService viewService;

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> list() {
        return ApiResponse.ok(deviceCategoryRepository.findAll().stream().map(viewService::categoryMap).toList());
    }

    @PostMapping
    @OperationLogRecord(module = "设备分类", action = "新增分类")
    public ApiResponse<Map<String, Object>> create(@Valid @RequestBody DeviceCategoryRequest request) {
        AuthContext.requireRole(UserRole.ADMIN);
        DeviceCategory category = new DeviceCategory();
        fill(category, request);
        return ApiResponse.ok("新增成功", viewService.categoryMap(deviceCategoryRepository.save(category)));
    }

    @PutMapping("/{id}")
    @OperationLogRecord(module = "设备分类", action = "修改分类")
    public ApiResponse<Map<String, Object>> update(@PathVariable Long id, @Valid @RequestBody DeviceCategoryRequest request) {
        AuthContext.requireRole(UserRole.ADMIN);
        DeviceCategory category = deviceCategoryRepository.findById(id).orElseThrow(() -> new BusinessException("分类不存在"));
        fill(category, request);
        return ApiResponse.ok("修改成功", viewService.categoryMap(deviceCategoryRepository.save(category)));
    }

    @DeleteMapping("/{id}")
    @OperationLogRecord(module = "设备分类", action = "删除分类")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        AuthContext.requireRole(UserRole.ADMIN);
        deviceCategoryRepository.deleteById(id);
        return ApiResponse.ok("删除成功", null);
    }

    private void fill(DeviceCategory category, DeviceCategoryRequest request) {
        category.setCode(request.getCode());
        category.setName(request.getName());
        category.setDescription(request.getDescription());
    }
}
