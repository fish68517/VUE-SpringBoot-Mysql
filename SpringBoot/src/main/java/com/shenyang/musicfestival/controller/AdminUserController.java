package com.shenyang.musicfestival.controller;

import com.shenyang.musicfestival.dto.admin.AdminUserDTO;
import com.shenyang.musicfestival.entity.PointsHistory;
import com.shenyang.musicfestival.entity.User;
import com.shenyang.musicfestival.repository.PointsHistoryRepository;
import com.shenyang.musicfestival.repository.UserRepository;
import com.shenyang.musicfestival.util.ApiResponse;
import com.shenyang.musicfestival.util.MaskingUtil; // 如果有脱敏工具类就用，没有可以去掉
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserRepository userRepository;
    private final PointsHistoryRepository pointsHistoryRepository;

    // 1. 获取用户列表（带分页和搜索）
    @GetMapping
    public ResponseEntity<ApiResponse<Page<AdminUserDTO>>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<User> userPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            userPage = userRepository.findByPhoneContainingOrNicknameContaining(keyword, keyword, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }

        // 转换为前端需要的 DTO
        Page<AdminUserDTO> dtoPage = userPage.map(user -> AdminUserDTO.builder()
                .id(user.getId())
                .phone(user.getPhone())
                .nickname(user.getNickname())
                .realName(user.getRealName())
                // 身份证脱敏展示，防止隐私泄露
                .idNumber(user.getIdNumber() != null ? user.getIdNumber().replaceAll("(\\d{4})\\d{10}(\\w{4})", "$1**********$2") : "")
                .points(user.getPoints() == null ? 0 : user.getPoints())
                .status((user.getIsBlocked() != null && user.getIsBlocked()) ? "banned" : "active")
                .createdAt(user.getCreatedAt())
                .lastLoginTime(user.getUpdatedAt()) // 演示用
                .build());

        return ResponseEntity.ok(ApiResponse.success(dtoPage, "获取用户列表成功"));
    }

    // 2. 封禁用户
    @PutMapping("/{id}/ban")
    public ResponseEntity<ApiResponse<Void>> banUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        user.setIsBlocked(true);
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success(null, "封禁成功"));
    }

    // 3. 解封用户
    @PutMapping("/{id}/unban")
    public ResponseEntity<ApiResponse<Void>> unbanUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        user.setIsBlocked(false);
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success(null, "解封成功"));
    }

    // 4. 调整用户积分 (后台手动充值/扣减)
    @PostMapping("/{id}/points")
    public ResponseEntity<ApiResponse<Void>> adjustPoints(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Integer points = (Integer) body.get("points");
        String reason = (String) body.get("reason");

        if (points == null || points == 0) throw new IllegalArgumentException("积分变动值不能为0");

        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        Long currentPoints = user.getPoints() == null ? 0L : user.getPoints();

        // 更新积分
        user.setPoints(currentPoints + points);
        userRepository.save(user);

        // 记录后台操作流水
        PointsHistory history = new PointsHistory();
        history.setUserId(user.getId());
        history.setChangeAmount(points);
        history.setChangeType("manual_adjust");
        history.setDescription("管理员调整：" + (reason != null ? reason : "后台修改"));
        pointsHistoryRepository.save(history);

        return ResponseEntity.ok(ApiResponse.success(null, "积分调整成功"));
    }
}