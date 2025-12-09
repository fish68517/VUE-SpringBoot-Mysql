package com.graduation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.sql.Timestamp;

@Data
@TableName("user_resource_action")
public class UserResourceAction {
    @TableId(type = IdType.AUTO)
    private Long actionId;
    private Long campusUserId;
    private Long learnResourceId;
    private Boolean isLiked;      // true: 已点赞
    private Boolean isCollected;  // true: 已收藏
    private Timestamp actionTime;
}