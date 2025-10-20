package com.graduation.dto;

public class InboundOrderStatusUpdateDTO {
    // 字段名 "status" 必须与前端发送的 JSON key 一致
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
