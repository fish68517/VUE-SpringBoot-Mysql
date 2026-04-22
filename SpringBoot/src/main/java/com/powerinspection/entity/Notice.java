package com.powerinspection.entity;

import com.powerinspection.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "notice")
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(nullable = false)
    private Boolean pinned = Boolean.FALSE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NoticeStatus status = NoticeStatus.ACTIVE;

    @Column(name = "publish_time", nullable = false)
    private LocalDateTime publishTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "publisher_id", nullable = false)
    private User publisher;
}
