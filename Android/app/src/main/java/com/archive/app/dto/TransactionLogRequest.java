package com.archive.app.dto;

// DTO for manually creating a transaction log (if needed, less recommended for scans)
public class TransactionLogRequest {
    private Integer inventoryId;
    private Integer userId;
    private String type; // "入库", "出库", "调整"
    private Integer quantityChange;
    private Integer quantityAfterTransaction; // Backend might recalculate this
    private String notes;

    // Constructor, Getters, Setters
    public TransactionLogRequest(Integer inventoryId, Integer userId, String type, Integer quantityChange, Integer quantityAfterTransaction, String notes) {
        this.inventoryId = inventoryId;
        this.userId = userId;
        this.type = type;
        this.quantityChange = quantityChange;
        this.quantityAfterTransaction = quantityAfterTransaction;
        this.notes = notes;
    }

    // Add Getters and Setters here...
    public Integer getInventoryId() { return inventoryId; }
    public void setInventoryId(Integer inventoryId) { this.inventoryId = inventoryId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Integer getQuantityChange() { return quantityChange; }
    public void setQuantityChange(Integer quantityChange) { this.quantityChange = quantityChange; }
    public Integer getQuantityAfterTransaction() { return quantityAfterTransaction; }
    public void setQuantityAfterTransaction(Integer quantityAfterTransaction) { this.quantityAfterTransaction = quantityAfterTransaction; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
