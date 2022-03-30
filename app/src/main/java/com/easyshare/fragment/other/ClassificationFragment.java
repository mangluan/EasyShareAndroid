package com.easyshare.fragment.other;

import android.app.Activity;
import android.content.Intent;
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
import com.easyshare.adapter.ClassificationAdapter;
import com.easyshare.base.BaseException;
import com.easyshare.base.BaseFragment;
import com.easyshare.base.RxjavaThrowable;
import com.easyshare.entity.ClassificationEntity;
import com.easyshare.network.RetrofitFactory;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ClassificationFragment extends BaseFragment implements ClassificationAdapter.OnClickListener {

    private ClassificationViewModel mViewModel;

    @BindView(R.id.RecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.SmartRefreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;

    ClassificationAdapter adapter;

    public static ClassificationFragment newInstance() {
        return new ClassificationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_classification, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ClassificationViewModel.class);
        // init RecyclerView
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ClassificationAdapter(mViewModel.getClassificationListData());
        mRecyclerView.setAdapter(adapter);
        adapter.setOnClickListener(this);
        // init observe
        mViewModel.observeClassificationListData(getViewLifecycleOwner(), list -> {
            adapter.notifyItemRangeChanged(0, list.size());
        });
        // 刷新页码归零
        mSmartRefreshLayout.setOnRefreshListener(refreshLayout -> initData());
        // 自动刷新
        mSmartRefreshLayout.autoRefresh();
    }

    /**
     * 网络获取数据
     */
    private void initData() {
        Disposable subscribe = RetrofitFactory.getsInstance(getContext())
                .getClassificationAdapter()
                .subscribeOn(Schedulers.io()) // 子线程执行方法
                .observeOn(AndroidSchedulers.mainThread()) // 主线程回调
                .subscribe(resp -> {   // 成功回调
                    if (resp.getCode() == 0) {
                        adapter.notifyDataSetChanged();
                        mSmartRefreshLayout.finishRefresh();
                        mViewModel.setClassificationListData(resp.getData());
                    } else {
                        throw new BaseException(resp.getMsg());
                    }
                }, (RxjavaThrowable) throwable -> {   // 出错回调
                    mSmartRefreshLayout.finishRefresh(false);
                });
        mDisposables.add(subscribe);
    }

    /**
     * item click
     */
    @Override
    public void onClickItemListener(ClassificationEntity entity, int position) {
        Intent intent = new Intent();
        intent.putExtra("classification",entity);
        getActivity().setResult(Activity.RESULT_OK,intent);
        getActivity().finish();
    }

}