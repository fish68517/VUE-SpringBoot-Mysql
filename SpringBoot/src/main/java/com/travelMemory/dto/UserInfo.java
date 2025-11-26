package com.travelMemory.dto;

import com.travelMemory.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

    private Long id;

    private String username;

    private String avatarUrl;

    private String bio;

    /**
     * Convert User entity to UserInfo DTO
     * @param user the user entity
     * @return UserInfo DTO
     */
    public static UserInfo from(User user) {
        return UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .build();
    }
}
