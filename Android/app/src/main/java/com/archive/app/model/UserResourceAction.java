package com.archive.app.model;

import com.google.gson.annotations.SerializedName;

public class UserResourceAction {
    @SerializedName("actionId")
    private Long actionId;
    @SerializedName("campusUserId")
    private Long campusUserId;
    @SerializedName("learnResourceId")
    private Long learnResourceId;
    @SerializedName("isLiked")
    private Boolean isLiked;
    @SerializedName("isCollected")
    private Boolean isCollected;

    public Boolean getIsLiked() { return isLiked != null && isLiked; }
    public Boolean getIsCollected() { return isCollected != null && isCollected; }
    // Setters if needed


    public Long getActionId() {
        return actionId;
    }

    public void setActionId(Long actionId) {
        this.actionId = actionId;
    }

    public Long getCampusUserId() {
        return campusUserId;
    }

    public void setCampusUserId(Long campusUserId) {
        this.campusUserId = campusUserId;
    }

    public Long getLearnResourceId() {
        return learnResourceId;
    }

    public void setLearnResourceId(Long learnResourceId) {
        this.learnResourceId = learnResourceId;
    }

    public Boolean getLiked() {
        return isLiked;
    }

    public void setLiked(Boolean liked) {
        isLiked = liked;
    }

    public Boolean getCollected() {
        return isCollected;
    }

    public void setCollected(Boolean collected) {
        isCollected = collected;
    }
}