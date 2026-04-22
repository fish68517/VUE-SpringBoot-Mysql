package com.powerinspection.controller;

import com.powerinspection.common.ApiResponse;
import com.powerinspection.common.BusinessException;
import com.powerinspection.config.AppProperties;
import com.powerinspection.dto.DefectRequest;
import com.powerinspection.dto.RepairOrderCreateRequest;
import com.powerinspection.entity.*;
import com.powerinspection.logging.OperationLogRecord;
import com.powerinspection.repository.DefectRepository;
import com.powerinspection.repository.InspectionRecordRepository;
import com.powerinspection.repository.RepairOrderRepository;
import com.powerinspection.repository.UserRepository;
import com.powerinspection.security.AuthContext;
import com.powerinspection.service.ViewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/defects")
@RequiredArgsConstructor
public class DefectController {

    private final DefectRepository defectRepository;
    private final RepairOrderRepository repairOrderRepository;
    private final InspectionRecordRepository inspectionRecordRepository;
    private final UserRepository userRepository;
    private final ViewService viewService;
    private final AppProperties appProperties;

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> list() {
        return ApiResponse.ok(defectRepository.findAll().stream()
                .map(defect -> viewService.defectMap(defect, appProperties.getBaseUrl() + "/images"))
                .toList());
    }

    @PostMapping
    @OperationLogRecord(module = "异常缺陷", action = "新增缺陷")
    public ApiResponse<Map<String, Object>> create(@Valid @RequestBody DefectRequest request) {
        AuthContext.requireRole(UserRole.ADMIN);
        Defect defect = new Defect();
        fillDefect(defect, request);
        return ApiResponse.ok("新增成功", viewService.defectMap(defectRepository.save(defect), appProperties.getBaseUrl() + "/images"));
    }

    @PutMapping("/{id}")
    @OperationLogRecord(module = "异常缺陷", action = "修改缺陷")
    public ApiResponse<Map<String, Object>> update(@PathVariable Long id, @Valid @RequestBody DefectRequest request) {
        AuthContext.requireRole(UserRole.ADMIN);
        Defect defect = defectRepository.findById(id).orElseThrow(() -> new BusinessException("缺陷记录不存在"));
        fillDefect(defect, request);
        return ApiResponse.ok("修改成功", viewService.defectMap(defectRepository.save(defect), appProperties.getBaseUrl() + "/images"));
    }

    @DeleteMapping("/{id}")
    @OperationLogRecord(module = "异常缺陷", action = "删除缺陷")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        AuthContext.requireRole(UserRole.ADMIN);
        if (repairOrderRepository.existsByDefectId(id)) {
            throw new BusinessException("该缺陷已关联维修工单，请先删除维修工单");
        }
        defectRepository.deleteById(id);
        return ApiResponse.ok("删除成功", null);
    }

    @PostMapping("/{id}/repair-orders")
    @OperationLogRecord(module = "异常缺陷", action = "派发维修工单")
    public ApiResponse<Map<String, Object>> createRepairOrder(@PathVariable Long id, @Valid @RequestBody RepairOrderCreateRequest request) {
        AuthContext.requireRole(UserRole.ADMIN);
        if (repairOrderRepository.existsByDefectId(id)) {
            throw new BusinessException("该缺陷已经创建过维修工单");
        }
        Defect defect = defectRepository.findById(id).orElseThrow(() -> new BusinessException("缺陷记录不存在"));
        User maintainer = userRepository.findById(request.getMaintainerId()).orElseThrow(() -> new BusinessException("维护员不存在"));
        if (maintainer.getRole() != UserRole.MAINTAINER) {
            throw new BusinessException("所选用户不是维护员");
        }

        RepairOrder order = new RepairOrder();
        order.setOrderNo("ORDER-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setDefect(defect);
        order.setDevice(defect.getDevice());
        order.setMaintainer(maintainer);
        order.setStatus(OrderStatus.PENDING);
        order.setRemark(request.getRemark());

        defect.setStatus(DefectStatus.ASSIGNED);
        defectRepository.save(defect);
        return ApiResponse.ok("派单成功", viewService.repairOrderMap(repairOrderRepository.save(order), appProperties.getBaseUrl() + "/images"));
    }

    private void fillDefect(Defect defect, DefectRequest request) {
        InspectionRecord record = inspectionRecordRepository.findById(request.getRecordId())
                .orElseThrow(() -> new BusinessException("巡检记录不存在"));
        defectRepository.findByRecordId(request.getRecordId())
                .filter(item -> !item.getId().equals(defect.getId()))
                .ifPresent(item -> {
                    throw new BusinessException("该巡检记录已经生成过缺陷");
                });

        defect.setRecord(record);
        defect.setDevice(record.getDevice());
        defect.setReporter(record.getInspector());
        defect.setLevel(request.getLevel());
        defect.setStatus(request.getStatus());
        defect.setDescription(request.getDescription());
        defect.setImageName(request.getImageName());
        defect.setReportedAt(defect.getReportedAt() == null ? LocalDateTime.now() : defect.getReportedAt());
    }
}
