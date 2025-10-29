package com.graduation.dto; // 推荐放在一个 dto 包下

import com.graduation.entity.Inventory;
import com.graduation.entity.Products; // 确保你有一个 Products 实体类

public class ScanResponse {

    private Inventory inventory;
    private Products product;

    // 构造函数
    public ScanResponse(Inventory inventory, Products product) {
        this.inventory = inventory;
        this.product = product;
    }

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