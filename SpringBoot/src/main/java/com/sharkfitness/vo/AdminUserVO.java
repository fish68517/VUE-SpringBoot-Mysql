package com.sharkfitness.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Extended VO for admin user listings with additional statistics
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserVO {
    
    private Long id;
    private String username;
    private String role;
    private String avatar;
    private String gender;
    private String intro;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Additional statistics for coaches
    private Long studentCount;
    private Long contentCount;
}
