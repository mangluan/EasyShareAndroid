package com.easyshare.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.easyshare.base.BaseActivity;
import com.easyshare.fragment.user.ImproveUserDataFragment;

/**
 * 注册后初始化基本用户信息页
 */
public class ImproveUserDataActivity extends BaseActivity {

    @Override
    protected Fragment createFragment() {
        return ImproveUserDataFragment.newInstance();
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, ImproveUserDataActivity.class);
        context.startActivity(intent);
    }

}