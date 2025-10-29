package com.graduation.dto;

public class ScanRequest {

    private String batchCode;
    private int userId;

    // 无参构造函数
    public ScanRequest() {
    }

    // 全参构造函数
    public ScanRequest(String batchCode, int userId) {
        this.batchCode = batchCode;
        this.userId = userId;
    }

    // Getters and Setters
    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}