package com.easyshare.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.easyshare.base.BaseActivity;
import com.easyshare.fragment.user.LoginRecordFragment;

/**
 * 登录记录页
 */
public class LoginRecordActivity extends BaseActivity {

    @Override
    protected Fragment createFragment() {
        return LoginRecordFragment.newInstance();
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, LoginRecordActivity.class);
        context.startActivity(intent);
    }

}