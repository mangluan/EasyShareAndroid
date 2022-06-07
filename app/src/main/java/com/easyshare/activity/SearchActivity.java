package com.easyshare.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.easyshare.base.BaseActivity;
import com.easyshare.fragment.album.SearchFragment;
import com.easyshare.fragment.setting.AboutSoftwareFragment;

public class SearchActivity extends BaseActivity {

    @Override
    protected Fragment createFragment() {
        return SearchFragment.newInstance();
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }

}