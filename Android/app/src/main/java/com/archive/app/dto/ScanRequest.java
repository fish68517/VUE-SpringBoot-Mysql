package com.archive.app.dto;

public class ScanRequest {
    private String batchCode;
    private Integer userId; // Assuming userId is an Integer based on entity

    public ScanRequest(String batchCode, Integer userId) {
        this.batchCode = batchCode;
        this.userId = userId;
    }

    // Getters
    public String getBatchCode() {
        return batchCode;
    }

    public Integer getUserId() {
        return userId;
    }
}
