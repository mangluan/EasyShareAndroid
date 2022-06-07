package com.easyshare.fragment.album;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.easyshare.R;
import com.easyshare.base.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class PublishPhotoAlbumFragment extends BaseFragment {

    private PublishPhotoAlbumViewModel mViewModel;

    @BindView(R.id.image)
    ImageView mImageView;

    public static PublishPhotoAlbumFragment newInstance() {
        return new PublishPhotoAlbumFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_publish_photo_album, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(PublishPhotoAlbumViewModel.class);
        // TODO: Use the ViewModel
    }


    /**
     * 点击图片
     */
    @OnClick(R.id.image)
    public void onImageClick(ImageView mImageView) {
//        Glide.with(this).load(R.drawable.test)
//                .transition(withCrossFade()).into(mImageView);
    }

    /**
     * 加载发布按钮
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_publish_nav_bottom, menu);
    }

    /**
     * 点击发布按钮 -- 标题
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (R.id.nav_publish == item.getItemId()) {
            onPublishClick();
            return true;
        } else return super.onOptionsItemSelected(item);
    }

    private void onPublishClick() {
    }


}