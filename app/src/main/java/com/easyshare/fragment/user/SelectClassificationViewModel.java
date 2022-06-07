package com.easyshare.fragment.user;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.easyshare.entity.ClassificationEntity;

import java.util.ArrayList;
import java.util.List;

public class SelectClassificationViewModel extends ViewModel {

    private MutableLiveData<List<ClassificationEntity>> mClassificationListData;
    private MutableLiveData<List<ClassificationEntity>> mSelectData;
    private MutableLiveData<Integer> mInitCount;

    public SelectClassificationViewModel() {
        mInitCount = new MutableLiveData<>();
        mInitCount.setValue(0);
        mClassificationListData = new MutableLiveData<>();
        mClassificationListData.setValue(new ArrayList<>());
        mSelectData = new MutableLiveData<>();
        mSelectData.setValue(new ArrayList<>());
    }

    public List<ClassificationEntity> getClassificationListData() {
        return mClassificationListData.getValue();
    }

    public void setClassificationListData(List<ClassificationEntity> list) {
        mClassificationListData.getValue().clear();
        mClassificationListData.getValue().addAll(list);
        mClassificationListData.setValue(mClassificationListData.getValue());
    }

    public void observeClassificationListData(@NonNull LifecycleOwner owner, @NonNull Observer<List<ClassificationEntity>> observer) {
        mClassificationListData.observe(owner, observer);
    }

    public List<ClassificationEntity> getSelectData() {
        return mSelectData.getValue();
    }

    public void setSelectData(MutableLiveData<List<ClassificationEntity>> selectData) {
        mSelectData = selectData;
    }

    public void removeSelectData(ClassificationEntity entity) {
        mSelectData.getValue().remove(entity);
        mSelectData.setValue(mSelectData.getValue());
    }

    public void addSelectData(ClassificationEntity entity) {
        mSelectData.getValue().add(entity);
        mSelectData.setValue(mSelectData.getValue());
    }

    public void observeSelectData(@NonNull LifecycleOwner owner, @NonNull Observer<List<ClassificationEntity>> observer) {
        mSelectData.observe(owner, observer);
    }

    public void initCount() {
        mInitCount.setValue(1);
    }

    public boolean isInitCount() {
        return mInitCount.getValue() == 1;
    }
}