package com.admin.mapper;

import com.admin.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminMapper {
    
    /**
     * Find admin by username
     */
    Admin findByUsername(@Param("username") String username);
    
    /**
     * Find admin by id
     */
    Admin findById(@Param("id") Long id);
    
    /**
     * Insert admin
     */
    int insert(Admin admin);
}
