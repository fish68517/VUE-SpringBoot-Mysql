package com.zhuang.embroidery.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 投票记录实体类
 */
@Entity
@Table(name = "vote_record", indexes = {
    @Index(name = "idx_vote_id", columnList = "vote_id"),
    @Index(name = "idx_user_id", columnList = "user_id")
}, uniqueConstraints = {
    @UniqueConstraint(name = "unique_vote_user", columnNames = {"vote_id", "user_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteRecord {

    /**
     * 投票记录ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 投票ID
     */
    @Column(nullable = false)
    private Long voteId;

    /**
     * 用户ID
     */
    @Column(nullable = false)
    private Long userId;

    /**
     * 选择的选项
     */
    @Column(length = 255)
    private String selectedOption;

    /**
     * 投票时间
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 创建前自动设置投票时间
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
