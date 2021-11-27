package com.easyshare.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.easyshare.base.BaseActivity;
import com.easyshare.fragment.user.ChangePasswordFragment;

public class ChangePasswordActivity extends BaseActivity {

    @Override
    protected Fragment createFragment() {
        return ChangePasswordFragment.newInstance();
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, ChangePasswordActivity.class);
        context.startActivity(intent);
    }

}