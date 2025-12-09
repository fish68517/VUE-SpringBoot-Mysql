package com.graduation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 产品主数据表
 * </p>
 *
 * @author 张三
 * @since 2025-10-12
 */
@Getter
@Setter
@Entity
@TableName("Products")
public class Products implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 如果主键是自增的
    private Integer id;

    /**
     * SKU (Stock Keeping Unit), 产品的唯一标识符
     */
    private String sku;

    /**
     * 产品名称
     */
    private String name;

    /**
     * 产品详细描述
     */
    private String description;


    //  默认情况下 Jackson 只认识 ISO-8601 标准格式，例如： "2025-12-09T02:45:09"
    // 而 Spring Boot 默认的 Jackson

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updatedAt;

    private Integer createdBy;

}
