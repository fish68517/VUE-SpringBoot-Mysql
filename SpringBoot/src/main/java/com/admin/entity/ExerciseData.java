package com.admin.entity;

import java.time.LocalDateTime;

public class ExerciseData {
    private Long id;
    private Long userId;
    private String exerciseType;
    private String location;
    private Integer duration;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ExerciseData() {
    }

    public ExerciseData(Long userId, String exerciseType, String location, Integer duration) {
        this.userId = userId;
        this.exerciseType = exerciseType;
        this.location = location;
        this.duration = duration;
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

    public String getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
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
        return "ExerciseData{" +
                "id=" + id +
                ", userId=" + userId +
                ", exerciseType='" + exerciseType + '\'' +
                ", location='" + location + '\'' +
                ", duration=" + duration +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
