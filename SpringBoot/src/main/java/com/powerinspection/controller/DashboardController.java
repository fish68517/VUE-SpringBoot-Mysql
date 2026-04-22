package com.powerinspection.controller;

import com.powerinspection.common.ApiResponse;
import com.powerinspection.entity.UserRole;
import com.powerinspection.repository.*;
import com.powerinspection.security.AuthContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final InspectionTaskRepository taskRepository;
    private final InspectionRecordRepository recordRepository;
    private final DefectRepository defectRepository;
    private final RepairOrderRepository repairOrderRepository;

    @GetMapping("/summary")
    public ApiResponse<Map<String, Object>> summary() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("currentRole", AuthContext.getUser().getRole());
        map.put("userCount", userRepository.count());
        map.put("deviceCount", deviceRepository.count());
        map.put("taskCount", taskRepository.count());
        map.put("recordCount", recordRepository.count());
        map.put("defectCount", defectRepository.count());
        map.put("repairOrderCount", repairOrderRepository.count());
        map.put("adminCount", userRepository.findByRole(UserRole.ADMIN).size());
        map.put("inspectorCount", userRepository.findByRole(UserRole.INSPECTOR).size());
        map.put("maintainerCount", userRepository.findByRole(UserRole.MAINTAINER).size());
        return ApiResponse.ok(map);
    }
}
