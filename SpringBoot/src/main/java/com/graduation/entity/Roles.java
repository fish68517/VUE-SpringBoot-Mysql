package com.graduation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户角色表
 * </p>
 *
 * @author 张三
 * @since 2025-10-12
 */
@Getter
@Setter
public class Roles implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 角色名称 (例如: 系统管理员, 仓库管理员)
     */
    private String roleName;
}
