package com.easyshare.fragment.album;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easyshare.R;
import com.easyshare.base.BaseFragment;

public class PublishPhotoAlbumFragment extends BaseFragment {

    private PublishPhotoAlbumViewModel mViewModel;

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

}