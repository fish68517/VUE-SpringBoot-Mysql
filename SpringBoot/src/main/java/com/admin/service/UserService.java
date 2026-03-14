package com.admin.service;

import com.admin.dto.UserDTO;
import com.admin.dto.UserLoginDTO;
import com.admin.dto.UserRegisterDTO;
import com.admin.entity.User;
import com.admin.mapper.UserMapper;
import com.admin.vo.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * Get user by id
     * 
     * @param id user id
     * @return UserDTO if found, null otherwise
     */
    public UserDTO getUserById(Long id) {
        if (id == null || id <= 0) {
            log.warn("获取用户失败: 无效的用户ID - {}", id);
            return null;
        }

        User user = userMapper.findById(id);
        
        if (user == null) {
            log.warn("获取用户失败: 用户不存在 - {}", id);
            return null;
        }

        return convertToDTO(user);
    }

    /**
     * Get all users with pagination and search
     * 
     * @param page page number (1-based)
     * @param pageSize page size
     * @param searchKeyword search keyword for username or email
     * @return PageResponse with user list
     */
    public PageResponse<UserDTO> getUserList(int page, int pageSize, String searchKeyword) {
        if (page < 1) {
            page = 1;
        }
        if (pageSize < 1 || pageSize > 100) {
            pageSize = 10;
        }

        int offset = (page - 1) * pageSize;
        
        // Handle empty search keyword
        if (searchKeyword != null && searchKeyword.trim().isEmpty()) {
            searchKeyword = null;
        }

        List<User> users = userMapper.findAll(offset, pageSize, searchKeyword);
        int total = userMapper.countAll(searchKeyword);

        List<UserDTO> userDTOs = users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        log.info("获取用户列表: 页码={}, 页大小={}, 搜索关键词={}, 总数={}", page, pageSize, searchKeyword, total);

        return new PageResponse<>(userDTOs, total, page, pageSize);
    }

    /**
     * Update user information
     * 
     * @param id user id
     * @param userDTO user data to update
     * @return true if update successful, false otherwise
     */
    public boolean updateUser(Long id, UserDTO userDTO) {
        if (id == null || id <= 0) {
            log.warn("更新用户失败: 无效的用户ID - {}", id);
            return false;
        }

        User existingUser = userMapper.findById(id);
        if (existingUser == null) {
            log.warn("更新用户失败: 用户不存在 - {}", id);
            return false;
        }

        User user = new User();
        user.setId(id);
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setHeight(userDTO.getHeight());
        user.setWeight(userDTO.getWeight());
        user.setAvatar(userDTO.getAvatar());

        int result = userMapper.update(user);
        
        if (result > 0) {
            log.info("用户信息更新成功: {}", id);
            return true;
        } else {
            log.warn("用户信息更新失败: {}", id);
            return false;
        }
    }

    /**
     * Delete user by id
     * 
     * @param id user id
     * @return true if delete successful, false otherwise
     */
    public boolean deleteUser(Long id) {
        if (id == null || id <= 0) {
            log.warn("删除用户失败: 无效的用户ID - {}", id);
            return false;
        }

        User existingUser = userMapper.findById(id);
        if (existingUser == null) {
            log.warn("删除用户失败: 用户不存在 - {}", id);
            return false;
        }

        int result = userMapper.deleteById(id);
        
        if (result > 0) {
            log.info("用户删除成功: {}", id);
            return true;
        } else {
            log.warn("用户删除失败: {}", id);
            return false;
        }
    }

    /**
     * Convert User entity to UserDTO
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setHeight(user.getHeight());
        dto.setWeight(user.getWeight());
        dto.setAvatar(user.getAvatar());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }

    public boolean register(UserRegisterDTO registerDTO) {
        // 1. 检查用户名是否已存在
        // ...

        // 2. 创建实体类并赋值
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(registerDTO.getPassword()); // 建议这里做一次 MD5 或 BCrypt 加密
        user.setEmail(registerDTO.getEmail()); // 前提是你的 User 实体类和数据库表里有 phone 字段

        // 3. 插入数据库
         int result = userMapper.insert(user);
         return result > 0;


    }

    /**
     * User login validation
     */
    public UserDTO login(UserLoginDTO loginDTO) {
        // 根据账号(用户名)查询用户
        User user = userMapper.findByUsername(loginDTO.getAccount());

        if (user == null) {
            log.warn("用户登录失败: 账号不存在 - {}", loginDTO.getAccount());
            return null;
        }

        // 校验密码 (如果你注册时用了加密，这里需要用对应的解密/匹配逻辑。如果是明文直接用 equals)
        if (!user.getPassword().equals(loginDTO.getPassword())) {
            log.warn("用户登录失败: 密码错误 - {}", loginDTO.getAccount());
            return null;
        }

        log.info("用户登录成功: {}", loginDTO.getAccount());

        // 登录成功，返回不包含密码的 DTO 给前端
        return convertToDTO(user);
    }
}
