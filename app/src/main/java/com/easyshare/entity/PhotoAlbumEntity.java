package com.easyshare.entity;

import java.util.Date;
import java.util.List;

public class PhotoAlbumEntity {

    private int albumId;
    private int classificationId;
    private String title;
    private String content;
    private int type;
    private String time;

    /**
     * 发布图册用户
     */
    private UserInfoEntity userEntity;

    /**
     * 评论、点赞数量
     */
    private int commentCount;
    private int likeCount;

    /**
     * 是否点赞
     */
    private int isLike;

    /**
     * 图片列表
     */
    private List<PictureEntity> pictureEntities;

    @Override
    public String toString() {
        return "PhotoAlbumEntity{" +
                "albumId=" + albumId +
                ", classificationId=" + classificationId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", type=" + type +
                ", time=" + time +
                ", mUserEntity=" + userEntity +
                ", commentCount=" + commentCount +
                ", likeCount=" + likeCount +
                ", isLike=" + isLike +
                ", mPictureEntities=" + pictureEntities +
                '}';
    }

    public UserInfoEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserInfoEntity userEntity) {
        this.userEntity = userEntity;
    }

    public int getIsLike() {
        return isLike;
    }

    public void setIsLike(int isLike) {
        this.isLike = isLike;
    }

    public List<PictureEntity> getPictureEntities() {
        return pictureEntities;
    }

    public void setPictureEntities(List<PictureEntity> pictureEntities) {
        this.pictureEntities = pictureEntities;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public boolean isLike() {
        return isLike == 1;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public int getClassificationId() {
        return classificationId;
    }

    public void setClassificationId(int classificationId) {
        this.classificationId = classificationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
