package com.charging.platform.controller;

import com.charging.platform.common.Result;
import com.charging.platform.dto.ProfileUpdateRequest;
import com.charging.platform.service.UserProfileService;
import com.charging.platform.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserProfileService userProfileService;

    @GetMapping("/{id}")
    public Result<UserVO> profile(@PathVariable Long id) {
        return Result.success(userProfileService.getProfile(id));
    }

    @PutMapping("/{id}")
    public Result<UserVO> updateProfile(@PathVariable Long id,
                                        @Valid @RequestBody ProfileUpdateRequest request) {
        return Result.success(userProfileService.updateProfile(id, request));
    }
}
