package com.shenyang.musicfestival.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User entity representing system users
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_phone", columnList = "phone", unique = true),
    @Index(name = "idx_id_number", columnList = "id_number", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Column(name = "phone", nullable = false, unique = true, length = 20)
    private String phone;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname", length = 100)
    private String nickname;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "real_name", length = 100)
    private String realName;

    @Column(name = "id_number", unique = true, length = 18)
    private String idNumber;

    @Column(name = "is_real_name_verified", nullable = false)
    private Boolean isRealNameVerified = false;

    @Column(name = "points", nullable = false)
    private Long points = 0L;

    @Column(name = "is_blocked", nullable = false)
    private Boolean isBlocked = false;

    @Column(name = "role", length = 20)
    private String role = "USER";  // USER, ADMIN

}
