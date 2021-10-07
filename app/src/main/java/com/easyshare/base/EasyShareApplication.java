package com.easyshare.base;

import android.app.Application;
import android.view.Gravity;

import com.hjq.toast.ToastUtils;
import com.hjq.toast.style.ToastBlackStyle;
import com.tencent.bugly.Bugly;

public class EasyShareApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化 Bugly
        Bugly.init(getApplicationContext(), "424e29f88b", false);
        // 初始化 ToastUtils
        ToastUtils.init(this);
        ToastUtils.initStyle(new ToastBlackStyle(this) {
            @Override
            public int getCornerRadius() {
                return dp2px(10);
            }

            @Override
            public int getPaddingStart() {
                return dp2px(16);
            }

            @Override
            public int getPaddingTop() {
                return dp2px(10);
            }
        });
        ToastUtils.setGravity(Gravity.BOTTOM, 0, 300);
    }

}
