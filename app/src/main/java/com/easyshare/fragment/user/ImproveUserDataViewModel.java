package com.easyshare.fragment.user;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

public class ImproveUserDataViewModel extends ViewModel {


    private MutableLiveData<String> mAvatarData;
    private MutableLiveData<String> mSexData;
    private MutableLiveData<String> mBirthdayData;

    public ImproveUserDataViewModel() {
        mAvatarData = new MutableLiveData<>();
        mSexData = new MutableLiveData<>();
        mBirthdayData = new MutableLiveData<>();
    }

    public String getAvatarData() {
        return mAvatarData.getValue();
    }

    public void setAvatarData(String avatarData) {
        mAvatarData.setValue(avatarData);
    }

    public void observeAvatarData(@NonNull LifecycleOwner owner, @NonNull Observer<String> observer) {
        mAvatarData.observe(owner, observer);
    }


    public String getSexData() {
        return mSexData.getValue();
    }

    public void setSexData(String sexData) {
        mSexData.setValue(sexData);
    }

    public void observeSexData(@NonNull LifecycleOwner owner, @NonNull Observer<String> observer) {
        mSexData.observe(owner, observer);
    }

    public String getBirthdayData() {
        return mBirthdayData.getValue();
    }

    public void setBirthdayData(String birthdayData) {
        mBirthdayData.setValue(birthdayData);
    }


    public void observeBirthdayData(@NonNull LifecycleOwner owner, @NonNull Observer<String> observer) {
        mBirthdayData.observe(owner, observer);
    }

}