package com.studyroom.controller;

import com.studyroom.dto.Result;
import com.studyroom.service.ViolationService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/violation")
public class ViolationController {

    private final ViolationService violationService;

    public ViolationController(ViolationService violationService) {
        this.violationService = violationService;
    }

    @GetMapping("/my")
    public Result<List<Map<String, Object>>> getMyViolations(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return violationService.getUserViolations(userId);
    }

    @GetMapping("/count")
    public Result<Integer> getMyViolationCount(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return violationService.getUserViolationCount(userId);
    }

    @GetMapping("/list")
    public Result<List<Map<String, Object>>> getAllViolations(HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("role");
        if (role == null || role != 1) {
            return Result.error(403, "无权限");
        }
        return violationService.getAllViolations();
    }

    @PutMapping("/handle/{id}")
    public Result<String> handleViolation(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestParam Integer status) {
        Integer role = (Integer) request.getAttribute("role");
        if (role == null || role != 1) {
            return Result.error(403, "无权限");
        }
        return violationService.handleViolation(id, status);
    }
}
