package com.graduation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 库存及批次表
 * </p>
 *
 * @author 张三
 * @since 2025-10-12
 */
@Getter
@Setter
public class Inventory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 关联产品表的外键
     */
    private Integer productId;

    /**
     * 批次唯一编码, 用于生成二维码
     */
    private String batchCode;

    /**
     * 此批次所属的入库单
     */
    private Integer inboundOrderId;

    /**
     * 该批次的当前库存数量
     */
    private Integer quantity;

    /**
     * 批次被实际扫码入库的时间
     */
    private LocalDateTime receivedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
