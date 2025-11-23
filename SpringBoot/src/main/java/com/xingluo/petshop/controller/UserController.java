package com.xingluo.petshop.controller;

import com.xingluo.petshop.JwtUtils;
import com.xingluo.petshop.common.ApiResponse;
import com.xingluo.petshop.dto.LoginDTO;
import com.xingluo.petshop.dto.RegisterDTO;
import com.xingluo.petshop.dto.UpdateUserDTO;
import com.xingluo.petshop.dto.UserVO;
import com.xingluo.petshop.entity.User;
import com.xingluo.petshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 * 提供用户注册、登录、信息查询和更新等RESTful API
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 用户注册
     * POST /api/user/register
     */
    @PostMapping("/register")
    public ApiResponse<UserVO> register(@RequestBody RegisterDTO registerDTO) {
        // 将DTO转换为实体
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(registerDTO.getPassword());
        user.setEmail(registerDTO.getEmail());
        user.setPhone(registerDTO.getPhone());
        user.setNickname(registerDTO.getNickname());
        
        // 调用服务层注册
        User registeredUser = userService.register(user);
        
        // 转换为VO返回（不包含密码）
        UserVO userVO = convertToVO(registeredUser);
        return ApiResponse.ok(userVO);
    }

    /**
     * 用户登录
     * POST /api/user/login
     */
    @PostMapping("/login")
    public ApiResponse<UserVO> login(@RequestBody LoginDTO loginDTO) {
        // 调用服务层登录
        User user = userService.login(loginDTO.getUsername(), loginDTO.getPassword());
        // 需要添加 token

        UserVO vo = convertToVO(user);

        vo.setPassword(user.getPassword());
        // 获取token
        String token =JwtUtils.generateToken( user.getId());
        vo.setToken(token);
        return ApiResponse.ok(vo);
    }

    /**
     * 获取用户信息
     * GET /api/user/profile
     * 注意：实际项目中应该从token中获取userId，这里简化处理，通过请求参数传递
     */
    @GetMapping("/profile")
    public ApiResponse<UserVO> getProfile(@RequestParam Long userId) {
        User user = userService.getUserById(userId);
        UserVO userVO = convertToVO(user);
        return ApiResponse.ok(userVO);
    }

    /**
     * 更新用户信息
     * PUT /api/user/profile
     * 注意：实际项目中应该从token中获取userId，这里简化处理，通过请求参数传递
     */
    @PutMapping("/profile")
    public ApiResponse<UserVO> updateProfile(@RequestParam Long userId, 
                                             @RequestBody UpdateUserDTO updateUserDTO) {
        // 将DTO转换为实体
        User user = new User();
        user.setNickname(updateUserDTO.getNickname());
        user.setEmail(updateUserDTO.getEmail());
        user.setPhone(updateUserDTO.getPhone());
        user.setAvatar(updateUserDTO.getAvatar());
        user.setAddress(updateUserDTO.getAddress());
        
        // 调用服务层更新
        User updatedUser = userService.updateUser(userId, user);
        
        // 转换为VO返回
        UserVO userVO = convertToVO(updatedUser);
        return ApiResponse.ok(userVO);
    }

    /**
     * 将User实体转换为UserVO
     * 不包含密码等敏感信息
     */
    private UserVO convertToVO(User user) {
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }
}
