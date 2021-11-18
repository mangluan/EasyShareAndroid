package com.easyshare.fragment.user;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.easyshare.entity.LoginRecordEntity;
import com.easyshare.entity.UserInfoEntity;

import java.util.ArrayList;
import java.util.List;

public class LoginRecordViewModel extends ViewModel {

    private MutableLiveData<List<LoginRecordEntity>> mRecordListData;

    public LoginRecordViewModel() {
        mRecordListData = new MutableLiveData<>();
        mRecordListData.setValue(new ArrayList<>());
    }

    public List<LoginRecordEntity> getRecordListData() {
        return mRecordListData.getValue();
    }

    public void setRecordListData(List<LoginRecordEntity> recordListData) {
        mRecordListData.getValue().clear();
        mRecordListData.getValue().addAll(recordListData);
        mRecordListData.setValue(mRecordListData.getValue());
    }

    public void observeFriendListData(@NonNull LifecycleOwner owner, @NonNull Observer<List<LoginRecordEntity>> observer) {
        mRecordListData.observe(owner, observer);
    }

}