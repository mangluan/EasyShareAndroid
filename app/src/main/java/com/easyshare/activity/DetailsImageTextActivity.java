package com.easyshare.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.easyshare.base.BaseActivity;
import com.easyshare.entity.PhotoAlbumEntity;
import com.easyshare.fragment.album.DetailsImageTextFragment;
import com.easyshare.fragment.album.PublishImageTextFragment;

public class DetailsImageTextActivity extends BaseActivity {

    @Override
    protected Fragment createFragment() {
        return DetailsImageTextFragment.newInstance();
    }

    public static void startActivity(Context context, PhotoAlbumEntity entity) {
        Intent intent = new Intent(context, DetailsImageTextActivity.class);
        intent.putExtra("entity",entity);
        context.startActivity(intent);
    }

}