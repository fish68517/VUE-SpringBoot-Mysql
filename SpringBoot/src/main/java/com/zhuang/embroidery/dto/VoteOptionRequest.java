package com.zhuang.embroidery.dto;

public class VoteOptionRequest {
    private String selectedOption;
    private Long userId;

    // Getters and Setters
    public String getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}