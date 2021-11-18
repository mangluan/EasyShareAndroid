package com.easyshare.fragment.user;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.easyshare.R;
import com.easyshare.activity.FriendsActivity;
import com.easyshare.adapter.FriendsAdapter;
import com.easyshare.base.BaseDataBean;
import com.easyshare.base.BaseException;
import com.easyshare.base.BaseFragment;
import com.easyshare.base.RxjavaResponse;
import com.easyshare.base.RxjavaThrowable;
import com.easyshare.entity.UserInfoEntity;
import com.easyshare.network.RetrofitFactory;
import com.hjq.toast.ToastUtils;
import com.lxj.xpopup.XPopup;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.List;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@SuppressLint("NonConstantResourceId")
public class FriendsFragment extends BaseFragment implements FriendsAdapter.OnClickListener {

    private FriendsViewModel mViewModel;

    @BindView(R.id.RecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.SmartRefreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    FriendsAdapter adapter;


    public static FriendsFragment newInstance(FriendsActivity.FriendsType friendsType) {
        FriendsFragment friendsFragment = new FriendsFragment();
        Bundle args = new Bundle();
        args.putSerializable(FriendsActivity.FRIENDS_TYPE, friendsType);
        friendsFragment.setArguments(args);
        return friendsFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FriendsViewModel.class);
        // init title
        FriendsActivity.FriendsType type = (FriendsActivity.FriendsType) getArguments().getSerializable(FriendsActivity.FRIENDS_TYPE);
        mToolbar.setTitle(type == FriendsActivity.FriendsType.ATTENTION ? R.string.attention : R.string.fans);
        // init RecyclerView
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FriendsAdapter(mViewModel.getFriendListData(), type);
        adapter.setOnClickListener(this);
        mRecyclerView.setAdapter(adapter);
        // init observe
        mViewModel.observeFriendListData(getViewLifecycleOwner(), list -> {
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
        FriendsActivity.FriendsType type = (FriendsActivity.FriendsType) getArguments().getSerializable(FriendsActivity.FRIENDS_TYPE);
        Observable<BaseDataBean<List<UserInfoEntity>>> observable = type == FriendsActivity.FriendsType.ATTENTION
                ? RetrofitFactory.getsInstance(getContext()).getUserAttentionList(String.valueOf(pageIndex), null)
                : RetrofitFactory.getsInstance(getContext()).getUserFansList(String.valueOf(pageIndex), null);
        Disposable subscribe = observable
                .subscribeOn(Schedulers.io()) // 子线程执行方法
                .observeOn(AndroidSchedulers.mainThread()) // 主线程回调
                .subscribe(resp -> {   // 成功回调
                    RxjavaResponse.authentication(resp, getContext());
                    if (resp.getCode() == 0) {
                        if (pageIndex == 1) {
                            adapter.notifyDataSetChanged();
                            mSmartRefreshLayout.finishRefresh();
                            mViewModel.setFriendListData(resp.getData());
                            if (resp.getData().size() == 0) { // 显示没有数据提示
                                mSmartRefreshLayout.finishLoadMoreWithNoMoreData();
                            }
                        } else if (resp.getData().size() != 0) {
                            mSmartRefreshLayout.finishLoadMore();
                            mViewModel.addFriendListData(resp.getData());
                        } else {
                            mSmartRefreshLayout.finishLoadMoreWithNoMoreData();
                        }
                    } else {
                        throw new BaseException(resp.getMsg());
                    }
                }, (RxjavaThrowable) throwable -> {
                    // 页面显示
                    if (pageIndex == 1) {
                        mSmartRefreshLayout.finishRefresh(false);
                    } else {
                        mSmartRefreshLayout.finishLoadMore(false);
                    }
                });
        mDisposables.add(subscribe);
    }

    /**
     * 点击 item 项
     * TODO
     */
    @Override
    public void onClickItemListener(UserInfoEntity entity, FriendsActivity.FriendsType type, int position) {

    }

    /**
     * 点击 item 按钮 TODO
     */
    @Override
    public void onClickSubmitListener(Button button, UserInfoEntity entity, FriendsActivity.FriendsType type, int position) {
        if (entity.isFriend() || // 是互关好友，直接进入取关 ;  如果是关注页面且关注过了
                (type == FriendsActivity.FriendsType.ATTENTION && entity.getIsFriend() >= 0)) {
            new XPopup.Builder(getContext())
                    .asConfirm(null, getString(R.string.cancel_attention_hint, entity.getName()), getString(R.string.cancel), getString(R.string.confirm),
                            () -> cancelAttention(button, entity, type, position), null, false, 0)
                    .show();
        } else { // 添加关注
            appendAttention(button, entity, type, position);
        }
    }

    /**
     * 添加关注
     */
    private void appendAttention(Button button, UserInfoEntity entity, FriendsActivity.FriendsType type, int position) {
        button.setEnabled(false);
        Disposable subscribe = RetrofitFactory.getsInstance(getContext())
                .appendAttention(String.valueOf(entity.getUserId()))
                .subscribeOn(Schedulers.io()) // 子线程执行方法
                .observeOn(AndroidSchedulers.mainThread()) // 主线程回调
                .subscribe(resp -> {   // 成功回调
                    RxjavaResponse.authentication(resp, getContext());
                    if (resp.getCode() == 0) {
                        ToastUtils.show(resp.getMsg());
                        if (type == FriendsActivity.FriendsType.ATTENTION) {
                            entity.setIsFriend(entity.getIsFriend() + 2);
                        }else {
                            entity.setIsFriend(entity.getIsFriend() + 1);
                        }
                        adapter.notifyItemChanged(position);
                    } else {
                        throw new BaseException(resp.getMsg());
                    }
                    button.setEnabled(true);
                }, (RxjavaThrowable) throwable -> button.setEnabled(true));
        mDisposables.add(subscribe);
    }

    /**
     * 取消关注
     */
    private void cancelAttention(Button button, UserInfoEntity entity, FriendsActivity.FriendsType type, int position) {
        Disposable subscribe = RetrofitFactory.getsInstance(getContext())
                .cancelAttention(String.valueOf(entity.getUserId()))
                .subscribeOn(Schedulers.io()) // 子线程执行方法
                .observeOn(AndroidSchedulers.mainThread()) // 主线程回调
                .subscribe(resp -> {   // 成功回调
                    RxjavaResponse.authentication(resp, getContext());
                    if (resp.getCode() == 0) {
                        ToastUtils.show(resp.getMsg());
                        if (type == FriendsActivity.FriendsType.ATTENTION) {
                            entity.setIsFriend(entity.getIsFriend() - 2);
                        }else {
                            entity.setIsFriend(entity.getIsFriend() - 1);
                        }
                        adapter.notifyItemChanged(position);
                    } else {
                        throw new BaseException(resp.getMsg());
                    }
                    button.setEnabled(true);
                }, (RxjavaThrowable) throwable -> button.setEnabled(true));
        mDisposables.add(subscribe);
    }

}