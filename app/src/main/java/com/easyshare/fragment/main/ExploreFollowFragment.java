package com.easyshare.fragment.main;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easyshare.R;
import com.easyshare.base.BaseFragment;

public class ExploreFollowFragment extends BaseFragment {

    private ExploreFollowViewModel mViewModel;

    public static ExploreFollowFragment newInstance() {
        return new ExploreFollowFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_explore_follow, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ExploreFollowViewModel.class);
        // TODO: Use the ViewModel
    }

}