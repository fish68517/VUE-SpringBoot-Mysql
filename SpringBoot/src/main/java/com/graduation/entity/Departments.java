package com.graduation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 部门信息表
 * </p>
 *
 * @author 张三
 * @since 2025-10-12
 */
@Getter
@Setter
public class Departments implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 部门名称
     */
    private String name;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
