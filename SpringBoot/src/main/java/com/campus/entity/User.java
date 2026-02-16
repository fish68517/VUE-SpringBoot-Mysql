package com.campus.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * User Entity
 * Represents a user in the system
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_username", columnList = "username"),
        @Index(name = "idx_email", columnList = "email")
})
public class User extends BaseEntity {

    /**
     * Username (student ID)
     */
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    /**
     * Password (encrypted)
     */
    @Column(nullable = false, length = 255)
    private String password;

    /**
     * Email address
     */
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    /**
     * Phone number
     */
    @Column(length = 20)
    private String phone;

    /**
     * Nickname
     */
    @Column(length = 50)
    private String nickname;

    /**
     * Avatar URL
     */
    @Column(length = 255)
    private String avatarUrl;

    /**
     * Gender
     */
    @Column(length = 20)
    private String gender;

    /**
     * Birthday
     */
    @Column
    private java.time.LocalDate birthday;

    /**
     * User role
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    /**
     * Account status
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    /**
     * Activities organized by this user (one-to-many relationship)
     */
    @OneToMany(mappedBy = "organizerId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Activity> organizedActivities;

    /**
     * Registrations by this user (one-to-many relationship)
     */
    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Registration> registrations;

    /**
     * Crowdfunding supports by this user (one-to-many relationship)
     */
    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CrowdfundingSupport> crowdfundingSupports;

    /**
     * Feedback provided by this user (one-to-many relationship)
     */
    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Feedback> feedbacks;

    /**
     * Audit logs created by this user (one-to-many relationship)
     */
    @OneToMany(mappedBy = "auditorId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AuditLog> auditLogs;

    /**
     * User role enumeration
     */
    public enum UserRole {
        USER,           // Regular user
        ORGANIZER,      // Activity organizer
        ADMIN           // Administrator
    }

    /**
     * Account status enumeration
     */
    public enum AccountStatus {
        ACTIVE,         // Active account
        DISABLED        // Disabled account
    }

}

