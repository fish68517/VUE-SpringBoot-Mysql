package com.graduation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * <p>
 * 系统关键操作日志表
 * </p>
 */
@Getter
@Setter
@TableName("system_logs")
public class SystemLogs implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 操作者ID
     */
    private Integer operatorId;

    /**
     * 操作者用户名
     */
    private String operatorName;

    /**
     * 动作类型
     */
    private String action;

    /**
     * 操作对象ID
     */
    private String targetId;

    /**
     * 详细描述
     */
    private String details;

    /**
     * 操作者IP
     */
    private String ipAddress;

    private LocalDateTime createdAt;
}