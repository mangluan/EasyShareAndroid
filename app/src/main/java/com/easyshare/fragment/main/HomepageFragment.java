package com.easyshare.fragment.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.easyshare.R;
import com.easyshare.activity.FriendsActivity;
import com.easyshare.activity.LoginActivity;
import com.easyshare.activity.MainActivity;
import com.easyshare.activity.SearchActivity;
import com.easyshare.activity.SelectClassificationActivity;
import com.easyshare.adapter.ExploreAdapter;
import com.easyshare.adapter.UserRecommendAdapter;
import com.easyshare.base.BaseDataBean;
import com.easyshare.base.BaseException;
import com.easyshare.base.BaseFragment;
import com.easyshare.base.RxjavaResponse;
import com.easyshare.base.RxjavaThrowable;
import com.easyshare.entity.PhotoAlbumEntity;
import com.easyshare.entity.UserInfoEntity;
import com.easyshare.network.Constants;
import com.easyshare.network.RetrofitFactory;
import com.easyshare.utils.UserUtils;
import com.hjq.toast.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 主页
 */
@SuppressLint("NonConstantResourceId")
public class HomepageFragment extends BaseFragment implements UserRecommendAdapter.OnClickListener, ExploreAdapter.OnClickListener {

    private HomepageViewModel mViewModel;

    @BindView(R.id.rv_users_to_recommend)
    RecyclerView mUserRecommendRecyclerView;
    UserRecommendAdapter mUserRecommendAdapter;

    @BindView(R.id.login_prompt_layout)
    View mLoginPromptLayout;

    @BindView(R.id.SmartRefreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;
    @BindView(R.id.HomeRecyclerView)
    RecyclerView mHomeRecyclerView;
    ExploreAdapter mHomeExploreAdapter;

    public static HomepageFragment newInstance() {
        return new HomepageFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_homepage, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HomepageViewModel.class);
        // init User RecyclerView
        mUserRecommendRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mUserRecommendAdapter = new UserRecommendAdapter(mViewModel.getUserRecommendListData());
        mUserRecommendAdapter.setOnClickListener(this);
        mUserRecommendRecyclerView.setAdapter(mUserRecommendAdapter);
        // init user observe
        mViewModel.observeUserRecommendListData(getViewLifecycleOwner(),
                list -> mUserRecommendAdapter.notifyDataSetChanged());

        // init Home RecyclerView
        mHomeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mHomeExploreAdapter = new ExploreAdapter(mViewModel.getAlbumListData());
        mHomeExploreAdapter.setOnClickListener(this);
        mHomeRecyclerView.setAdapter(mHomeExploreAdapter);
        // init home observe
        mViewModel.observeAlbumListData(getViewLifecycleOwner(), list -> {
            int limit = list.size() / mViewModel.getPageIndex();
            mHomeExploreAdapter.notifyItemRangeChanged(list.size() - limit, limit);
        });
        // 页码有变动，调用加载代码
        mViewModel.observePageIndexData(getViewLifecycleOwner(), this::initData);
        // 刷新页码归零
        mSmartRefreshLayout.setOnRefreshListener(refreshLayout -> mViewModel.makeZeroPage());
        // 加载页码自增
        mSmartRefreshLayout.setOnLoadMoreListener(refreshLayout -> mViewModel.addSelfPage());
        // 加载数据
        initData();
    }

