package com.easyshare.fragment.album;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.easyshare.entity.PhotoAlbumEntity;
import com.easyshare.entity.PictureEntity;

import java.util.ArrayList;
import java.util.List;

public class DetailsImageTextViewModel extends ViewModel {

    private MutableLiveData<PhotoAlbumEntity> mAlbumEntityLiveData;
    private MutableLiveData<List<PictureEntity>> mPictureListLiveData;

    public DetailsImageTextViewModel() {
        mAlbumEntityLiveData = new MutableLiveData<>();
        mPictureListLiveData = new MutableLiveData<>();
        mPictureListLiveData.setValue(new ArrayList<>());
    }

    public PhotoAlbumEntity getAlbumEntity() {
        return mAlbumEntityLiveData.getValue();
    }

    public void setAlbumEntity(PhotoAlbumEntity albumEntity) {
        mAlbumEntityLiveData.setValue(albumEntity);
    }

    public void observeAlbumEntityData(@NonNull LifecycleOwner owner, @NonNull Observer<PhotoAlbumEntity> observer) {
        mAlbumEntityLiveData.observe(owner, observer);
    }

    public List<PictureEntity> getPictureList() {
        return mPictureListLiveData.getValue();
    }

    public void setPictureList(List<PictureEntity> data) {
        mPictureListLiveData.getValue().clear();
        mPictureListLiveData.getValue().addAll(data);
        mPictureListLiveData.setValue(mPictureListLiveData.getValue());
    }

    public void observePictureListData(@NonNull LifecycleOwner owner, @NonNull Observer<List<PictureEntity>> observer) {
        mPictureListLiveData.observe(owner, observer);
    }

}