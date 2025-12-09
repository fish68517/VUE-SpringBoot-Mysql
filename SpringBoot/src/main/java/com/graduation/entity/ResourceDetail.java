package com.graduation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.Id;
import lombok.Data;
import java.sql.Timestamp;

@Data
@TableName("resource_detail")
public class ResourceDetail {
    @Id
    @TableId(type = IdType.AUTO)
    private Long detailId;
    private Long learnResourceId;
    private String contentType; // article, video, image
    private String title;
    private String contentText;
    private String mediaFileNames; // 存储文件名，如 "img1.jpg,img2.jpg"
    private Integer likeCount;
    private Integer collectCount;
    private Timestamp createTime;
}