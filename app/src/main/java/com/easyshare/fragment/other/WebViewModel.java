package com.easyshare.fragment.other;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

public class WebViewModel extends ViewModel {


    private MutableLiveData<String> mWebURL;

    public WebViewModel() {
        mWebURL = new MutableLiveData<>();
    }

    public String getWebURL() {
        return mWebURL.getValue();
    }

    public void setWebURL(String webURL) {
        mWebURL.setValue(webURL);
    }

    public void observeWebURL(@NonNull LifecycleOwner owner, @NonNull Observer<String> observer) {
        mWebURL.observe(owner, observer);
    }

}