package com.easyshare.fragment.user;

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
import com.easyshare.adapter.LoginRecordAdapter;
import com.easyshare.base.BaseException;
import com.easyshare.base.BaseFragment;
import com.easyshare.base.RxjavaResponse;
import com.easyshare.base.RxjavaThrowable;
import com.easyshare.network.RetrofitFactory;
import com.hjq.toast.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LoginRecordFragment extends BaseFragment {

    private LoginRecordViewModel mViewModel;

    @BindView(R.id.RecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.SmartRefreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;

    LoginRecordAdapter adapter;

    public static LoginRecordFragment newInstance() {
        return new LoginRecordFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login_record, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LoginRecordViewModel.class);
        // init RecyclerView
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new LoginRecordAdapter(mViewModel.getRecordListData());
        mRecyclerView.setAdapter(adapter);
        // init observe
        mViewModel.observeFriendListData(getViewLifecycleOwner(), list -> {
            adapter.notifyItemRangeChanged(0, list.size());
        });
        // ??????????????????
        mSmartRefreshLayout.setOnRefreshListener(refreshLayout -> initData());
        // ????????????
        mSmartRefreshLayout.autoRefresh();
    }

    /**
     * ??????????????????
     */
    private void initData() {
        Disposable subscribe = RetrofitFactory.getsInstance(getContext())
                .getLoginRecord()
                .subscribeOn(Schedulers.io()) // ?????????????????????
                .observeOn(AndroidSchedulers.mainThread()) // ???????????????
                .subscribe(resp -> {   // ????????????
                    RxjavaResponse.authentication(resp,getContext());
                    if (resp.getCode() == 0) {
                        adapter.notifyDataSetChanged();
                        mSmartRefreshLayout.finishRefresh();
                        mViewModel.setRecordListData(resp.getData());
                    } else {
                        throw new BaseException(resp.getMsg());
                    }
                }, (RxjavaThrowable) throwable -> {   // ????????????
                    mSmartRefreshLayout.finishRefresh(false);
                });
        mDisposables.add(subscribe);
    }

}