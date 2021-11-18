package com.easyshare.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.easyshare.base.BaseActivity;
import com.easyshare.fragment.user.FriendsFragment;

public class FriendsActivity extends BaseActivity {

    public static final String FRIENDS_TYPE = "Friends Type";

    public enum FriendsType {
        ATTENTION, FANS
    }

    @Override
    protected Fragment createFragment() {
        FriendsType friendsType = (FriendsType) getIntent().getSerializableExtra(FRIENDS_TYPE);
        return FriendsFragment.newInstance(friendsType);
    }

    public static void startActivity(Context context, FriendsType type, int count) {
        // if (count == 0) ; // 跳转到其他页面、可以是推荐关注页面、也可以合并入这个页面；目前没有此需求 TODO
        Intent intent = new Intent(context, FriendsActivity.class);
        intent.putExtra(FRIENDS_TYPE, type);
        context.startActivity(intent);
    }

}