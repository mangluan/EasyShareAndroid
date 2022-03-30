package com.easyshare.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.easyshare.base.BaseActivity;
import com.easyshare.fragment.album.LikeRecordFragment;

/**
 * 点赞记录页
 */
public class LikeRecordActivity extends BaseActivity {

    @Override
    protected Fragment createFragment() {
        return LikeRecordFragment.newInstance();
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, LikeRecordActivity.class);
        context.startActivity(intent);
    }

}