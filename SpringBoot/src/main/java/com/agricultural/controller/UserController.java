package com.agricultural.controller;

import com.agricultural.dto.Result;
import com.agricultural.entity.User;
import com.agricultural.service.UserService;
import com.agricultural.util.LoggerUtil;
import com.agricultural.util.StringUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制层
 * 
 * 处理用户相关的HTTP请求，包括注册、登录、查询、更新、删除等操作
 * 
 * @author Agricultural Platform Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册接口
     * 
     * @param username 用户名
     * @param password 密码
     * @param email 邮箱
     * @param phone 手机号
     * @param userType 用户类型 (FARMER, MERCHANT, ADMIN)
     * @param region 地区
     * @return 注册成功的用户信息
     */
    @PostMapping("/register")
    public Result<User> register(
            @RequestParam @NotBlank(message = "用户名不能为空") String username,
            @RequestParam @NotBlank(message = "密码不能为空") String password,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam @NotNull(message = "用户类型不能为空") User.UserType userType,
            @RequestParam(required = false) String region) {
        
        LoggerUtil.info("收到用户注册请求，用户名: {}", username);
        
        try {
            // 参数验证
            if (StringUtil.isBlank(username)) {
                LoggerUtil.warn("用户注册请求参数验证失败: 用户名为空");
                return Result.validationError("用户名不能为空");
            }
            
            if (StringUtil.isBlank(password)) {
                LoggerUtil.warn("用户注册请求参数验证失败: 密码为空");
                return Result.validationError("密码不能为空");
            }
            
            if (userType == null) {
                LoggerUtil.warn("用户注册请求参数验证失败: 用户类型为空");
                return Result.validationError("用户类型不能为空");
            }
            
            // 调用业务层进行注册
            User registeredUser = userService.register(username, password, email, phone, userType, region);
            
            LoggerUtil.info("用户注册成功，用户ID: {}, 用户名: {}", registeredUser.getId(), username);
            return Result.success("用户注册成功", registeredUser);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("用户注册失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("用户注册异常: " + e.getMessage(), e);
            return Result.error("用户注册失败，请稍后重试");
        }
    }

    /**
     * 用户登录接口
     * 
     * @param username 用户名
     * @param password 密码
     * @return 登录成功的用户信息
     */
    @PostMapping("/login")
    public Result<User> login(
            @RequestParam @NotBlank(message = "用户名不能为空") String username,
            @RequestParam @NotBlank(message = "密码不能为空") String password) {
        
        LoggerUtil.info("收到用户登录请求，用户名: {}", username);
        
        try {
            // 参数验证
            if (StringUtil.isBlank(username)) {
                LoggerUtil.warn("用户登录请求参数验证失败: 用户名为空");
                return Result.validationError("用户名不能为空");
            }
            
            if (StringUtil.isBlank(password)) {
                LoggerUtil.warn("用户登录请求参数验证失败: 密码为空");
                return Result.validationError("密码不能为空");
            }
            
            // 调用业务层进行登录
            User loginUser = userService.login(username, password);
            
            LoggerUtil.info("用户登录成功，用户ID: {}, 用户名: {}", loginUser.getId(), username);
            return Result.success("用户登录成功", loginUser);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("用户登录失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("用户登录异常: " + e.getMessage(), e);
            return Result.error("用户登录失败，请稍后重试");
        }
    }

    /**
     * 获取用户信息接口
     * 
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public Result<User> getUserInfo(@PathVariable @NotNull(message = "用户ID不能为空") Long id) {
        
        LoggerUtil.info("收到获取用户信息请求，用户ID: {}", id);
        
        try {
            // 参数验证
            if (id == null || id <= 0) {
                LoggerUtil.warn("获取用户信息请求参数验证失败: 用户ID无效");
                return Result.validationError("用户ID无效");
            }
            
            // 调用业务层获取用户信息
            User user = userService.getUserById(id);
            
            LoggerUtil.info("获取用户信息成功，用户ID: {}", id);
            return Result.success(user);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("获取用户信息失败: {}", e.getMessage());
            return Result.notFound(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("获取用户信息异常: " + e.getMessage(), e);
            return Result.error("获取用户信息失败，请稍后重试");
        }
    }

    /**
     * 更新用户信息接口
     * 
     * @param id 用户ID
     * @param email 邮箱
     * @param phone 手机号
     * @param region 地区
     * @return 更新后的用户信息
     */
    @PutMapping("/{id}")
    public Result<User> updateUser(
            @PathVariable @NotNull(message = "用户ID不能为空") Long id,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String region) {
        
        LoggerUtil.info("收到更新用户信息请求，用户ID: {}", id);
        
        try {
            // 参数验证
            if (id == null || id <= 0) {
                LoggerUtil.warn("更新用户信息请求参数验证失败: 用户ID无效");
                return Result.validationError("用户ID无效");
            }
            
            // 检查是否至少提供了一个要更新的字段
            if (StringUtil.isBlank(email) && StringUtil.isBlank(phone) && StringUtil.isBlank(region)) {
                LoggerUtil.warn("更新用户信息请求参数验证失败: 没有提供要更新的字段");
                return Result.validationError("至少需要提供一个要更新的字段");
            }
            
            // 调用业务层更新用户信息
            User updatedUser = userService.updateUser(id, email, phone, region);
            
            LoggerUtil.info("更新用户信息成功，用户ID: {}", id);
            return Result.success("用户信息更新成功", updatedUser);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("更新用户信息失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("更新用户信息异常: " + e.getMessage(), e);
            return Result.error("更新用户信息失败，请稍后重试");
        }
    }

    /**
     * 删除用户接口
     * 
     * @param id 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable @NotNull(message = "用户ID不能为空") Long id) {
        
        LoggerUtil.info("收到删除用户请求，用户ID: {}", id);
        
        try {
            // 参数验证
            if (id == null || id <= 0) {
                LoggerUtil.warn("删除用户请求参数验证失败: 用户ID无效");
                return Result.validationError("用户ID无效");
            }
            
            // 调用业务层删除用户
            userService.deleteUser(id);
            
            LoggerUtil.info("删除用户成功，用户ID: {}", id);
            return Result.success("用户删除成功");
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("删除用户失败: {}", e.getMessage());
            return Result.notFound(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("删除用户异常: " + e.getMessage(), e);
            return Result.error("删除用户失败，请稍后重试");
        }
    }

    /**
     * 修改用户密码接口
     * 
     * @param id 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改结果
     */
    @PostMapping("/{id}/change-password")
    public Result<User> changePassword(
            @PathVariable @NotNull(message = "用户ID不能为空") Long id,
            @RequestParam @NotBlank(message = "旧密码不能为空") String oldPassword,
            @RequestParam @NotBlank(message = "新密码不能为空") String newPassword) {
        
        LoggerUtil.info("收到修改用户密码请求，用户ID: {}", id);
        
        try {
            // 参数验证
            if (id == null || id <= 0) {
                LoggerUtil.warn("修改密码请求参数验证失败: 用户ID无效");
                return Result.validationError("用户ID无效");
            }
            
            if (StringUtil.isBlank(oldPassword)) {
                LoggerUtil.warn("修改密码请求参数验证失败: 旧密码为空");
                return Result.validationError("旧密码不能为空");
            }
            
            if (StringUtil.isBlank(newPassword)) {
                LoggerUtil.warn("修改密码请求参数验证失败: 新密码为空");
                return Result.validationError("新密码不能为空");
            }
            
            // 调用业务层修改密码
            User updatedUser = userService.changePassword(id, oldPassword, newPassword);
            
            LoggerUtil.info("修改用户密码成功，用户ID: {}", id);
            return Result.success("密码修改成功", updatedUser);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("修改密码失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("修改密码异常: " + e.getMessage(), e);
            return Result.error("修改密码失败，请稍后重试");
        }
    }

    /**
     * 检查用户名是否存在接口
     * 
     * @param username 用户名
     * @return 检查结果
     */
    @GetMapping("/check/username")
    public Result<Boolean> checkUsernameExists(
            @RequestParam @NotBlank(message = "用户名不能为空") String username) {
        
        LoggerUtil.info("收到检查用户名是否存在请求，用户名: {}", username);
        
        try {
            // 参数验证
            if (StringUtil.isBlank(username)) {
                LoggerUtil.warn("检查用户名请求参数验证失败: 用户名为空");
                return Result.validationError("用户名不能为空");
            }
            
            // 调用业务层检查用户名
            boolean exists = userService.usernameExists(username);
            
            LoggerUtil.info("检查用户名完成，用户名: {}, 存在: {}", username, exists);
            return Result.success(exists);
            
        } catch (Exception e) {
            LoggerUtil.error("检查用户名异常: " + e.getMessage(), e);
            return Result.error("检查用户名失败，请稍后重试");
        }
    }

    /**
     * 检查邮箱是否存在接口
     * 
     * @param email 邮箱
     * @return 检查结果
     */
    @GetMapping("/check/email")
    public Result<Boolean> checkEmailExists(
            @RequestParam @NotBlank(message = "邮箱不能为空") String email) {
        
        LoggerUtil.info("收到检查邮箱是否存在请求，邮箱: {}", email);
        
        try {
            // 参数验证
            if (StringUtil.isBlank(email)) {
                LoggerUtil.warn("检查邮箱请求参数验证失败: 邮箱为空");
                return Result.validationError("邮箱不能为空");
            }
            
            // 调用业务层检查邮箱
            boolean exists = userService.emailExists(email);
            
            LoggerUtil.info("检查邮箱完成，邮箱: {}, 存在: {}", email, exists);
            return Result.success(exists);
            
        } catch (Exception e) {
            LoggerUtil.error("检查邮箱异常: " + e.getMessage(), e);
            return Result.error("检查邮箱失败，请稍后重试");
        }
    }
}
