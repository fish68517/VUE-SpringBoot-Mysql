package com.charging.platform.service;

import com.charging.platform.dto.ProfileUpdateRequest;
import com.charging.platform.entity.SysUser;
import com.charging.platform.exception.BusinessException;
import com.charging.platform.mapper.SysUserMapper;
import com.charging.platform.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final SysUserMapper userMapper;

    public UserVO getProfile(Long id) {
        return toVO(requireUser(id));
    }

    @Transactional
    public UserVO updateProfile(Long id, ProfileUpdateRequest request) {
        SysUser user = requireUser(id);
        user.setNickname(request.getNickname().trim());
        user.setPhone(normalize(request.getPhone()));
        user.setEmail(normalize(request.getEmail()));
        userMapper.updateById(user);
        return toVO(userMapper.selectById(id));
    }

    private SysUser requireUser(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return user;
    }

    private String normalize(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private UserVO toVO(SysUser user) {
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
