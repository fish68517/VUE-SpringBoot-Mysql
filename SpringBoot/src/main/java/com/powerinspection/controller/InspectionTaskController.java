package com.powerinspection.controller;

import com.powerinspection.common.ApiResponse;
import com.powerinspection.common.BusinessException;
import com.powerinspection.dto.RecordSubmitRequest;
import com.powerinspection.dto.TaskRequest;
import com.powerinspection.entity.*;
import com.powerinspection.logging.OperationLogRecord;
import com.powerinspection.repository.*;
import com.powerinspection.security.AuthContext;
import com.powerinspection.service.InspectionTaskItemService;
import com.powerinspection.service.ViewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class InspectionTaskController {

    private final InspectionTaskRepository taskRepository;
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final InspectionRecordRepository recordRepository;
    private final DefectRepository defectRepository;
    private final InspectionTaskItemRepository taskItemRepository;
    private final InspectionTaskItemService taskItemService;
    private final ViewService viewService;

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> list() {
        User currentUser = AuthContext.getUser();
        List<InspectionTask> tasks = currentUser.getRole() == UserRole.INSPECTOR
                ? taskRepository.findByInspectorId(currentUser.getId())
                : taskRepository.findAll();
        return ApiResponse.ok(tasks.stream().map(viewService::taskMap).toList());
    }

    @GetMapping("/{id}/items")
    public ApiResponse<List<Map<String, Object>>> items(@PathVariable Long id) {
        InspectionTask task = taskRepository.findById(id).orElseThrow(() -> new BusinessException("任务不存在"));
        User currentUser = AuthContext.getUser();
        if (currentUser.getRole() == UserRole.INSPECTOR && !task.getInspector().getId().equals(currentUser.getId())) {
            throw new BusinessException("只能查看自己的任务检查项");
        }
        List<Map<String, Object>> items = taskItemRepository.findByTaskIdOrderBySortOrderAscIdAsc(id).stream()
                .map(viewService::taskItemMap)
                .toList();
        return ApiResponse.ok(items);
    }

    @PostMapping
    @OperationLogRecord(module = "巡检任务", action = "新增任务")
    public ApiResponse<Map<String, Object>> create(@Valid @RequestBody TaskRequest request) {
        AuthContext.requireRole(UserRole.ADMIN);
        Device device = deviceRepository.findById(request.getDeviceId()).orElseThrow(() -> new BusinessException("设备不存在"));
        User inspector = userRepository.findById(request.getInspectorId()).orElseThrow(() -> new BusinessException("巡检员不存在"));
        if (inspector.getRole() != UserRole.INSPECTOR) {
            throw new BusinessException("所选用户不是巡检员");
        }
        InspectionTask task = new InspectionTask();
        task.setTaskNo("TASK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        task.setTitle(request.getTitle());
        task.setDevice(device);
        task.setInspector(inspector);
        task.setPlannedDate(request.getPlannedDate());
        task.setPriority(request.getPriority());
        task.setRemark(request.getRemark());
        task.setStatus(TaskStatus.PENDING);
        InspectionTask savedTask = taskRepository.save(task);
        taskItemService.rebuildTaskItems(savedTask);
        return ApiResponse.ok("新增成功", viewService.taskMap(savedTask));
    }

    @PutMapping("/{id}")
    @OperationLogRecord(module = "巡检任务", action = "修改任务")
    public ApiResponse<Map<String, Object>> update(@PathVariable Long id, @Valid @RequestBody TaskRequest request) {
        AuthContext.requireRole(UserRole.ADMIN);
        InspectionTask task = taskRepository.findById(id).orElseThrow(() -> new BusinessException("任务不存在"));
        Device device = deviceRepository.findById(request.getDeviceId()).orElseThrow(() -> new BusinessException("设备不存在"));
        User inspector = userRepository.findById(request.getInspectorId()).orElseThrow(() -> new BusinessException("巡检员不存在"));
        task.setTitle(request.getTitle());
        task.setDevice(device);
        task.setInspector(inspector);
        task.setPlannedDate(request.getPlannedDate());
        task.setPriority(request.getPriority());
        task.setRemark(request.getRemark());
        InspectionTask savedTask = taskRepository.save(task);
        if (!recordRepository.existsByTaskId(savedTask.getId())) {
            taskItemService.rebuildTaskItems(savedTask);
        }
        return ApiResponse.ok("修改成功", viewService.taskMap(savedTask));
    }

    @DeleteMapping("/{id}")
    @OperationLogRecord(module = "巡检任务", action = "删除任务")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        AuthContext.requireRole(UserRole.ADMIN);
        taskItemRepository.deleteByTaskId(id);
        taskRepository.deleteById(id);
        return ApiResponse.ok("删除成功", null);
    }

    @PostMapping("/{id}/submit")
    @OperationLogRecord(module = "巡检任务", action = "提交巡检")
    public ApiResponse<Map<String, Object>> submit(@PathVariable Long id, @Valid @RequestBody RecordSubmitRequest request) {
        AuthContext.requireRole(UserRole.INSPECTOR);
        InspectionTask task = taskRepository.findById(id).orElseThrow(() -> new BusinessException("任务不存在"));
        User currentUser = AuthContext.getUser();
        if (!task.getInspector().getId().equals(currentUser.getId())) {
            throw new BusinessException("只能提交自己的巡检任务");
        }
        if (recordRepository.existsByTaskId(id)) {
            throw new BusinessException("该任务已提交巡检记录");
        }

        List<InspectionTaskItem> taskItems = taskItemRepository.findByTaskIdOrderBySortOrderAscIdAsc(id);
        for (var itemRequest : request.getItems()) {
            taskItems.stream()
                    .filter(taskItem -> taskItem.getId().equals(itemRequest.getItemId()))
                    .findFirst()
                    .ifPresent(taskItem -> {
                        taskItem.setCheckedValue(itemRequest.getCheckedValue());
                        taskItem.setItemResult(itemRequest.getItemResult());
                        taskItem.setItemRemark(itemRequest.getItemRemark());
                    });
        }
        taskItemRepository.saveAll(taskItems);

        boolean hasAbnormalItem = taskItems.stream()
                .map(InspectionTaskItem::getItemResult)
                .filter(result -> result != null && !result.isBlank())
                .anyMatch(result -> Stream.of("异常", "需复检").anyMatch(result::equals));

        if (hasAbnormalItem && "正常".equals(request.getResult())) {
            request.setResult("异常");
        }
        if (hasAbnormalItem && !Boolean.TRUE.equals(request.getNeedRepair())) {
            request.setNeedRepair(Boolean.TRUE);
        }

        task.setStatus(TaskStatus.COMPLETED);
        task.getDevice().setLastInspectionTime(LocalDateTime.now());

        InspectionRecord record = new InspectionRecord();
        record.setTask(task);
        record.setDevice(task.getDevice());
        record.setInspector(currentUser);
        record.setResult(request.getResult());
        record.setStatusDescription(request.getStatusDescription());
        record.setImageName(request.getImageName());
        record.setNeedRepair(Boolean.TRUE.equals(request.getNeedRepair()));
        record.setInspectionTime(LocalDateTime.now());

        InspectionRecord savedRecord = recordRepository.save(record);
        taskRepository.save(task);

        if (Boolean.TRUE.equals(request.getNeedRepair())) {
            Defect defect = new Defect();
            defect.setRecord(savedRecord);
            defect.setDevice(task.getDevice());
            defect.setReporter(currentUser);
            defect.setLevel(request.getDefectLevel() == null || request.getDefectLevel().isBlank() ? "中" : request.getDefectLevel());
            defect.setDescription(request.getStatusDescription() == null || request.getStatusDescription().isBlank()
                    ? "巡检发现异常，待维护处理"
                    : request.getStatusDescription());
            defect.setImageName(request.getImageName());
            defect.setReportedAt(LocalDateTime.now());
            defect.setStatus(DefectStatus.REPORTED);
            defectRepository.save(defect);
        }

        return ApiResponse.ok("提交成功", viewService.taskMap(task));
    }
}
