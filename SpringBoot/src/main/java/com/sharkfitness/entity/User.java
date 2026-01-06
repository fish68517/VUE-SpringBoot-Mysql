package com.sharkfitness.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sharkfitness.entity.enums.Role;
import com.sharkfitness.entity.enums.UserStatus;
import jakarta.persistence.*;



@Entity
@Table(name = "user")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true, length=50)
    private String username;

    @Column(name="password_hash", nullable=false, length=255)
    private String passwordHash;

    private String phone;
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private UserStatus status = UserStatus.NORMAL;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }
}
