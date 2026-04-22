package com.powerinspection.controller;

import com.powerinspection.common.ApiResponse;
import com.powerinspection.common.BusinessException;
import com.powerinspection.config.AppProperties;
import com.powerinspection.dto.RepairOrderProcessRequest;
import com.powerinspection.dto.RepairOrderSaveRequest;
import com.powerinspection.entity.*;
import com.powerinspection.logging.OperationLogRecord;
import com.powerinspection.repository.DefectRepository;
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
@RequestMapping("/api/repair-orders")
@RequiredArgsConstructor
public class RepairOrderController {

    private final RepairOrderRepository repairOrderRepository;
    private final DefectRepository defectRepository;
    private final UserRepository userRepository;
    private final ViewService viewService;
    private final AppProperties appProperties;

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> list() {
        User currentUser = AuthContext.getUser();
        List<RepairOrder> orders = currentUser.getRole() == UserRole.MAINTAINER
                ? repairOrderRepository.findByMaintainerId(currentUser.getId())
                : repairOrderRepository.findAll();
        return ApiResponse.ok(orders.stream()
                .map(order -> viewService.repairOrderMap(order, appProperties.getBaseUrl() + "/images"))
                .toList());
    }

    @PostMapping
    @OperationLogRecord(module = "维修工单", action = "新增工单")
    public ApiResponse<Map<String, Object>> create(@Valid @RequestBody RepairOrderSaveRequest request) {
        AuthContext.requireRole(UserRole.ADMIN);
        if (repairOrderRepository.existsByDefectId(request.getDefectId())) {
            throw new BusinessException("该缺陷已经存在维修工单");
        }
        RepairOrder order = new RepairOrder();
        order.setOrderNo("ORDER-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        fillRepairOrder(order, request);
        return ApiResponse.ok("新增成功", viewService.repairOrderMap(repairOrderRepository.save(order), appProperties.getBaseUrl() + "/images"));
    }

    @PutMapping("/{id}")
    @OperationLogRecord(module = "维修工单", action = "修改工单")
    public ApiResponse<Map<String, Object>> update(@PathVariable Long id, @Valid @RequestBody RepairOrderSaveRequest request) {
        AuthContext.requireRole(UserRole.ADMIN);
        RepairOrder order = repairOrderRepository.findById(id).orElseThrow(() -> new BusinessException("工单不存在"));
        repairOrderRepository.findByDefectId(request.getDefectId())
                .filter(item -> !item.getId().equals(id))
                .ifPresent(item -> {
                    throw new BusinessException("该缺陷已经存在其他维修工单");
                });
        fillRepairOrder(order, request);
        return ApiResponse.ok("修改成功", viewService.repairOrderMap(repairOrderRepository.save(order), appProperties.getBaseUrl() + "/images"));
    }

    @DeleteMapping("/{id}")
    @OperationLogRecord(module = "维修工单", action = "删除工单")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        AuthContext.requireRole(UserRole.ADMIN);
        RepairOrder order = repairOrderRepository.findById(id).orElseThrow(() -> new BusinessException("工单不存在"));
        Defect defect = order.getDefect();
        repairOrderRepository.delete(order);
        defect.setStatus(DefectStatus.REPORTED);
        defectRepository.save(defect);
        return ApiResponse.ok("删除成功", null);
    }

    @PutMapping("/{id}/process")
    @OperationLogRecord(module = "维修工单", action = "处理工单")
    public ApiResponse<Map<String, Object>> process(@PathVariable Long id, @Valid @RequestBody RepairOrderProcessRequest request) {
        AuthContext.requireRole(UserRole.MAINTAINER);
        RepairOrder order = repairOrderRepository.findById(id).orElseThrow(() -> new BusinessException("工单不存在"));
        User currentUser = AuthContext.getUser();
        if (!order.getMaintainer().getId().equals(currentUser.getId())) {
            throw new BusinessException("只能处理分配给自己的工单");
        }
        order.setStatus(OrderStatus.WAITING_REVIEW);
        order.setMeasures(request.getMeasures());
        order.setResult(request.getResult());
        order.setImageName(request.getImageName());
        order.setRemark(request.getRemark());
        order.setFinishedAt(LocalDateTime.now());

        Defect defect = order.getDefect();
        defect.setStatus(DefectStatus.PROCESSING);
        defectRepository.save(defect);

        return ApiResponse.ok("处理提交成功", viewService.repairOrderMap(repairOrderRepository.save(order), appProperties.getBaseUrl() + "/images"));
    }

    @PutMapping("/{id}/close")
    @OperationLogRecord(module = "维修工单", action = "闭环确认")
    public ApiResponse<Map<String, Object>> close(@PathVariable Long id) {
        AuthContext.requireRole(UserRole.ADMIN);
        RepairOrder order = repairOrderRepository.findById(id).orElseThrow(() -> new BusinessException("工单不存在"));
        order.setStatus(OrderStatus.CLOSED);
        if (order.getFinishedAt() == null) {
            order.setFinishedAt(LocalDateTime.now());
        }
        Defect defect = order.getDefect();
        defect.setStatus(DefectStatus.CLOSED);
        defectRepository.save(defect);
        return ApiResponse.ok("闭环成功", viewService.repairOrderMap(repairOrderRepository.save(order), appProperties.getBaseUrl() + "/images"));
    }

    private void fillRepairOrder(RepairOrder order, RepairOrderSaveRequest request) {
        Defect defect = defectRepository.findById(request.getDefectId()).orElseThrow(() -> new BusinessException("缺陷记录不存在"));
        User maintainer = userRepository.findById(request.getMaintainerId()).orElseThrow(() -> new BusinessException("维护员不存在"));
        if (maintainer.getRole() != UserRole.MAINTAINER) {
            throw new BusinessException("所选用户不是维护员");
        }

        order.setDefect(defect);
        order.setDevice(defect.getDevice());
        order.setMaintainer(maintainer);
        order.setStatus(request.getStatus() == null ? OrderStatus.PENDING : request.getStatus());
        order.setMeasures(request.getMeasures());
        order.setResult(request.getResult());
        order.setImageName(request.getImageName());
        order.setFinishedAt(request.getFinishedAt());
        order.setRemark(request.getRemark());

        syncDefectStatus(defect, order.getStatus());
        defectRepository.save(defect);
    }

    private void syncDefectStatus(Defect defect, OrderStatus status) {
        if (status == OrderStatus.CLOSED) {
            defect.setStatus(DefectStatus.CLOSED);
        } else if (status == OrderStatus.WAITING_REVIEW || status == OrderStatus.PROCESSING) {
            defect.setStatus(DefectStatus.PROCESSING);
        } else {
            defect.setStatus(DefectStatus.ASSIGNED);
        }
    }
}
