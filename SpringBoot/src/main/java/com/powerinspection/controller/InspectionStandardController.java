package com.powerinspection.controller;

import com.powerinspection.common.ApiResponse;
import com.powerinspection.common.BusinessException;
import com.powerinspection.dto.InspectionStandardRequest;
import com.powerinspection.entity.DeviceCategory;
import com.powerinspection.entity.InspectionStandard;
import com.powerinspection.entity.UserRole;
import com.powerinspection.logging.OperationLogRecord;
import com.powerinspection.repository.DeviceCategoryRepository;
import com.powerinspection.repository.InspectionStandardRepository;
import com.powerinspection.security.AuthContext;
import com.powerinspection.service.ViewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/standards")
@RequiredArgsConstructor
public class InspectionStandardController {

    private final InspectionStandardRepository inspectionStandardRepository;
    private final DeviceCategoryRepository deviceCategoryRepository;
    private final ViewService viewService;

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> list() {
        return ApiResponse.ok(inspectionStandardRepository.findAll().stream().map(viewService::standardMap).toList());
    }

    @PostMapping
    @OperationLogRecord(module = "巡检标准", action = "新增标准")
    public ApiResponse<Map<String, Object>> create(@Valid @RequestBody InspectionStandardRequest request) {
        AuthContext.requireRole(UserRole.ADMIN);
        InspectionStandard standard = new InspectionStandard();
        fill(standard, request);
        return ApiResponse.ok("新增成功", viewService.standardMap(inspectionStandardRepository.save(standard)));
    }

    @PutMapping("/{id}")
    @OperationLogRecord(module = "巡检标准", action = "修改标准")
    public ApiResponse<Map<String, Object>> update(@PathVariable Long id, @Valid @RequestBody InspectionStandardRequest request) {
        AuthContext.requireRole(UserRole.ADMIN);
        InspectionStandard standard = inspectionStandardRepository.findById(id).orElseThrow(() -> new BusinessException("巡检标准不存在"));
        fill(standard, request);
        return ApiResponse.ok("修改成功", viewService.standardMap(inspectionStandardRepository.save(standard)));
    }

    @DeleteMapping("/{id}")
    @OperationLogRecord(module = "巡检标准", action = "删除标准")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        AuthContext.requireRole(UserRole.ADMIN);
        inspectionStandardRepository.deleteById(id);
        return ApiResponse.ok("删除成功", null);
    }

    private void fill(InspectionStandard standard, InspectionStandardRequest request) {
        DeviceCategory category = deviceCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BusinessException("设备分类不存在"));
        standard.setCategory(category);
        standard.setItemName(request.getItemName());
        standard.setStandardValue(request.getStandardValue());
        standard.setCheckMethod(request.getCheckMethod());
        standard.setCycleDays(request.getCycleDays());
        standard.setAbnormalRule(request.getAbnormalRule());
        standard.setRemark(request.getRemark());
    }
}
