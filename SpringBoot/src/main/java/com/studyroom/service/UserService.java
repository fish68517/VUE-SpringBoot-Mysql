package com.studyroom.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.studyroom.dto.LoginDTO;
import com.studyroom.dto.RegisterDTO;
import com.studyroom.dto.Result;
import com.studyroom.dto.UserUpdateDTO;
import com.studyroom.entity.User;
import com.studyroom.mapper.UserMapper;
import com.studyroom.util.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;

    public UserService(UserMapper userMapper, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
    }

    public Result<Map<String, Object>> login(LoginDTO dto) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername())
        );

        if (user == null) {
            return Result.error("用户不存在");
        }

        String encryptedPassword = DigestUtils.md5DigestAsHex(dto.getPassword().getBytes());
        if (!encryptedPassword.equals(user.getPassword())) {
            return Result.error("密码错误");
        }

        if (user.getStatus() == 0) {
            return Result.error("账号已被禁用");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", toUserVO(user));

        return Result.success("登录成功", data);
    }

    public Result<String> register(RegisterDTO dto) {
        User existUser = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername())
        );

        if (existUser != null) {
            return Result.error("用户名已存在");
        }

        System.out.println("注册： " + dto.getUsername());
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(DigestUtils.md5DigestAsHex(dto.getPassword().getBytes()));
        user.setNickname(StringUtils.hasText(dto.getNickname()) ? dto.getNickname() : dto.getUsername());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setRole(0);
        user.setMonthlyStudyTime(0);
        user.setTotalStudyTime(0);
        user.setStatus(1);

        userMapper.insert(user);
        return Result.success("注册成功");
    }

    public Result<Map<String, Object>> getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(toUserVO(user));
    }

    public Result<String> updateUser(Long userId, UserUpdateDTO dto) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        if (StringUtils.hasText(dto.getNickname())) {
            user.setNickname(dto.getNickname());
        }
        if (StringUtils.hasText(dto.getPhone())) {
            user.setPhone(dto.getPhone());
        }
        if (StringUtils.hasText(dto.getEmail())) {
            user.setEmail(dto.getEmail());
        }

        // 修改密码
        if (StringUtils.hasText(dto.getOldPassword()) && StringUtils.hasText(dto.getNewPassword())) {
            String oldEncrypted = DigestUtils.md5DigestAsHex(dto.getOldPassword().getBytes());
            if (!oldEncrypted.equals(user.getPassword())) {
                return Result.error("原密码错误");
            }
            user.setPassword(DigestUtils.md5DigestAsHex(dto.getNewPassword().getBytes()));
        }

        userMapper.updateById(user);
        return Result.success("更新成功");
    }

    public Result<List<User>> listUsers() {
        List<User> users = userMapper.selectList(
                new LambdaQueryWrapper<User>().orderByDesc(User::getCreateTime)
        );
        return Result.success(users);
    }

    public Result<String> updateUserStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setStatus(status);
        userMapper.updateById(user);
        return Result.success(status == 1 ? "已启用" : "已禁用");
    }

    public void addStudyTime(Long userId, Integer minutes) {
        userMapper.addStudyTime(userId, minutes);
    }

    public void resetMonthlyStudyTime() {
        userMapper.resetMonthlyStudyTime();
    }

    private Map<String, Object> toUserVO(User user) {
        Map<String, Object> vo = new HashMap<>();
        vo.put("id", user.getId());
        vo.put("username", user.getUsername());
        vo.put("nickname", user.getNickname());
        vo.put("phone", user.getPhone());
        vo.put("email", user.getEmail());
        vo.put("role", user.getRole());
        vo.put("monthlyStudyTime", user.getMonthlyStudyTime());
        vo.put("totalStudyTime", user.getTotalStudyTime());
        vo.put("status", user.getStatus());
        vo.put("createTime", user.getCreateTime());
        return vo;
    }
}
