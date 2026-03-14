package com.admin.controller;

import com.admin.dto.AdminDTO;
import com.admin.dto.LoginDTO;
import com.admin.service.AdminService;
import com.admin.vo.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * Admin login endpoint
     * 

     * @return ApiResponse with AdminDTO if successful, error response otherwise
     */
/*    @PostMapping("/login")
    public ApiResponse<AdminDTO> login(@RequestParam String username, @RequestParam String password) {
        log.info("管理员登录请求: {}", username);
        
        if (username == null || username.trim().isEmpty()) {
            return ApiResponse.error(400, "用户名不能为空");
        }
        
        if (password == null || password.trim().isEmpty()) {
            return ApiResponse.error(400, "密码不能为空");
        }

        AdminDTO adminDTO = adminService.login(username, password);
        
        if (adminDTO == null) {
            return ApiResponse.error(401, "用户名或密码错误");
        }

        return ApiResponse.success("登录成功", adminDTO);
    }*/


    // 修改 AdminController 中的 login 方法
    @PostMapping("/login")
    public ApiResponse<AdminDTO> login(@RequestBody LoginDTO loginDTO) {
        AdminDTO adminDT  = adminService.login(loginDTO.getUsername(), loginDTO.getPassword());

        if (adminDT == null) {
            return ApiResponse.error(401, "用户名或密码错误");
        }

        return ApiResponse.success("登录成功", adminDT);
    }

    /**
     * Admin logout endpoint
     * 
     * @return ApiResponse with success message
     */
    @PostMapping("/logout")
    public ApiResponse<Object> logout() {
        log.info("管理员登出");
        return ApiResponse.success("登出成功", null);
    }
}
