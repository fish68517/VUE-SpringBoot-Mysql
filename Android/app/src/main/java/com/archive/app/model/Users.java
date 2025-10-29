package com.archive.app.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.time.LocalDateTime; // Use appropriate date/time class if needed

// Make sure your entity classes match the JSON structure returned by Spring Boot
// Use @SerializedName if JSON keys differ from field names (e.g., snake_case vs camelCase)
public class Users implements Serializable {

    private static final long serialVersionUID = 1L;

    @SerializedName("id") // Ensure this matches JSON key from backend
    private Integer id;

    @SerializedName("username")
    private String username;

     @SerializedName("passwordHash")
     private String passwordHash;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("roleId")
    private Integer roleId;

    @SerializedName("departmentId")
    private Integer departmentId;

    @SerializedName("isActive")
    private Boolean isActive;

    // Dates might be Strings or require custom deserialization depending on backend format
    @SerializedName("createdAt")
    private String createdAt; // Use String for simplicity with Retrofit/Gson defaults

    @SerializedName("updatedAt")
    private String updatedAt;

    // --- Getters and Setters ---
    public Integer getId() {
        return id;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
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
}
