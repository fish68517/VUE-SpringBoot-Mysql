package com.graduation.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.graduation.dto.LoginRequest;
import com.graduation.dto.RegisterDto;
import com.graduation.entity.Users;
import com.graduation.service.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
            return ResponseEntity.ok(user);
        } else {
            // 密码错误
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "用户名或密码错误"));
        }
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
        return service.save(newUser) ? ResponseEntity.ok(newUser) : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "注册失败"));

    }

}