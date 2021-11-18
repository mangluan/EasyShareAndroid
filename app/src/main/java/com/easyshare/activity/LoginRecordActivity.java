package com.easyshare.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.easyshare.base.BaseActivity;
import com.easyshare.fragment.user.LoginRecordFragment;

public class LoginRecordActivity extends BaseActivity {

    @Override
    protected Fragment createFragment() {
        return LoginRecordFragment.newInstance();
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, LoginRecordActivity.class);
        context.startActivity(intent);
    }

}