package com.easyshare.fragment.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.easyshare.R;
import com.easyshare.adapter.ExploreRecommendAdapter;
import com.easyshare.base.BaseFragment;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

@SuppressLint("NonConstantResourceId")
public class ExploreRecommendFragment extends BaseFragment {

    private ExploreRecommendViewModel mViewModel;

    @BindView(R.id.RecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.SmartRefreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;


    public static ExploreRecommendFragment newInstance() {
        return new ExploreRecommendFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_explore_recommend, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ExploreRecommendViewModel.class);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<Object> mList = new ArrayList<>();
        ExploreRecommendAdapter mAdapter = new ExploreRecommendAdapter(mList);
        mRecyclerView.setAdapter(mAdapter);
//        mSmartRefreshLayout.autoRefresh();
    }

}