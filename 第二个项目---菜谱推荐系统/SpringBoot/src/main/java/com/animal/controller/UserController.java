package com.animal.controller;

import com.animal.mapper.UserMapper;
import com.animal.model.Result;
import com.animal.model.User;
import com.animal.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.print.Pageable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    /*@GetMapping
    public List<User> getAllUsers() {
        return userMapper.findAll();
    }*/




    @GetMapping("/{id}")
    public User getUserById(@RequestParam Integer userId,@PathVariable Integer id) {
        return userMapper.findById(id);
    }

    @GetMapping("/info")
    public User getUserByUserId(@RequestParam("userId") Integer userId) {
        return userMapper.findById(userId);
    }

    @PostMapping("")
    public User createUser(@RequestParam Integer userId,@RequestBody User user) {
        userMapper.insert(user);
        return user;
    }

    /**
     * 更新用户信息
     * @param userId 用户ID
     * @param user 用户信息
     * @return 更新结果
     */
    @PutMapping("/info")
    public ResponseEntity<?> updateUserInfo(
            @RequestParam Integer userId,
            @RequestBody User user) {
        User existingUser = userMapper.findById(userId);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }

        user.setId(userId);
        // 不更新密码
        user.setPassword(existingUser.getPassword());
        userMapper.update(user);
        return ResponseEntity.ok(user);
    }


    @PutMapping("/{id}")
    public User updateUser(@RequestParam Integer userId,@PathVariable Integer id, @RequestBody User user) {
        user.setId(id);
        userMapper.update(user);
        return user;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@RequestParam Integer userId,@PathVariable Integer id) {
        userMapper.delete(id);
    }


    @PostMapping("/login")
    public Result login(@RequestParam Integer userId,@RequestBody User user) {
        User existUser = userMapper.selectOne(
                new QueryWrapper<User>()
                        .eq("username", user.getUsername())
                        .eq("password", user.getPassword())
        );
        if (existUser != null) {
            return Result.success(existUser);
        }
        return Result.error("用户名或密码错误");
    }


    // 分页查询用户列表
    @GetMapping
    public ResponseEntity<?> getUsers(@RequestParam Integer userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String query) {
        // 计算偏移量
        int offset = (page - 1) * pageSize;

        // 获取总数
        int total = userMapper.count(query);

        // 获取分页数据
        List<User> users = userMapper.findByPage(offset, pageSize, query);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("data", users);

        return ResponseEntity.ok(result);
    }





    // 修改密码
    @PutMapping("/{id}/password")
    public ResponseEntity<?> updatePassword(@RequestParam Integer userId,
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        User user = userMapper.findById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");

        // 验证旧密码
        if (!user.getPassword().equals(oldPassword)) {
            return ResponseEntity.badRequest().body("旧密码错误");
        }

        userMapper.updatePassword(id, newPassword);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        // 获取 token
      /*  String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            // 这里可以添加token黑名单逻辑，使token失效
            // tokenBlacklistService.addToBlacklist(token);
        }*/

        return ResponseEntity.ok().build();
    }




    /**
     * 上传用户头像
     * @param userId 用户ID
     * @param file 头像文件
     * @return 上传结果
     */
    @PostMapping("/uploadAvatar")
    public ResponseEntity<?> uploadAvatar(
            @RequestAttribute Integer userId,
            @RequestParam("file") MultipartFile file) {
        try {
            String avatarUrl = userService.uploadAvatar(userId, file);
            return ResponseEntity.ok().body(Map.of("message", "头像上传成功", "data", avatarUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "头像上传失败: " + e.getMessage()));
        }
    }
} 