package com.powerinspection.entity;

import com.powerinspection.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "operation_log")
public class OperationLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String module;

    @Column(nullable = false, length = 50)
    private String action;

    @Column(length = 50)
    private String username;

    @Column(name = "real_name", length = 50)
    private String realName;

    @Column(length = 20)
    private String role;

    @Column(name = "request_method", length = 10)
    private String requestMethod;

    @Column(name = "request_path", length = 255)
    private String requestPath;

    @Column(length = 50)
    private String ip;

    @Column(nullable = false)
    private Boolean success;

    @Column(length = 255)
    private String message;

    @Column(length = 2000)
    private String details;
}
