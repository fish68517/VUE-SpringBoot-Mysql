package com.graduation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 入库单 (任务) 表
 * </p>
 *
 * @author 张三
 * @since 2025-10-12
 */
@Getter
@Setter
@TableName("inbound_orders")
public class InboundOrders implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 入库单的唯一编号
     */
    private String orderNumber;

    /**
     * 创建该订单的用户 (仓库管理员) [cite: 15]
     */
    private Integer createdByUserId;

    /**
     * 入库单状态
     */
    private String status;

    /**
     * 额外备注
     */
    private String notes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
