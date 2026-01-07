package server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.mapper.UserMapper;
import server.model.User;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserMapper userMapper;

    // 分页查询用户列表
    @GetMapping
    public ResponseEntity<?> getUsers(
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
        result.put("items", users);
        
        return ResponseEntity.ok(result);
    }

    // 获取单个用户
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        User user = userMapper.findById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    // 创建用户
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        // 检查用户名是否已存在
        if (userMapper.findByUsername(user.getUsername()) != null) {
            return ResponseEntity.badRequest().body("用户名已存在");
        }
        
        userMapper.insert(user);
        return ResponseEntity.ok(user);
    }

    // 更新用户
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Integer id,
            @RequestBody User user) {
        User existingUser = userMapper.findById(id);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }

        user.setId(id);
        // 不更新密码
        user.setPassword(existingUser.getPassword());
        userMapper.update(user);
        return ResponseEntity.ok(user);
    }

    // 删除用户
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        User existingUser = userMapper.findById(id);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }

        userMapper.delete(id);
        return ResponseEntity.ok().build();
    }

    // 修改密码
    @PutMapping("/{id}/password")
    public ResponseEntity<?> updatePassword(
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
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            // 这里可以添加token黑名单逻辑，使token失效
            // tokenBlacklistService.addToBlacklist(token);
        }
        
        return ResponseEntity.ok().build();
    }

    /**
     * 获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/info")
    public User getUserInfo(@RequestAttribute Integer userId) {
        return userMapper.findById(userId);
    }

    /**
     * 更新用户信息
     * @param userId 用户ID
     * @param user 用户信息
     * @return 更新结果
     */
    @PutMapping("/info")
    public ResponseEntity<?> updateUserInfo(
            @RequestAttribute Integer userId,
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
            String avatarUrl = userMapper.uploadAvatar(userId, file);
            return ResponseEntity.ok().body(Map.of("message", "头像上传成功", "data", avatarUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "头像上传失败: " + e.getMessage()));
        }
    }
} 