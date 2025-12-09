package com.graduation.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.graduation.dto.LoginRequest;
import com.graduation.dto.RegisterDto;
import com.graduation.entity.Users;
import com.graduation.service.SystemLogsService;
import com.graduation.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.graduation.common.BaseController;

import java.util.Map;

/**
 * <p>
 * 系统用户账户表 前端控制器
 * </p>
 *
 * @author 张三
 * @since 2025-10-12
 */
@RestController
@RequestMapping("/users")
public class UsersController extends BaseController<UsersService, Users> {

    @Autowired
    private SystemLogsService systemLogsService;

    /**
     * 用户登录接口
     * @param loginRequest 包含用户名和密码的请求体
     * @return 登录成功返回用户信息，失败则返回错误信息
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // 1. 根据用户名查询用户
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", loginRequest.getUsername());
        Users user = service.getOne(queryWrapper);

        // 2. 检查用户是否存在
        if (user == null) {
            // 为了安全，通常返回模糊的错误信息
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "用户名或密码错误"));
        }

        // 3. 检查账户是否被禁用
        if (!user.getIsActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "该账户已被禁用"));
        }




        // 4. 验证密码
        // 注意：在实际生产环境中，密码应该是加密存储的，这里根据 db.sql 的设计进行明文比较。
        if (loginRequest.getPassword().equals(user.getPasswordHash())) {
            // 登录成功
            // 实际项目中，这里会生成一个 token 返回给前端
            systemLogsService.log(user.getId(), user.getUsername(), "LOGIN", null, "用户登录成功");
            return ResponseEntity.ok(user);
        } else {
            // 密码错误
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "用户名或密码错误"));
        }
    }


    // --- 重写 update 方法以记录敏感操作 (如修改状态、角色) ---
    @Override
    @PutMapping
    public boolean update(@RequestBody Users entity) {
        // 获取旧数据
        Users oldUser = service.getById(entity.getId());
        boolean success = service.updateById(entity);
        System.out.println("更新用户: " + entity.getUsername());

        if (success && oldUser != null) {
            // 简单的差异检测
            StringBuilder changes = new StringBuilder();
            if (!oldUser.getIsActive().equals(entity.getIsActive())) {
                changes.append("状态变更: ").append(oldUser.getIsActive()).append("->").append(entity.getIsActive()).append("; ");
            }
            if (entity.getRoleId() != null && !oldUser.getRoleId().equals(entity.getRoleId())) {
                changes.append("角色变更: ").append(oldUser.getRoleId()).append("->").append(entity.getRoleId()).append("; ");
            }
            // ... 可以添加更多检测
            // 用户名变更
            if (!oldUser.getUsername().equals(entity.getUsername())) {
                changes.append("用户名变更: ").append(oldUser.getUsername()).append("->").append(entity.getUsername()).append("; ");
            }
            // 全名变更
            if (!oldUser.getFullName().equals(entity.getFullName())) {
                changes.append("全名变更: ").append(oldUser.getFullName()).append("->").append(entity.getFullName()).append("; ");
            }
            // 密码变更
            if (!oldUser.getPasswordHash().equals(entity.getPasswordHash())) {
                changes.append("密码变更: ").append("******").append("->").append("******").append("; ");
            }
            // 部门变更
            if (entity.getDepartmentId() != null && !oldUser.getDepartmentId().equals(entity.getDepartmentId())) {
                changes.append("部门变更: ").append(oldUser.getDepartmentId()).append("->").append(entity.getDepartmentId()).append("; ");
            }

            if (changes.length() > 0) {
                // TODO: 实际项目中应从 SecurityContext 获取当前操作员ID，这里暂时写死为 1 (admin)
                systemLogsService.log(oldUser.getId(), "admin", "UPDATE_USER", String.valueOf(entity.getId()), "更新用户 [" + oldUser.getUsername() + "]: " + changes.toString());
            }
        }
        return success;
    }


    // --- 重写 delete 方法以记录删除操作 ---
    @Override
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable("id") Long id) {
        Users targetUser = service.getById(id);
        boolean success = service.removeById(id);
        System.out.println("删除用户: " + targetUser.getUsername());

        if (success && targetUser != null) {
            // TODO: 实际项目中应从 SecurityContext 获取当前操作员ID
            systemLogsService.log(targetUser.getId(), "admin", "DELETE_USER", String.valueOf(id), "删除了用户: " + targetUser.getUsername() + " (" + targetUser.getFullName() + ")");
        }
        return success;
    }

    // register接口
    // {"username":"admin123","passwordHash":"123456","fullName":"张管理","departmentId":null,"roleId":3,"isActive":true}
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto userDto) {
        Users newUser = new Users();
        newUser.setUsername(userDto.getUsername());
        newUser.setPasswordHash(userDto.getPasswordHash());
        newUser.setFullName(userDto.getFullName());
        newUser.setRoleId(userDto.getRoleId());
        newUser.setDepartmentId(userDto.getDepartmentId() == null ? 1 : userDto.getDepartmentId());
        newUser.setIsActive(true);



        if (service.save(newUser)) {
            systemLogsService.log(newUser.getId(), userDto.getUsername(), "Register", null, "用户注册成功");
            return ResponseEntity.ok(newUser);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "注册失败"));
        }
       // return service.save(newUser) ? ResponseEntity.ok(newUser) : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "注册失败"));

    }

}