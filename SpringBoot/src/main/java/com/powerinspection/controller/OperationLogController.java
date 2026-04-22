package com.powerinspection.controller;

import com.powerinspection.common.ApiResponse;
import com.powerinspection.entity.UserRole;
import com.powerinspection.repository.OperationLogRepository;
import com.powerinspection.security.AuthContext;
import com.powerinspection.service.ViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/operation-logs")
@RequiredArgsConstructor
public class OperationLogController {

    private final OperationLogRepository operationLogRepository;
    private final ViewService viewService;

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> list() {
        AuthContext.requireRole(UserRole.ADMIN);
        List<Map<String, Object>> logs = operationLogRepository.findAll().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(viewService::operationLogMap)
                .toList();
        return ApiResponse.ok(logs);
    }
}
