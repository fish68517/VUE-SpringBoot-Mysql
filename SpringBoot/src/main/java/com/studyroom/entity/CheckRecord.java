package com.studyroom.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("check_record")
public class CheckRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long reservationId;

    /**
     * 类型：1-签到，2-签退
     */
    private Integer type;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
