package com.campus.dto;

import com.campus.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * User Data Transfer Object
 * Used for transferring user data between layers
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {

    /**
     * User ID
     */
    private Long id;

    /**
     * Username (student ID)
     */
    private String username;

    /**
     * Email address
     */
    private String email;

    /**
     * Phone number
     */
    private String phone;

    /**
     * Nickname
     */
    private String nickname;

    /**
     * Avatar URL
     */
    private String avatarUrl;

    /**
     * Gender
     */
    private String gender;

    /**
     * Birthday
     */
    private java.time.LocalDate birthday;

    /**
     * User role
     */
    private User.UserRole role;

    /**
     * Account status
     */
    private User.AccountStatus status;

    /**
     * Creation timestamp
     */
    private LocalDateTime createdAt;

    /**
     * Last update timestamp
     */
    private LocalDateTime updatedAt;

    /**
     * Convert entity to DTO
     */
    public static UserDTO fromEntity(User user) {
        if (user == null) {
            return null;
        }
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .gender(user.getGender())
                .birthday(user.getBirthday())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /**
     * Convert DTO to entity
     */
    public User toEntity() {
        User user = User.builder()
                .username(this.username)
                .email(this.email)
                .phone(this.phone)
                .nickname(this.nickname)
                .avatarUrl(this.avatarUrl)
                .gender(this.gender)
                .birthday(this.birthday)
                .role(this.role)
                .status(this.status)
                .build();
        user.setId(this.id);
        return user;
    }

}
