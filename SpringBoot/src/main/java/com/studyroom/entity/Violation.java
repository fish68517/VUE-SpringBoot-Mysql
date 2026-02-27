package com.studyroom.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("violation")
public class Violation {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long reservationId;

    /**
     * 违规类型：1-预约未到，2-超时未签退，3-恶意取消
     */
    private Integer type;

    private String description;

    private Integer points;

    /**
     * 状态：0-未处理，1-已处理，2-已申诉
     */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
