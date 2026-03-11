package com.shenyang.musicfestival.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "checkin_tasks")
@Data
public class CheckinTask extends BaseEntity {
    private String name;
    private String description;
    private String coverImage;
    private Integer points;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer radius;
    // 加上这两个注解，告诉 Spring Boot 接受带空格的时间格式，并设置东八区时区
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;
    private String status;
}