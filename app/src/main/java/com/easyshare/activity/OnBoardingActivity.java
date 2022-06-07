package com.easyshare.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.easyshare.base.BaseActivity;
import com.easyshare.fragment.onboarding.OnBoardingParentFragment;

public class OnBoardingActivity extends BaseActivity {

    @Override
    protected Fragment createFragment() {
        return OnBoardingParentFragment.newInstance();
    }


    public static void startActivity(Context context) {
        Intent intent = new Intent(context, OnBoardingActivity.class);
        context.startActivity(intent);
    }

}