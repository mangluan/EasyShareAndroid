package com.easyshare.fragment.user;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easyshare.R;
import com.easyshare.base.BaseFragment;

public class AccountSecurityFragment extends BaseFragment {

    private AccountSecurityViewModel mViewModel;

    public static AccountSecurityFragment newInstance() {
        return new AccountSecurityFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account_security, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AccountSecurityViewModel.class);
        // TODO: Use the ViewModel
    }

}