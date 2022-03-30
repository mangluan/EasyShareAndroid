package com.easyshare.activity;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;

import com.easyshare.base.BaseActivity;
import com.easyshare.fragment.other.WebFragment;

public class WebActivity extends BaseActivity {

    private static final String WEB_URL = "web url";

    @Override
    protected Fragment createFragment() {
        return WebFragment.newInstance(getIntent().getStringExtra(WEB_URL));
    }

    public static void startActivity(Context context ,String webURL) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(WEB_URL,webURL);
        context.startActivity(intent);
    }

}