package com.easyshare.base;

import android.app.Application;
import android.text.TextUtils;
import android.view.Gravity;

import com.easyshare.R;
import com.easyshare.network.Constants;
import com.easyshare.network.RetrofitFactory;
import com.easyshare.utils.OSSUtils;
import com.easyshare.utils.SharedPreferenceUtils;
import com.easyshare.utils.UserUtils;
import com.hjq.toast.ToastUtils;
import com.hjq.toast.style.ToastBlackStyle;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.upgrade.UpgradeStateListener;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
        // oss
        OSSUtils.initOOS(this);
        // 自动登录
        String token = SharedPreferenceUtils.getString(this, Constants.TOKEN);
        if (!TextUtils.isEmpty(token)) { // 可以尝试登录
            Disposable subscribe = RetrofitFactory.getsInstance(this)
                    .loginForToken()
                    .subscribeOn(Schedulers.io()) // 子线程执行方法
                    .observeOn(AndroidSchedulers.mainThread()) // 主线程回调
                    .subscribe(resp -> {   // 成功回调
                        if (resp.getCode() == 0) {
                            UserUtils.getsInstance().login(resp.getData());
                        }else {
                            ToastUtils.show(getString(R.string.login_overdue));
                            SharedPreferenceUtils.putString(this, Constants.TOKEN, "");
                        }
                    });
        }
    }

}
