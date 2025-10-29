package com.graduation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 库存异动日志表
 * </p>
 *
 * @author 张三
 * @since 2025-10-12
 */
@Getter
@Setter
@Entity
@TableName("transaction_logs")
public class TransactionLogs implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 如果主键是自增的
    private Long id;

    /**
     * 关联的库存批次ID
     */
    private Integer inventoryId;

    /**
     * 执行此操作的用户 (操作员) [cite: 18]
     */
    private Integer userId;

    /**
     * 异动类型
     */
    private String type;

    /**
     * 库存变化数量 (+表示入库, -表示出库)
     */
    private Integer quantityChange;

    /**
     * 本次异动后, 批次的库存数量
     */
    private Integer quantityAfterTransaction;

    /**
     * 可选备注, 例如: 盘点调整原因
     */
    private String notes;

    private LocalDateTime createdAt;
}
