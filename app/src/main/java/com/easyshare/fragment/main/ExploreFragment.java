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
import com.easyshare.activity.DetailsImageTextActivity;
import com.easyshare.adapter.ExploreAdapter;
import com.easyshare.base.BaseException;
import com.easyshare.base.BaseFragment;
import com.easyshare.base.RxjavaThrowable;
import com.easyshare.entity.PhotoAlbumEntity;
import com.easyshare.network.RetrofitFactory;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@SuppressLint("NonConstantResourceId")
public class ExploreFragment extends BaseFragment implements ExploreAdapter.OnClickListener {

    private ExploreViewModel mViewModel;

    @BindView(R.id.RecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.SmartRefreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;

    ExploreAdapter adapter;

    public static ExploreFragment newInstance() {
        return new ExploreFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ExploreViewModel.class);
        // init RecyclerView
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ExploreAdapter(mViewModel.getAlbumListData());
        adapter.setOnClickListener(this);
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
                .getAllAlbumList(null, String.valueOf(pageIndex), null)
                .subscribeOn(Schedulers.io()) // ?????????????????????
                .observeOn(AndroidSchedulers.mainThread()) // ???????????????
                .subscribe(resp -> {   // ????????????
                    if (resp.getCode() == 0) {
                        if (pageIndex == 1) {
                            adapter.notifyDataSetChanged();
                            mSmartRefreshLayout.finishRefresh();
                            mViewModel.setAlbumListData(resp.getData());
                            if (resp.getData().size() == 0) { // ????????????????????????
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

    /**
     * ??????item??? -> ???????????????
     */
    @Override
    public void onClickItemListener(PhotoAlbumEntity entity) {
        DetailsImageTextActivity.startActivity(getContext(),entity);
    }

    @Override
    public void onClickMoreListener(PhotoAlbumEntity entity) {

    }

    @Override
    public void onClickLikeListener(PhotoAlbumEntity entity) {

    }

    @Override
    public void onClickCommentListener(PhotoAlbumEntity entity) {

    }

    @Override
    public void onClickShareListener(PhotoAlbumEntity entity) {

    }
}