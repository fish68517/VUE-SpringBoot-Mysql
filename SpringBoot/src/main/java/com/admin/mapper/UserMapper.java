package com.admin.mapper;

import com.admin.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    /**
     * Find user by id
     */
    User findById(@Param("id") Long id);

    /**
     * Find user by username
     */
    User findByUsername(@Param("username") String username);

    /**
     * Find all users with pagination and search
     */
    List<User> findAll(@Param("offset") int offset, @Param("limit") int limit,
                       @Param("searchKeyword") String searchKeyword);

    /**
     * Count total users with search filter
     */
    int countAll(@Param("searchKeyword") String searchKeyword);

    /**
     * Insert new user
     */
    int insert(User user);

    /**
     * Update user information
     */
    int update(User user);

    /**
     * Delete user by id
     */
    int deleteById(@Param("id") Long id);
}