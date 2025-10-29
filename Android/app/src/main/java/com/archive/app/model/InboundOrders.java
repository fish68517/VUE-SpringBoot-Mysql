package com.archive.app.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * 对应数据库中的 inbound_orders 表
 */
public class InboundOrders implements Serializable {


    private int id;


    private String orderNumber;


    private int createdByUserId;


    private String status;


    private String notes;


    private String createdAt;


    private String updatedAt;

    // transient 关键字告诉Gson在序列化时忽略此字段
    // 我们用它在客户端临时存储批次详情
    private List<Inventory> batchDetails;

    // 为新字段添加 Getter 和 Setter
    public List<Inventory> getBatchDetails() {
        return batchDetails;
    }

    public void setBatchDetails(List<Inventory> batchDetails) {
        this.batchDetails = batchDetails;
    }


    // --- Getters and Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public int getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(int createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "InboundOrders{" +
                "id=" + id +
                ", orderNumber='" + orderNumber + '\'' +
                ", createdByUserId=" + createdByUserId +
                ", status='" + status + '\'' +
                ", notes='" + notes + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}