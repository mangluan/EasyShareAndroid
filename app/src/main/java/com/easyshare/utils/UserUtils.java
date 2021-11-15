package com.easyshare.utils;

import com.easyshare.base.BaseDataBean;
import com.easyshare.entity.UserInfoEntity;
import com.easyshare.network.Constants;

import org.greenrobot.eventbus.EventBus;

public class UserUtils {

    private static volatile UserUtils sInstance;
    private volatile UserInfoEntity mLoginUser;

    private UserUtils() {
    }

    /**
     * 单利管理登录用户
     */
    public static UserUtils getsInstance() {
        if (sInstance == null) {
            synchronized (UserUtils.class) {
                if (sInstance == null) {
                    sInstance = new UserUtils();
                }
            }
        }
        return sInstance;
    }

    /**
     * 获取登录用户信息
     */
    public UserInfoEntity getUserInfo() {
        return mLoginUser;
    }

    /**
     * 登录
     */
    public void login(UserInfoEntity loginUser) {
        mLoginUser = loginUser;
        // 发送登录成功通知
        EventBus.getDefault().post(BaseDataBean.build(Constants.LOGIN_SUCCESSFULLY, loginUser));
    }

    /**
     * 退出登录
     */
    public void logout() {
        mLoginUser = null;
        // 发送退出登录通知
        EventBus.getDefault().post(BaseDataBean.build(Constants.LOGOUT));
    }

    /**
     * 判断是否已经登录
     */
    public boolean isLogin() {
        return mLoginUser != null;
    }


}
