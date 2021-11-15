package com.easyshare.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.easyshare.base.BaseActivity;
import com.easyshare.fragment.setting.AboutSoftwareFragment;

public class AboutSoftwareActivity extends BaseActivity {

    @Override
    protected Fragment createFragment() {
        return AboutSoftwareFragment.newInstance();
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, AboutSoftwareActivity.class);
        context.startActivity(intent);
    }

}