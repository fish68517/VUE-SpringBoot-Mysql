package com.submission.mapper;

import com.submission.entity.User;
import org.apache.ibatis.annotations.*;

/**
 * User Mapper - Data access layer for User entity
 */
@Mapper
public interface UserMapper {

    /**
     * Insert a new user
     */
    @Insert("INSERT INTO users (username, password, email, phone, role, status, academic_achievements, " +
            "work_email, expertise_areas, research_directions, created_at, updated_at) " +
            "VALUES (#{username}, #{password}, #{email}, #{phone}, #{role}, #{status}, " +
            "#{academicAchievements}, #{workEmail}, #{expertiseAreas}, #{researchDirections}, " +
            "NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    /**
     * Find user by username
     */
    @Select("SELECT id, username, password, email, phone, role, status, academic_achievements, " +
            "work_email, expertise_areas, research_directions, created_at, updated_at " +
            "FROM users WHERE username = #{username}")
    User findByUsername(String username);

    /**
     * Find user by id
     */
    @Select("SELECT id, username, password, email, phone, role, status, academic_achievements, " +
            "work_email, expertise_areas, research_directions, created_at, updated_at " +
            "FROM users WHERE id = #{id}")
    User findById(Long id);

    /**
     * Update user
     */
    @Update("UPDATE users SET password = #{password}, email = #{email}, phone = #{phone}, " +
            "role = #{role}, status = #{status}, academic_achievements = #{academicAchievements}, " +
            "work_email = #{workEmail}, expertise_areas = #{expertiseAreas}, " +
            "research_directions = #{researchDirections}, updated_at = NOW() " +
            "WHERE id = #{id}")
    int update(User user);

    /**
     * Find all active reviewers
     */
    @Select("SELECT id, username, password, email, phone, role, status, academic_achievements, " +
            "work_email, expertise_areas, research_directions, created_at, updated_at " +
            "FROM users WHERE role = 'REVIEWER' AND status = 'ACTIVE' ORDER BY username")
    java.util.List<User> findAllActiveReviewers();

    /**
     * Find users by role
     */
    @Select("SELECT id, username, password, email, phone, role, status, academic_achievements, " +
            "work_email, expertise_areas, research_directions, created_at, updated_at " +
            "FROM users WHERE role = #{role} AND status = 'ACTIVE' ORDER BY username")
    java.util.List<User> findByRole(String role);

    /**
     * Find all pending users (awaiting approval)
     */
    @Select("SELECT id, username, password, email, phone, role, status, academic_achievements, " +
            "work_email, expertise_areas, research_directions, created_at, updated_at " +
            "FROM users WHERE status = 'PENDING' ORDER BY created_at DESC")
    java.util.List<User> findPendingUsers();

    /**
     * Find all users with optional filtering
     */
    @Select("<script>" +
            "SELECT id, username, password, email, phone, role, status, academic_achievements, " +
            "work_email, expertise_areas, research_directions, created_at, updated_at " +
            "FROM users WHERE 1=1 " +
            "<if test='username != null and username != \"\"'> AND username LIKE CONCAT('%', #{username}, '%')</if> " +
            "<if test='email != null and email != \"\"'> AND email LIKE CONCAT('%', #{email}, '%')</if> " +
            "<if test='role != null and role != \"\"'> AND role = #{role}</if> " +
            "<if test='status != null and status != \"\"'> AND status = #{status}</if> " +
            "ORDER BY created_at DESC " +
            "</script>")
    java.util.List<User> findAllUsers(@org.apache.ibatis.annotations.Param("username") String username,
                                       @org.apache.ibatis.annotations.Param("email") String email,
                                       @org.apache.ibatis.annotations.Param("role") String role,
                                       @org.apache.ibatis.annotations.Param("status") String status);

    /**
     * Delete user by id
     */
    @Delete("DELETE FROM users WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * Update user status
     */
    @Update("UPDATE users SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int updateStatus(@org.apache.ibatis.annotations.Param("id") Long id,
                     @org.apache.ibatis.annotations.Param("status") String status);

    /**
     * Count users by role
     */
    @Select("SELECT COUNT(*) FROM users WHERE role = #{role}")
    long countUsersByRole(String role);

    /**
     * Count users by status
     */
    @Select("SELECT COUNT(*) FROM users WHERE status = #{status}")
    long countUsersByStatus(String status);
}
