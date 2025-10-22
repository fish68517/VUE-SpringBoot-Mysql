package com.archive.app.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class TransactionLogs implements Serializable {

    private static final long serialVersionUID = 1L;

    @SerializedName("id")
    private Long id; // Use Long for BIGINT

    @SerializedName("inventoryId")
    private Integer inventoryId;

    @SerializedName("userId")
    private Integer userId;

    @SerializedName("type")
    private String type; // "入库", "出库", "调整"

    @SerializedName("quantityChange")
    private Integer quantityChange;

    @SerializedName("quantityAfterTransaction")
    private Integer quantityAfterTransaction;

    @SerializedName("notes")
    private String notes;

    @SerializedName("createdAt")
    private String createdAt;

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Integer inventoryId) {
        this.inventoryId = inventoryId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getQuantityChange() {
        return quantityChange;
    }

    public void setQuantityChange(Integer quantityChange) {
        this.quantityChange = quantityChange;
    }

    public Integer getQuantityAfterTransaction() {
        return quantityAfterTransaction;
    }

    public void setQuantityAfterTransaction(Integer quantityAfterTransaction) {
        this.quantityAfterTransaction = quantityAfterTransaction;
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
}
