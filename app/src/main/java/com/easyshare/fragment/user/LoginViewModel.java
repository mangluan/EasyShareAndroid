package com.easyshare.fragment.user;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {

    public enum ActionState {
        WAITING_INPUT_MAIL, AUTH_CODE_LOGIN, PASSWORD_LOGIN
    }

    public enum ShowState {
        SHOW, HIDDEN
    }

    private MutableLiveData<String> mMailString;
    private MutableLiveData<ActionState> mActionState;
    private MutableLiveData<ShowState> mPasswordShowState;

    public LoginViewModel() {
        mMailString = new MutableLiveData<>();
        mActionState = new MutableLiveData<>();
        mActionState.setValue(ActionState.WAITING_INPUT_MAIL);
        mPasswordShowState = new MutableLiveData<>();
        mPasswordShowState.setValue(ShowState.HIDDEN);
    }

    public String getMail() {
        return mMailString.getValue();
    }

    public void setMail(String mail) {
        mMailString.setValue(mail);
    }

    public void observeMail(@NonNull LifecycleOwner owner, @NonNull Observer<String> observer) {
        mMailString.observe(owner, observer);
    }


    public ActionState getActionState() {
        return mActionState.getValue();
    }

    public void setActionState(ActionState actionState) {
        mActionState.setValue(actionState);
    }

    public void observeActionState(@NonNull LifecycleOwner owner, @NonNull Observer<ActionState> observer) {
        mActionState.observe(owner, observer);
    }

    public ShowState getPasswordShowState() {
        return mPasswordShowState.getValue();
    }

    public void setPasswordShowState(ShowState showState) {
        mPasswordShowState.setValue(showState);
    }

    public void observePasswordShowState(@NonNull LifecycleOwner owner, @NonNull Observer<ShowState> observer) {
        mPasswordShowState.observe(owner, observer);
    }


}