package com.easyshare.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "User_Info")
public class UserInfoEntity {

    @NonNull
    @PrimaryKey
    private int userId;
    private String email;
    private String name;
    private String avatarImage;
    private String sex;
    private String birthday;
    private String sign;
    private String phone;
    private String registrationDate;

    /** 足迹、关注、粉丝 */
    private int browsingCount;
    private int attentionCount;
    private int fansCount;

    public UserInfoEntity() {
    }

    public int getBrowsingCount() {
        return browsingCount;
    }

    public void setBrowsingCount(int browsingCount) {
        this.browsingCount = browsingCount;
    }

    public int getAttentionCount() {
        return attentionCount;
    }

    public void setAttentionCount(int attentionCount) {
        this.attentionCount = attentionCount;
    }

    public int getFansCount() {
        return fansCount;
    }

    public void setFansCount(int fansCount) {
        this.fansCount = fansCount;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarImage() {
        return avatarImage;
    }

    public void setAvatarImage(String avatarImage) {
        this.avatarImage = avatarImage;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    @Override
    public String toString() {
        return "UserInfoEntity{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", avatarImage='" + avatarImage + '\'' +
                ", sex='" + sex + '\'' +
                ", birthday='" + birthday + '\'' +
                ", sign='" + sign + '\'' +
                ", phone='" + phone + '\'' +
                ", registrationDate='" + registrationDate + '\'' +
                ", browsingCount=" + browsingCount +
                ", attentionCount=" + attentionCount +
                ", fansCount=" + fansCount +
                '}';
    }

}
