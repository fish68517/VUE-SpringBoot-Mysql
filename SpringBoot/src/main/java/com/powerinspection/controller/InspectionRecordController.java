package com.powerinspection.controller;

import com.powerinspection.common.ApiResponse;
import com.powerinspection.common.BusinessException;
import com.powerinspection.config.AppProperties;
import com.powerinspection.entity.InspectionRecord;
import com.powerinspection.entity.User;
import com.powerinspection.entity.UserRole;
import com.powerinspection.repository.InspectionRecordRepository;
import com.powerinspection.repository.InspectionTaskItemRepository;
import com.powerinspection.security.AuthContext;
import com.powerinspection.service.ViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class InspectionRecordController {

    private final InspectionRecordRepository recordRepository;
    private final InspectionTaskItemRepository taskItemRepository;
    private final ViewService viewService;
    private final AppProperties appProperties;

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> list() {
        User currentUser = AuthContext.getUser();
        List<InspectionRecord> records = currentUser.getRole() == UserRole.INSPECTOR
                ? recordRepository.findByInspectorId(currentUser.getId())
                : recordRepository.findAll();
        return ApiResponse.ok(records.stream()
                .map(record -> viewService.recordMap(record, appProperties.getBaseUrl() + "/images"))
                .toList());
    }

    @GetMapping("/{id}/items")
    public ApiResponse<List<Map<String, Object>>> items(@PathVariable Long id) {
        InspectionRecord record = recordRepository.findById(id).orElseThrow(() -> new BusinessException("巡检记录不存在"));
        User currentUser = AuthContext.getUser();
        if (currentUser.getRole() == UserRole.INSPECTOR && !record.getInspector().getId().equals(currentUser.getId())) {
            throw new BusinessException("只能查看自己的巡检记录明细");
        }
        List<Map<String, Object>> items = taskItemRepository.findByTaskIdOrderBySortOrderAscIdAsc(record.getTask().getId()).stream()
                .map(viewService::taskItemMap)
                .toList();
        return ApiResponse.ok(items);
    }
}
