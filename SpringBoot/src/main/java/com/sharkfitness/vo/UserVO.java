package com.sharkfitness.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * VO for user profile information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {
    
    private Long id;
    private String username;
    private String role;
    private String avatar;
    private String gender;
    private String intro;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
