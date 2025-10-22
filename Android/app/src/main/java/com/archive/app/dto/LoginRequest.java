package com.archive.app.dto;

public class LoginRequest {
    private String username;
    private String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters (Setters might not be needed if only used for sending)
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
