package com.admin.service;

import com.admin.dto.AdminDTO;
import com.admin.entity.Admin;
import com.admin.mapper.AdminMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AdminService {

    @Autowired
    private AdminMapper adminMapper;

    /**
     * Admin login with username and password
     * Validates credentials using plaintext password comparison
     * 
     * @param username admin username
     * @param password admin password (plaintext)
     * @return AdminDTO if login successful, null otherwise
     */
    public AdminDTO login(String username, String password) {
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            log.warn("登录失败: 用户名或密码为空");
            return null;
        }

        Admin admin = adminMapper.findByUsername(username);
        
        if (admin == null) {
            log.warn("登录失败: 用户名不存在 - {}", username);
            return null;
        }

        // Plaintext password verification
        if (!admin.getPassword().equals(password)) {
            log.warn("登录失败: 密码错误 - {}", username);
            return null;
        }

        log.info("管理员登录成功: {}", username);
        
        // Return AdminDTO without password
        AdminDTO adminDTO = new AdminDTO();
        adminDTO.setId(admin.getId());
        adminDTO.setUsername(admin.getUsername());
        adminDTO.setEmail(admin.getEmail());
        
        return adminDTO;
    }

    /**
     * Get admin by id
     * 
     * @param id admin id
     * @return AdminDTO if found, null otherwise
     */
    public AdminDTO getAdminById(Long id) {
        Admin admin = adminMapper.findById(id);
        
        if (admin == null) {
            return null;
        }

        AdminDTO adminDTO = new AdminDTO();
        adminDTO.setId(admin.getId());
        adminDTO.setUsername(admin.getUsername());
        adminDTO.setEmail(admin.getEmail());
        
        return adminDTO;
    }
}
