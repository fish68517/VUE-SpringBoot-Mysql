package com.charging.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_feedback")
public class UserFeedback {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Integer feedbackType;
    private String title;
    private String content;
    private String contact;
    private Integer processStatus;
    private String replyContent;
    private LocalDateTime createTime;
    private LocalDateTime processTime;
    private LocalDateTime updateTime;
}
