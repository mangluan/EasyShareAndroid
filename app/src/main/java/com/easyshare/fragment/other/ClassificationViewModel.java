package com.easyshare.fragment.other;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.easyshare.entity.ClassificationEntity;
import com.easyshare.entity.LoginRecordEntity;

import java.util.ArrayList;
import java.util.List;

public class ClassificationViewModel extends ViewModel {

    private MutableLiveData<List<ClassificationEntity>> mClassificationListData;

    public ClassificationViewModel() {
        mClassificationListData = new MutableLiveData<>();
        mClassificationListData.setValue(new ArrayList<>());
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


}