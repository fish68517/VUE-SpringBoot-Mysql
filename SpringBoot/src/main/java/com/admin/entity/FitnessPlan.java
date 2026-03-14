package com.admin.entity;

import java.time.LocalDateTime;

public class FitnessPlan {
    private Long id;
    private Long userId;
    private String goal;
    private String planContent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public FitnessPlan() {
    }

    public FitnessPlan(Long userId, String goal, String planContent) {
        this.userId = userId;
        this.goal = goal;
        this.planContent = planContent;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getPlanContent() {
        return planContent;
    }

    public void setPlanContent(String planContent) {
        this.planContent = planContent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "FitnessPlan{" +
                "id=" + id +
                ", userId=" + userId +
                ", goal='" + goal + '\'' +
                ", planContent='" + planContent + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
