package com.easyshare.fragment.main;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.easyshare.entity.PhotoAlbumEntity;

import java.util.ArrayList;
import java.util.List;

public class ExploreViewModel extends ViewModel {

    private MutableLiveData<List<PhotoAlbumEntity>> mAlbumListData;
    private MutableLiveData<Integer> mPageIndexData;

    public ExploreViewModel() {
        mAlbumListData = new MutableLiveData<>();
        mAlbumListData.setValue(new ArrayList<>());
        mPageIndexData = new MutableLiveData<>();
        mPageIndexData.setValue(1);
    }

    public List<PhotoAlbumEntity> getAlbumListData() {
        return mAlbumListData.getValue();
    }

    public void setAlbumListData(List<PhotoAlbumEntity> albumListData) {
        mAlbumListData.getValue().clear();
        addAlbumListData(albumListData);
    }

    public void addAlbumListData(List<PhotoAlbumEntity> albumListData) {
        mAlbumListData.getValue().addAll(albumListData);
        mAlbumListData.setValue(mAlbumListData.getValue());
    }

    public void observeAlbumListData(@NonNull LifecycleOwner owner, @NonNull Observer<List<PhotoAlbumEntity>> observer) {
        mAlbumListData.observe(owner, observer);
    }

    public void addSelfPage() {
        int pageIndex = mPageIndexData.getValue();
        mPageIndexData.setValue(pageIndex + 1);
    }

    public int getPageIndex() {
        return mPageIndexData.getValue();
    }

    public void makeZeroPage() {
        mPageIndexData.setValue(1);
    }

    public void observePageIndexData(@NonNull LifecycleOwner owner, @NonNull Observer<Integer> observer) {
        mPageIndexData.observe(owner, observer);
    }

}