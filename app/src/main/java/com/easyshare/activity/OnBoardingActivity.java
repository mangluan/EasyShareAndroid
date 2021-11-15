package com.easyshare.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.easyshare.base.BaseActivity;
import com.easyshare.fragment.onboarding.OnBoardingParentFragment;

public class OnBoardingActivity extends BaseActivity {

    @Override
    protected Fragment createFragment() {
        return OnBoardingParentFragment.newInstance();
    }

    @Override
    public void finish() {
//        super.finish();
    }

}