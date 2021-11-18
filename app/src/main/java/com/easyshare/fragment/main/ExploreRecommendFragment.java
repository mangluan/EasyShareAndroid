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
import com.easyshare.base.BaseException;
import com.easyshare.base.BaseFragment;
import com.easyshare.base.RxjavaResponse;
import com.easyshare.base.RxjavaThrowable;
import com.easyshare.network.RetrofitFactory;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@SuppressLint("NonConstantResourceId")
public class ExploreRecommendFragment extends BaseFragment {

    private ExploreRecommendViewModel mViewModel;

    @BindView(R.id.RecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.SmartRefreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;

    ExploreRecommendAdapter adapter;

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
        // init RecyclerView
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ExploreRecommendAdapter(mViewModel.getAlbumListData());
        mRecyclerView.setAdapter(adapter);
        // init observe
        mViewModel.observeAlbumListData(getViewLifecycleOwner(), list -> {
            int limit = list.size() / mViewModel.getPageIndex();
            adapter.notifyItemRangeChanged(list.size() - limit, limit);
        });
        // 页码有变动，调用加载代码
        mViewModel.observePageIndexData(getViewLifecycleOwner(), this::initData);
        // 刷新页码归零
        mSmartRefreshLayout.setOnRefreshListener(refreshLayout -> mViewModel.makeZeroPage());
        // 加载页码自增
        mSmartRefreshLayout.setOnLoadMoreListener(refreshLayout -> mViewModel.addSelfPage());
    }

    /**
     * 网络获取数据
     */
    private void initData(int pageIndex) {
        Disposable subscribe = RetrofitFactory.getsInstance(getContext())
                .getAllAlbum(null, String.valueOf(pageIndex), null)
                .subscribeOn(Schedulers.io()) // 子线程执行方法
                .observeOn(AndroidSchedulers.mainThread()) // 主线程回调
                .subscribe(resp -> {   // 成功回调
                    if (resp.getCode() == 0) {
                        if (pageIndex == 1) {
                            adapter.notifyDataSetChanged();
                            mSmartRefreshLayout.finishRefresh();
                            mViewModel.setAlbumListData(resp.getData());
                            if (resp.getData().size() == 0) { // 显示没有数据提示
                                mSmartRefreshLayout.finishLoadMoreWithNoMoreData();
                            }
                        } else if (resp.getData().size() != 0) {
                            mSmartRefreshLayout.finishLoadMore();
                            mViewModel.addAlbumListData(resp.getData());
                        } else {
                            mSmartRefreshLayout.finishLoadMoreWithNoMoreData();
                        }
                    } else {
                        throw new BaseException(resp.getMsg());
                    }
                }, (RxjavaThrowable) throwable -> {   // 出错回调
                    // 页面显示
                    if (pageIndex == 1) {
                        mSmartRefreshLayout.finishRefresh(false);
                    } else {
                        mSmartRefreshLayout.finishLoadMore(false);
                    }
                });
        mDisposables.add(subscribe);
    }

}