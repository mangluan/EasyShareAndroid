package com.easyshare.fragment.main;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.easyshare.entity.PhotoAlbumEntity;
import com.easyshare.entity.UserInfoEntity;

import java.util.ArrayList;
import java.util.List;

public class HomepageViewModel extends ViewModel {

    private MutableLiveData<List<UserInfoEntity>> mUserRecommendListData;

    private MutableLiveData<List<PhotoAlbumEntity>> mAlbumListData;
    private MutableLiveData<Integer> mPageIndexData;

    public HomepageViewModel() {
        mUserRecommendListData = new MutableLiveData<>();
        mUserRecommendListData.setValue(new ArrayList<>());
        mAlbumListData = new MutableLiveData<>();
        mAlbumListData.setValue(new ArrayList<>());
        mPageIndexData = new MutableLiveData<>();
        mPageIndexData.setValue(1);
    }

    public List<UserInfoEntity> getUserRecommendListData() {
        return mUserRecommendListData.getValue();

    }

    public void setUserRecommendListData(List<UserInfoEntity> userRecommend) {
        mUserRecommendListData.getValue().clear();
        mUserRecommendListData.getValue().addAll(userRecommend);
        mUserRecommendListData.setValue(mUserRecommendListData.getValue());
    }

    public void observeUserRecommendListData(@NonNull LifecycleOwner owner, @NonNull Observer<List<UserInfoEntity>> observer) {
        mUserRecommendListData.observe(owner, observer);
    }

    /** 主页列表 **/

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