package com.sharkfitness.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class BaseEntity {

    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createTime = now;
        this.updateTime = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    public LocalDateTime getCreateTime() { return createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
}
