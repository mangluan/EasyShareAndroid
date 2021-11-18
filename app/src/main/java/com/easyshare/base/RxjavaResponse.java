package com.easyshare.base;

import android.content.Context;

import com.easyshare.activity.LoginActivity;
import com.easyshare.network.Constants;
import com.easyshare.utils.SharedPreferenceUtils;
import com.easyshare.utils.UserUtils;

public class RxjavaResponse {

    /**
     * 只清除用户信息
     */
    public static <T> void authentication(BaseDataBean<T> dataBean) throws Exception {
        if (dataBean.getCode() == -1) { // 登录过期
            UserUtils.getsInstance().logout();
            throw new BaseException(dataBean.getMsg());
        }
    }

    /**
     * 跳转到登录页面 （不能清除token ）
     */
    public static <T> void authentication(BaseDataBean<T> dataBean, Context context) throws Exception {
        if (dataBean.getCode() == -1) { // 登录过期
            LoginActivity.startActivity(context);
            UserUtils.getsInstance().logout();
            throw new BaseException(dataBean.getMsg());
        }
    }

}
