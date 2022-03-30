package com.easyshare.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.easyshare.base.BaseActivity;
import com.easyshare.fragment.other.ClassificationFragment;
import com.theartofdev.edmodo.cropper.CropImage;

public class ClassificationActivity extends BaseActivity {

    @Override
    protected Fragment createFragment() {
        return ClassificationFragment.newInstance();
    }

    public static void startActivityForResult(Activity activity,int requestCode) {
        Intent intent = new Intent(activity, ClassificationActivity.class);
        activity.startActivityForResult(intent,requestCode);
    }

    public static void startActivityForResult(Fragment fragment, @NonNull ActivityResultLauncher<Intent> resultLauncher) {
        Intent intent = new Intent(fragment.getContext(), ClassificationActivity.class);
        resultLauncher.launch(intent);
    }

}