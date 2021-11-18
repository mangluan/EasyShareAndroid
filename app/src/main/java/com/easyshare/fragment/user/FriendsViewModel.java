package com.easyshare.fragment.user;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.easyshare.entity.UserInfoEntity;

import java.util.ArrayList;
import java.util.List;

public class FriendsViewModel extends ViewModel {

    private MutableLiveData<List<UserInfoEntity>> mFriendListData;
    private MutableLiveData<Integer> mPageIndexData;

    public FriendsViewModel() {
        mFriendListData = new MutableLiveData<>();
        mFriendListData.setValue(new ArrayList<>());
        mPageIndexData = new MutableLiveData<>();
        mPageIndexData.setValue(1);
    }

    public List<UserInfoEntity> getFriendListData() {
        return mFriendListData.getValue();
    }

    public void setFriendListData(List<UserInfoEntity> friendListData) {
        mFriendListData.getValue().clear();
        addFriendListData(friendListData);
    }

    public void addFriendListData(List<UserInfoEntity> albumListData) {
        mFriendListData.getValue().addAll(albumListData);
        mFriendListData.setValue(mFriendListData.getValue());
    }

    public void observeFriendListData(@NonNull LifecycleOwner owner, @NonNull Observer<List<UserInfoEntity>> observer) {
        mFriendListData.observe(owner, observer);
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