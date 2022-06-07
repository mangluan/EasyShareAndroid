package com.easyshare.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.easyshare.base.BaseActivity;
import com.easyshare.fragment.user.SelectClassificationFragment;

/**
 * 选择分类 ： 喜好
 */
public class SelectClassificationActivity extends BaseActivity {

    @Override
    protected Fragment createFragment() {
        return SelectClassificationFragment.newInstance();
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, SelectClassificationActivity.class);
        context.startActivity(intent);
    }

}