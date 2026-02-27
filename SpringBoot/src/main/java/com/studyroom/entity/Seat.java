package com.studyroom.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("seat")
public class Seat {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 座位编号，如 A-01, B-15
     */
    private String seatNo;

    /**
     * 区域：A区、B区、C区、D区
     */
    private String area;

    /**
     * 状态：0-不可用，1-可用
     */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
