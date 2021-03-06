package com.easyshare.fragment.album;

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
import com.easyshare.adapter.ExploreAdapter;
import com.easyshare.base.BaseException;
import com.easyshare.base.BaseFragment;
import com.easyshare.base.RxjavaThrowable;
import com.easyshare.network.RetrofitFactory;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@SuppressLint("NonConstantResourceId")
public class LikeRecordFragment extends BaseFragment {

    private LikeRecordViewModel mViewModel;

    @BindView(R.id.RecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.SmartRefreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;

    ExploreAdapter adapter;

    public static LikeRecordFragment newInstance() {
        return new LikeRecordFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_like_record, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LikeRecordViewModel.class);
        // init RecyclerView
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ExploreAdapter(mViewModel.getAlbumListData());
        mRecyclerView.setAdapter(adapter);
        // init observe
        mViewModel.observeAlbumListData(getViewLifecycleOwner(), list -> {
            int limit = list.size() / mViewModel.getPageIndex();
            adapter.notifyItemRangeChanged(list.size() - limit, limit);
        });
        // ????????????????????????????????????
        mViewModel.observePageIndexData(getViewLifecycleOwner(), this::initData);
        // ??????????????????
        mSmartRefreshLayout.setOnRefreshListener(refreshLayout -> mViewModel.makeZeroPage());
        // ??????????????????
        mSmartRefreshLayout.setOnLoadMoreListener(refreshLayout -> mViewModel.addSelfPage());
    }

    /**
     * ??????????????????
     */
    private void initData(int pageIndex) {
        Disposable subscribe = RetrofitFactory.getsInstance(getContext())
                .getLikeAlbumList(String.valueOf(pageIndex), null)
                .subscribeOn(Schedulers.io()) // ?????????????????????
                .observeOn(AndroidSchedulers.mainThread()) // ???????????????
                .subscribe(resp -> {   // ????????????
                    if (resp.getCode() == 0) {
                        if (pageIndex == 1) {
                            adapter.notifyDataSetChanged();
                            mSmartRefreshLayout.finishRefresh();
                            if (resp.getData().size() == 0) {
                                mSmartRefreshLayout.finishLoadMoreWithNoMoreData();
                                adapter.setDataEmpty();
                            }
                            getView().findViewById(R.id.text_no_data).setVisibility(
                                    resp.getData().size() == 0 ? View.VISIBLE : View.INVISIBLE);
                            mViewModel.setAlbumListData(resp.getData());
                        } else if (resp.getData().size() != 0) {
                            mSmartRefreshLayout.finishLoadMore();
                            mViewModel.addAlbumListData(resp.getData());
                        } else {
                            mSmartRefreshLayout.finishLoadMoreWithNoMoreData();
                        }
                    } else {
                        throw new BaseException(resp.getMsg());
                    }
                }, (RxjavaThrowable) throwable -> {   // ????????????
                    // ????????????
                    if (pageIndex == 1) {
                        mSmartRefreshLayout.finishRefresh(false);
                    } else {
                        mSmartRefreshLayout.finishLoadMore(false);
                    }
                });
        mDisposables.add(subscribe);
    }


}