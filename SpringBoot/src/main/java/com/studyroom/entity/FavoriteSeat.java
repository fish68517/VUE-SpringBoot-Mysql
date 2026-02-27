package com.studyroom.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("favorite_seat")
public class FavoriteSeat {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long seatId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
