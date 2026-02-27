package com.studyroom.controller;

import com.studyroom.dto.*;
import com.studyroom.entity.User;
import com.studyroom.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody @Validated LoginDTO dto) {
        return userService.login(dto);
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody @Validated RegisterDTO dto) {
        System.out.println("注册进度。。。。");
        return userService.register(dto);
    }

    @GetMapping("/info")
    public Result<Map<String, Object>> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return userService.getUserInfo(userId);
    }

    @PutMapping("/update")
    public Result<String> updateUser(HttpServletRequest request, @RequestBody UserUpdateDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        return userService.updateUser(userId, dto);
    }

    @GetMapping("/list")
    public Result<List<User>> listUsers(HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("role");
        if (role == null || role != 1) {
            return Result.error(403, "无权限");
        }
        return userService.listUsers();
    }

    @PutMapping("/status/{userId}")
    public Result<String> updateUserStatus(
            HttpServletRequest request,
            @PathVariable Long userId,
            @RequestParam Integer status) {
        Integer role = (Integer) request.getAttribute("role");
        if (role == null || role != 1) {
            return Result.error(403, "无权限");
        }
        return userService.updateUserStatus(userId, status);
    }
}
