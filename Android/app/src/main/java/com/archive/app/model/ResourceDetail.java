package com.archive.app.model;

import com.google.gson.annotations.SerializedName;

public class ResourceDetail {
    @SerializedName("detailId")
    private Long detailId;
    @SerializedName("learnResourceId")
    private Long learnResourceId;
    @SerializedName("contentType")
    private String contentType; // "article", "video", "image"
    @SerializedName("title")
    private String title;
    @SerializedName("contentText")
    private String contentText;
    @SerializedName("mediaFileNames")
    private String mediaFileNames; // "img1.jpg,img2.jpg"
    @SerializedName("likeCount")
    private Integer likeCount;
    @SerializedName("collectCount")
    private Integer collectCount;
    @SerializedName("createTime")
    private String createTime;

    // Getters
    public String getContentType() { return contentType; }
    public String getTitle() { return title; }
    public String getContentText() { return contentText; }
    public String getMediaFileNames() { return mediaFileNames; }
    public Integer getLikeCount() { return likeCount == null ? 0 : likeCount; }
    public Integer getCollectCount() { return collectCount == null ? 0 : collectCount; }

    public Long getDetailId() {
        return detailId;
    }

    public void setDetailId(Long detailId) {
        this.detailId = detailId;
    }

    public Long getLearnResourceId() {
        return learnResourceId;
    }

    public void setLearnResourceId(Long learnResourceId) {
        this.learnResourceId = learnResourceId;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public void setMediaFileNames(String mediaFileNames) {
        this.mediaFileNames = mediaFileNames;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public void setCollectCount(Integer collectCount) {
        this.collectCount = collectCount;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}