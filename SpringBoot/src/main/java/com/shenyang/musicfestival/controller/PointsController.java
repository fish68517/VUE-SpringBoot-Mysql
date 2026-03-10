package com.shenyang.musicfestival.controller;

import com.shenyang.musicfestival.dto.*;
import com.shenyang.musicfestival.service.PointsService;
import com.shenyang.musicfestival.util.ApiResponse;
import com.shenyang.musicfestival.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointsController {

    private final PointsService pointsService;
    private final JwtUtil jwtUtil;

    // 安全获取当前登录用户 ID，防报错兜底方法
    private Long extractUserIdSafe(String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                return jwtUtil.extractUserId(token.substring(7));
            }
            if (token != null && !token.isEmpty()) {
                return jwtUtil.extractUserId(token);
            }
        } catch (Exception e) {}
        return 1L; // 演示专用，未登录默认走 1 号测试账户
    }

    @GetMapping("/tasks")
    public ResponseEntity<ApiResponse<List<CheckinTaskDTO>>> getTasks(@RequestHeader(value = "Authorization", required = false) String token) {
        return ResponseEntity.ok(ApiResponse.success(pointsService.getTasks(extractUserIdSafe(token)), "获取任务成功"));
    }

    @PostMapping("/tasks/{taskId}/checkin")
    public ResponseEntity<ApiResponse<Void>> checkin(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable Long taskId) {
        try {
            pointsService.checkin(extractUserIdSafe(token), taskId);
            return ResponseEntity.ok(ApiResponse.success(null, "打卡成功！"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/mall")
    public ResponseEntity<ApiResponse<List<PointsMallItemDTO>>> getMallItems() {
        return ResponseEntity.ok(ApiResponse.success(pointsService.getMallItems(), "获取商城商品成功"));
    }

    @PostMapping("/mall/{itemId}/exchange")
    public ResponseEntity<ApiResponse<Void>> exchange(@RequestHeader(value = "Authorization", required = false) String token,
                                                      @PathVariable Long itemId,
                                                      @RequestBody Map<String, String> body) {
        try {
            pointsService.exchangeItem(extractUserIdSafe(token), itemId, body.get("address"));
            return ResponseEntity.ok(ApiResponse.success(null, "兑换成功！"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<PointsHistoryDTO>>> getHistory(@RequestHeader(value = "Authorization", required = false) String token) {
        return ResponseEntity.ok(ApiResponse.success(pointsService.getHistory(extractUserIdSafe(token)), "获取流水成功"));
    }

    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<Long>> getBalance(@RequestHeader(value = "Authorization", required = false) String token) {
        return ResponseEntity.ok(ApiResponse.success(pointsService.getBalance(extractUserIdSafe(token)), "获取余额成功"));
    }
}