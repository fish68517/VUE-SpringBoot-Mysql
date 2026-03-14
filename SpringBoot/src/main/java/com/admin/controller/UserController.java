package com.admin.controller;

import com.admin.dto.UserDTO;
import com.admin.dto.UserLoginDTO;
import com.admin.dto.UserRegisterDTO;
import com.admin.service.UserService;
import com.admin.vo.ApiResponse;
import com.admin.vo.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/admin/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Get user list with pagination and search
     * 
     * @param page page number (default 1)
     * @param pageSize page size (default 10)
     * @param search search keyword for username or email
     * @return ApiResponse with PageResponse containing user list
     */
    @GetMapping
    public ApiResponse<PageResponse<UserDTO>> getUserList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String search) {
        
        log.info("获取用户列表请求: page={}, pageSize={}, search={}", page, pageSize, search);
        
        if (page < 1) {
            return ApiResponse.error(400, "页码必须大于0");
        }
        
        if (pageSize < 1 || pageSize > 100) {
            return ApiResponse.error(400, "页大小必须在1-100之间");
        }

        PageResponse<UserDTO> pageResponse = userService.getUserList(page, pageSize, search);
        return ApiResponse.success("获取用户列表成功", pageResponse);
    }

    /**
     * Get user details by id
     * 
     * @param id user id
     * @return ApiResponse with UserDTO if found, error response otherwise
     */
    @GetMapping("/{id}")
    public ApiResponse<UserDTO> getUserById(@PathVariable Long id) {
        log.info("获取用户详情请求: id={}", id);
        
        if (id == null || id <= 0) {
            return ApiResponse.error(400, "用户ID无效");
        }

        UserDTO userDTO = userService.getUserById(id);
        
        if (userDTO == null) {
            return ApiResponse.error(404, "用户不存在");
        }

        return ApiResponse.success("获取用户详情成功", userDTO);
    }

    /**
     * Update user information
     * 
     * @param id user id
     * @param userDTO user data to update
     * @return ApiResponse with success or error message
     */
    @PutMapping("/{id}")
    public ApiResponse<Object> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        log.info("更新用户信息请求: id={}", id);
        
        if (id == null || id <= 0) {
            return ApiResponse.error(400, "用户ID无效");
        }

        if (userDTO == null) {
            return ApiResponse.error(400, "用户数据不能为空");
        }

        boolean success = userService.updateUser(id, userDTO);
        
        if (!success) {
            return ApiResponse.error(404, "用户不存在或更新失败");
        }

        return ApiResponse.success("用户信息更新成功", null);
    }

    /**
     * Delete user by id
     * 
     * @param id user id
     * @return ApiResponse with success or error message
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Object> deleteUser(@PathVariable Long id) {
        log.info("删除用户请求: id={}", id);
        
        if (id == null || id <= 0) {
            return ApiResponse.error(400, "用户ID无效");
        }

        boolean success = userService.deleteUser(id);
        
        if (!success) {
            return ApiResponse.error(404, "用户不存在或删除失败");
        }

        return ApiResponse.success("用户删除成功", null);
    }


    /**
     * User registration
     * * @param registerDTO user registration data
     * @return ApiResponse with success or error message
     */
    @PostMapping("/register")
    public ApiResponse<Object> register(@RequestBody UserRegisterDTO registerDTO) {
        log.info("用户注册请求: username={}", registerDTO.getUsername());

        // 1. 基础参数校验
        if (registerDTO.getUsername() == null || registerDTO.getUsername().trim().isEmpty()) {
            return ApiResponse.error(400, "用户名不能为空");
        }
        if (registerDTO.getPassword() == null || registerDTO.getPassword().trim().isEmpty()) {
            return ApiResponse.error(400, "密码不能为空");
        }

        // 2. 调用 Service 层进行注册逻辑 (需要在 UserService 中实现此方法)
        boolean success = userService.register(registerDTO);

        if (!success) {
            return ApiResponse.error(400, "注册失败，用户名或手机号可能已存在");
        }

        return ApiResponse.success("用户注册成功", null);
    }


    /**
     * User login
     * @param loginDTO user login data
     * @return ApiResponse with UserDTO if successful, error response otherwise
     */
    @PostMapping("/login")
    public ApiResponse<UserDTO> login(@RequestBody UserLoginDTO loginDTO) {
        log.info("用户登录请求: account={}", loginDTO.getAccount());

        if (loginDTO.getAccount() == null || loginDTO.getAccount().trim().isEmpty()) {
            return ApiResponse.error(400, "账号不能为空");
        }
        if (loginDTO.getPassword() == null || loginDTO.getPassword().trim().isEmpty()) {
            return ApiResponse.error(400, "密码不能为空");
        }

        // 调用 Service 层处理登录
        UserDTO userDTO = userService.login(loginDTO);

        if (userDTO == null) {
            return ApiResponse.error(401, "账号或密码错误");
        }

        return ApiResponse.success("登录成功", userDTO);
    }
}
