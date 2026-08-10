package com.charging.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.charging.platform.dto.LoginRequest;
import com.charging.platform.dto.RegisterRequest;
import com.charging.platform.entity.SysUser;
import com.charging.platform.exception.BusinessException;
import com.charging.platform.mapper.SysUserMapper;
import com.charging.platform.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SysUserMapper userMapper;

    public UserVO login(LoginRequest request) {
        String username = request.getUsername().trim();
        SysUser user = findByUsername(username);
        if (user == null || !Objects.equals(user.getPassword(), request.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (!Objects.equals(user.getStatus(), 1)) {
            throw new BusinessException("当前账号已被禁用");
        }
        return toUserVO(user);
    }

    @Transactional
    public UserVO register(RegisterRequest request) {
        String username = request.getUsername().trim();
        if (findByUsername(username) != null) {
            throw new BusinessException("用户名已存在");
        }

        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(request.getPassword());
        user.setNickname(StringUtils.hasText(request.getNickname()) ? request.getNickname().trim() : username);
        user.setPhone(normalize(request.getPhone()));
        user.setEmail(normalize(request.getEmail()));
        user.setUserType(0);
        user.setStatus(1);
        userMapper.insert(user);
        return toUserVO(user);
    }

    private SysUser findByUsername(String username) {
        return userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
                .last("LIMIT 1"));
    }

    private String normalize(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private UserVO toUserVO(SysUser user) {
        return UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .phone(user.getPhone())
                .email(user.getEmail())
                .userType(user.getUserType())
                .status(user.getStatus())
                .build();
    }
}
