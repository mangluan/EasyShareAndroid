package com.easyshare.entity;

import java.util.Date;

public class LoginRecordEntity {

    private String loginTime;
    private String source;

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "LoginRecordEntity{" +
                "loginTime='" + loginTime + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}