    /**
     * 初始化主页列表数据
     */
    private void initData(int pageIndex) {
        Disposable subscribe = RetrofitFactory.getsInstance(getContext())
                .getAllAlbumList(null, String.valueOf(pageIndex), null)
                .subscribeOn(Schedulers.io()) // 子线程执行方法
                .observeOn(AndroidSchedulers.mainThread()) // 主线程回调
                .subscribe(resp -> {   // 成功回调
                    if (resp.getCode() == 0) {
                        if (pageIndex == 1) {
                            mHomeExploreAdapter.notifyDataSetChanged();
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

    /**
     * 网络获取数据
     */
    private void initData() {
        initUserRecommendList();
        if (UserUtils.getsInstance().isLogin()) {

        } else { // 未登录
            mLoginPromptLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 网络获取用户推荐数据
     */
    private void initUserRecommendList() {
        Disposable userRecommendDisposable = RetrofitFactory.getsInstance(getContext())
                .getUserRecommendList()
                .subscribeOn(Schedulers.io()) // 子线程执行方法
                .observeOn(AndroidSchedulers.mainThread()) // 主线程回调
                .subscribe(resp -> {   // 成功回调
                    if (resp.getCode() == 0) {
                        mViewModel.setUserRecommendListData(resp.getData());
                    } else {
                        throw new BaseException(resp.getMsg());
                    }
                }, (RxjavaThrowable) throwable -> initUserRecommendList());
        mDisposables.add(userRecommendDisposable);
    }

    /**
     * 点击登录
     */
    @OnClick(R.id.login_btn)
    public void onLoginClick() {
        LoginActivity.startActivity(getContext());
    }

    /**
     * 点击随便看看  - 跳转发现页
     */
    @OnClick(R.id.to_explore_btn)
    public void onToExploreClick() {
        if (getActivity() instanceof MainActivity)
            ((MainActivity) getActivity()).onToExploreClick();
    }

    /**
     * 登录成功通知 、 打开分类选择通知
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(BaseDataBean<UserInfoEntity> dataBean) {
        if (dataBean.getCode() == Constants.LOGIN_SUCCESSFULLY) { // 登录成功
            UserInfoEntity user = dataBean.getData();
            if (user != null) { // 填充用户信息
                if (mLoginPromptLayout.getVisibility() == View.VISIBLE)  // 登录提示隐藏
                    mLoginPromptLayout.setVisibility(View.INVISIBLE);

            }
        } else if (dataBean.getCode() == Constants.OPEN_SELECT_CLASSIFICATION_ACTIVITY) {  // 打开选择分类
            SelectClassificationActivity.startActivity(getContext());
        }
    }

    /**
     * 退出登录监听
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogout(BaseDataBean<Void> dataBean) {
        if (dataBean.getCode() == Constants.LOGOUT) {
            mLoginPromptLayout.setVisibility(View.VISIBLE);
            // TODO
        }
    }

    /**
     * 用户推荐 item 点击
     */
    @Override
    public void onClickUserItemListener(UserInfoEntity entity, int position) {

    }

    /**
     * 用户推荐 item button 点击
     */
    @Override
    public void onClickUserSubmitListener(Button button, UserInfoEntity entity, int position) {
        if (!UserUtils.getsInstance().isLogin())
            ToastUtils.show(R.string.error_login);
        button.setEnabled(false);
        Observable<BaseDataBean<Void>> observable = entity.isFriend() ?
                RetrofitFactory.getsInstance(getContext()).cancelAttention(String.valueOf(entity.getUserId())) :
                RetrofitFactory.getsInstance(getContext()).appendAttention(String.valueOf(entity.getUserId()));
        Disposable subscribe = observable.subscribeOn(Schedulers.io()) // 子线程执行方法
                .observeOn(AndroidSchedulers.mainThread()) // 主线程回调
                .subscribe(resp -> {   // 成功回调
                    RxjavaResponse.authentication(resp, getContext());
                    if (resp.getCode() == 0) {
                        ToastUtils.show(resp.getMsg());
                        entity.setIsFriend(entity.isFriend() ? 0 : 1);
                        mUserRecommendAdapter.notifyItemChanged(position);
                    } else {
                        throw new BaseException(resp.getMsg());
                    }
                    button.setEnabled(true);
                }, (RxjavaThrowable) throwable -> button.setEnabled(true));
        mDisposables.add(subscribe);
    }


    /**
     * 点击搜索图标
     */
    @OnClick(R.id.search_view)
    public void onSearchClick(View view) {
        SearchActivity.startActivity(getContext());
    }

    /**
     * 点击 item
     */
    @Override
    public void onClickItemListener(PhotoAlbumEntity entity) {

    }

    /**
     * 点击 item
     */
    @Override
    public void onClickMoreListener(PhotoAlbumEntity entity) {

    }

    /**
     * 点击 item
     */
    @Override
    public void onClickLikeListener(PhotoAlbumEntity entity) {

    }

    /**
     * 点击 item
     */
    @Override
    public void onClickCommentListener(PhotoAlbumEntity entity) {

    }

    /**
     * 点击 item
     */
    @Override
    public void onClickShareListener(PhotoAlbumEntity entity) {

    }
}