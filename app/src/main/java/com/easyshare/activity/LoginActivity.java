package com.easyshare.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.easyshare.R;
import com.easyshare.base.BaseActivity;
import com.easyshare.fragment.user.LoginFragment;
import com.lxj.xpopup.XPopup;

/**
 * 登录注册页
 */
public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Fragment createFragment() {
        return LoginFragment.newInstance();
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

}