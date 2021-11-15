package com.easyshare.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.easyshare.base.BaseActivity;
import com.easyshare.fragment.user.AccountSecurityFragment;

public class AccountSecurityActivity extends BaseActivity {

    @Override
    protected Fragment createFragment() {
        return AccountSecurityFragment.newInstance();
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, AccountSecurityActivity.class);
        context.startActivity(intent);
    }

}