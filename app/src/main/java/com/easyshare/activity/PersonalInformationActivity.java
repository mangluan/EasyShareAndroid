package com.easyshare.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.easyshare.base.BaseActivity;
import com.easyshare.fragment.user.PersonalInformationFragment;

public class PersonalInformationActivity extends BaseActivity {

    @Override
    protected Fragment createFragment() {
        return PersonalInformationFragment.newInstance();
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, PersonalInformationActivity.class);
        context.startActivity(intent);
    }

}