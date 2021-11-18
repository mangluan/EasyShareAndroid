package com.easyshare.entity;

import java.util.Date;

public class PictureEntity {

    private int pictureId;
    private int albumId; // 附属图册ID
    private int userId; // 上传用户ID
    private String picurl;
    private float aspectRatio;
    private String uploadTime;
    private String description;

    @Override
    public String toString() {
        return "PictureEntity{" +
                "pictureId=" + pictureId +
                ", albumId=" + albumId +
                ", userId=" + userId +
                ", picurl='" + picurl + '\'' +
                ", aspectRatio=" + aspectRatio +
                ", uploadTime=" + uploadTime +
                ", description='" + description + '\'' +
                '}';
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPictureId() {
        return pictureId;
    }

    public void setPictureId(int pictureId) {
        this.pictureId = pictureId;
    }

    public String getPicurl() {
        return picurl;
    }

    public void setPicurl(String picurl) {
        this.picurl = picurl;
    }

    public float getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
