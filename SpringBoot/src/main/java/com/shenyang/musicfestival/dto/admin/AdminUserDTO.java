package com.shenyang.musicfestival.dto.admin;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class AdminUserDTO {
    private Long id;
    private String phone;
    private String nickname;
    private String realName;
    private String idNumber; // 身份证号
    private Long points;
    private String status; // active 或 banned
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginTime;
}