package com.charging.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("guide_point")
public class GuidePoint {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long stationId;
    private String pointName;
    private Integer pointType;
    private BigDecimal xRatio;
    private BigDecimal yRatio;
    private String description;
    private Integer sortNo;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
