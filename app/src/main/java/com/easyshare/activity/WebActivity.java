package com.easyshare.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.easyshare.base.BaseActivity;
import com.easyshare.fragment.web.WebFragment;

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