package com.archive.app.dto;

import com.archive.app.model.Inventory;
import com.archive.app.model.Products;
import com.google.gson.annotations.SerializedName;


// Assuming backend combines Inventory and Product for scan responses
public class ScanResponse {

    @SerializedName("inventory") // Match the JSON key from the backend
    private Inventory inventory;

    @SerializedName("product") // Match the JSON key from the backend
    private Products product;

    // Getters and Setters
    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Products getProduct() {
        return product;
    }

    public void setProduct(Products product) {
        this.product = product;
    }
}
