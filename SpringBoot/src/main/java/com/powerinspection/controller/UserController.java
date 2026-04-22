package com.powerinspection.controller;

import com.powerinspection.common.ApiResponse;
import com.powerinspection.common.BusinessException;
import com.powerinspection.dto.UserRequest;
import com.powerinspection.entity.User;
import com.powerinspection.entity.UserRole;
import com.powerinspection.logging.OperationLogRecord;
import com.powerinspection.repository.UserRepository;
import com.powerinspection.security.AuthContext;
import com.powerinspection.service.ViewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final ViewService viewService;

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> list(@RequestParam(required = false) UserRole role) {
        AuthContext.requireRole(UserRole.ADMIN);
        List<User> users = role == null ? userRepository.findAll() : userRepository.findByRole(role);
        return ApiResponse.ok(users.stream().map(viewService::userMap).toList());
    }

    @GetMapping("/options")
    public ApiResponse<List<Map<String, Object>>> options(@RequestParam UserRole role) {
        List<Map<String, Object>> list = userRepository.findByRole(role).stream()
                .map(user -> Map.<String, Object>of(
                        "id", user.getId(),
                        "label", user.getRealName() + "(" + user.getUsername() + ")"
                ))
                .toList();
        return ApiResponse.ok(list);
    }

    @PostMapping
    @OperationLogRecord(module = "用户管理", action = "新增用户")
    public ApiResponse<Map<String, Object>> create(@Valid @RequestBody UserRequest request) {
        AuthContext.requireRole(UserRole.ADMIN);
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BusinessException("用户名已存在");
        }
        User user = new User();
        fillUser(user, request);
        return ApiResponse.ok("新增成功", viewService.userMap(userRepository.save(user)));
    }

    @PutMapping("/{id}")
    @OperationLogRecord(module = "用户管理", action = "修改用户")
    public ApiResponse<Map<String, Object>> update(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        AuthContext.requireRole(UserRole.ADMIN);
        User user = userRepository.findById(id).orElseThrow(() -> new BusinessException("用户不存在"));
        if (!user.getUsername().equals(request.getUsername())
                && userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BusinessException("用户名已存在");
        }
        fillUser(user, request);
        return ApiResponse.ok("修改成功", viewService.userMap(userRepository.save(user)));
    }

    @DeleteMapping("/{id}")
    @OperationLogRecord(module = "用户管理", action = "删除用户")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        AuthContext.requireRole(UserRole.ADMIN);
        userRepository.deleteById(id);
        return ApiResponse.ok("删除成功", null);
    }

    private void fillUser(User user, UserRequest request) {
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setRealName(request.getRealName());
        user.setRole(request.getRole());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setStatus(request.getStatus());
    }
}
