package com.shenyang.musicfestival.controller;

import com.shenyang.musicfestival.entity.CheckinRecord;
import com.shenyang.musicfestival.entity.CheckinTask;
import com.shenyang.musicfestival.entity.User;
import com.shenyang.musicfestival.repository.CheckinRecordRepository;
import com.shenyang.musicfestival.repository.CheckinTaskRepository;
import com.shenyang.musicfestival.repository.UserRepository;
import com.shenyang.musicfestival.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/tasks")
@RequiredArgsConstructor
public class AdminTaskController {

    private final CheckinTaskRepository checkinTaskRepository;
    private final CheckinRecordRepository checkinRecordRepository; // 新增：记录仓库
    private final UserRepository userRepository; // 新增：用户仓库

    // ==========================================
    // 1. 获取打卡任务列表 (Tasks)
    // ==========================================
    @GetMapping
    public ResponseEntity<ApiResponse<Page<CheckinTask>>> getTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {

        List<CheckinTask> allTasks = checkinTaskRepository.findAll();

        List<CheckinTask> filteredTasks = allTasks.stream()
                .filter(t -> keyword == null || keyword.trim().isEmpty() ||
                        (t.getName() != null && t.getName().contains(keyword)))
                .sorted((t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()))
                .collect(Collectors.toList());

        int start = Math.min(page * size, filteredTasks.size());
        int end = Math.min(start + size, filteredTasks.size());
        List<CheckinTask> pagedList = filteredTasks.subList(start, end);

        Pageable pageable = PageRequest.of(page, size);
        Page<CheckinTask> resultPage = new PageImpl<>(pagedList, pageable, filteredTasks.size());

        return ResponseEntity.ok(ApiResponse.success(resultPage, "获取打卡任务列表成功"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CheckinTask>> createTask(@RequestBody CheckinTask task) {
        if (task.getStatus() == null) task.setStatus("active");
        return ResponseEntity.ok(ApiResponse.success(checkinTaskRepository.save(task), "创建任务成功"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CheckinTask>> updateTask(@PathVariable Long id, @RequestBody CheckinTask taskDetails) {
        CheckinTask task = checkinTaskRepository.findById(id).orElseThrow();
        task.setName(taskDetails.getName());
        task.setDescription(taskDetails.getDescription());
        task.setPoints(taskDetails.getPoints());
        task.setCoverImage(taskDetails.getCoverImage());
        task.setLatitude(taskDetails.getLatitude());
        task.setLongitude(taskDetails.getLongitude());
        task.setRadius(taskDetails.getRadius());
        task.setStartTime(taskDetails.getStartTime());
        task.setEndTime(taskDetails.getEndTime());
        task.setStatus(taskDetails.getStatus());
        return ResponseEntity.ok(ApiResponse.success(checkinTaskRepository.save(task), "更新任务成功"));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateTaskStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        CheckinTask task = checkinTaskRepository.findById(id).orElseThrow();
        String status = body.get("status");
        if (status != null && !status.trim().isEmpty()) {
            task.setStatus(status);
            checkinTaskRepository.save(task);
        }
        return ResponseEntity.ok(ApiResponse.success(null, "任务状态更新成功"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable Long id) {
        checkinTaskRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null, "删除任务成功"));
    }

    // ==========================================
    // 6. 获取打卡审核记录列表 (Records)
    // 解决报错：/api/admin/tasks/records
    // ==========================================
    @GetMapping("/records")
    public ResponseEntity<ApiResponse<Page<Map<String, Object>>>> getTaskRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {

        // 取出所有打卡记录，按时间倒序
        List<CheckinRecord> allRecords = checkinRecordRepository.findAll().stream()
                .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()))
                .collect(Collectors.toList());

        List<Map<String, Object>> dtoList = new ArrayList<>();

        for (CheckinRecord record : allRecords) {
            // 根据状态过滤 (pending, approved, rejected)
            if (status != null && !status.trim().isEmpty() && !status.equals(record.getStatus())) {
                continue;
            }

            // 联表获取用户信息和任务信息
            User user = userRepository.findById(record.getUserId()).orElse(null);
            CheckinTask task = checkinTaskRepository.findById(record.getTaskId()).orElse(null);

            // 组装前端需要的展示数据
            Map<String, Object> dto = new HashMap<>();
            dto.put("id", record.getId());
            dto.put("userId", record.getUserId());
            dto.put("userPhone", user != null ? user.getPhone() : "未知账号");
            dto.put("taskId", record.getTaskId());
            dto.put("taskName", task != null ? task.getName() : "未知任务");
            dto.put("photo", record.getPhoto());
            dto.put("latitude", record.getLatitude());
            dto.put("longitude", record.getLongitude());
            dto.put("status", record.getStatus());
            dto.put("rejectReason", record.getRejectReason());
            dto.put("createdAt", record.getCreatedAt());

            dtoList.add(dto);
        }

        int start = Math.min(page * size, dtoList.size());
        int end = Math.min(start + size, dtoList.size());
        List<Map<String, Object>> pagedList = dtoList.subList(start, end);

        Pageable pageable = PageRequest.of(page, size);
        Page<Map<String, Object>> resultPage = new PageImpl<>(pagedList, pageable, dtoList.size());

        return ResponseEntity.ok(ApiResponse.success(resultPage, "获取打卡记录成功"));
    }

    // ==========================================
    // 7. 审核打卡记录 (通过 / 驳回)
    // ==========================================
    @PutMapping("/records/{id}/audit")
    public ResponseEntity<ApiResponse<Void>> auditRecord(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        CheckinRecord record = checkinRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("打卡记录不存在"));

        String newStatus = body.get("status");
        String rejectReason = body.get("rejectReason");

        if (newStatus != null && !newStatus.trim().isEmpty()) {
            record.setStatus(newStatus);
        }
        if ("rejected".equals(newStatus) && rejectReason != null) {
            record.setRejectReason(rejectReason);
        }

        checkinRecordRepository.save(record);
        return ResponseEntity.ok(ApiResponse.success(null, "审核操作成功"));
    }
}