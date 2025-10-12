package com.graduation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 系统用户账户表
 * </p>
 *
 * @author 张三
 * @since 2025-10-12
 */
@Getter
@Setter
public class Users implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 登录用户名
     */
    private String username;

    /**
     * 加密后的密码
     */
    private String passwordHash;

    /**
     * 用户全名
     */
    private String fullName;

    /**
     * 关联角色表的外键
     */
    private Integer roleId;

    /**
     * 关联部门表的外键
     */
    private Integer departmentId;

    /**
     * 标记用户账户是否启用
     */
    private Boolean isActive;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
