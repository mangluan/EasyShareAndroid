package com.easyshare.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.easyshare.base.BaseActivity;
import com.easyshare.fragment.album.PublishImageTextFragment;
import com.easyshare.fragment.setting.AboutSoftwareFragment;

public class PublishImageTextActivity extends BaseActivity {

    @Override
    protected Fragment createFragment() {
        return PublishImageTextFragment.newInstance();
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, PublishImageTextActivity.class);
        context.startActivity(intent);
    }

}