package com.easyshare.entity;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.util.StringUtil;

import com.hjq.toast.ToastUtils;

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
    private int level;

    /**
     * 足迹、关注、粉丝
     */
    private int browsingCount;
    private int attentionCount;
    private int fansCount;

    /**
     * 是否互关好友
     */
    private int isFriend;

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

    public int getIsFriend() {
        return isFriend;
    }

    public boolean isFriend() {
        return isFriend == 1;
    }

    public void setIsFriend(int isFriend) {
        this.isFriend = isFriend;
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
        return TextUtils.isEmpty(name) ? String.format("用户%04d", userId) : name;
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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
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
                ", level='" + level + '\'' +
                ", registrationDate='" + registrationDate + '\'' +
                ", browsingCount=" + browsingCount +
                ", attentionCount=" + attentionCount +
                ", fansCount=" + fansCount +
                ", isFriend=" + (isFriend == 1) +
                '}';
    }

}
