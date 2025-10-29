package com.archive.app.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Inventory implements Serializable {

    private static final long serialVersionUID = 1L;


    private Integer id;


    private Integer productId;


    private String batchCode;


    private Integer inboundOrderId;


    private Integer quantity;


    private String receivedAt; // Actual inbound scan time


    private String createdAt;


    private String updatedAt;

    // --- Getters and Setters ---

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public Integer getInboundOrderId() {
        return inboundOrderId;
    }

    public void setInboundOrderId(Integer inboundOrderId) {
        this.inboundOrderId = inboundOrderId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(String receivedAt) {
        this.receivedAt = receivedAt;
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
        return "Inventory{" +
                "id=" + id +
                ", productId=" + productId +
                ", batchCode='" + batchCode + '\'' +
                ", inboundOrderId=" + inboundOrderId +
                ", quantity=" + quantity +
                ", receivedAt='" + receivedAt + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
