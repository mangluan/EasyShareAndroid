package com.easyshare.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.easyshare.base.BaseActivity;
import com.easyshare.fragment.album.PublishPhotoAlbumFragment;
import com.easyshare.fragment.setting.AboutSoftwareFragment;

public class PublishPhotoAlbumActivity extends BaseActivity {

    @Override
    protected Fragment createFragment() {
        return PublishPhotoAlbumFragment.newInstance();
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, PublishPhotoAlbumActivity.class);
        context.startActivity(intent);
    }

}