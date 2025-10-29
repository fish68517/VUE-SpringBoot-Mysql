package com.archive.app.model;

import com.google.gson.annotations.SerializedName;

/**
 * 对应数据库中的 roles 表
 */
public class Roles {


    private int id;


    private String roleName;

    // --- Getters and Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String toString() {
        return "Roles{" +
                "id=" + id +
                ", roleName='" + roleName + '\'' +
                '}';
    }
}