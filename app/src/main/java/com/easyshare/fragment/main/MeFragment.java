package com.easyshare.fragment.main;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.easyshare.R;
import com.easyshare.activity.AboutSoftwareActivity;
import com.easyshare.activity.AccountSecurityActivity;
import com.easyshare.activity.FriendsActivity;
import com.easyshare.activity.LikeRecordActivity;
import com.easyshare.activity.LoginActivity;
import com.easyshare.base.BaseDataBean;
import com.easyshare.base.BaseException;
import com.easyshare.base.BaseFragment;
import com.easyshare.base.RxjavaResponse;
import com.easyshare.base.RxjavaThrowable;
import com.easyshare.entity.UserInfoEntity;
import com.easyshare.network.Constants;
import com.easyshare.network.RetrofitFactory;
import com.easyshare.utils.GlideCatchUtil;
import com.easyshare.utils.SharedPreferenceUtils;
import com.easyshare.utils.UserUtils;
import com.hjq.toast.ToastUtils;
import com.lxj.xpopup.XPopup;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * 我的
 */
@SuppressLint("NonConstantResourceId")
public class MeFragment extends BaseFragment {

    private MeViewModel mViewModel;

    @BindView(R.id.avatar_image)
    ImageView mAvatarImageView;
    @BindView(R.id.user_name)
    TextView mUserNameTextView;
    @BindView(R.id.title_subheading)
    TextView mTitleSubheadingTextView;
    @BindView(R.id.browsing_text)
    TextView mBrowsingCountTextView;
    @BindView(R.id.attention_text)
    TextView mAttentionCountTextView;
    @BindView(R.id.fans_text)
    TextView mFansCountTextView;
    @BindView(R.id.logout_btn)
    Button mLogoutButton;
    @BindView(R.id.cache_size_text)
    TextView mCacheSizeTextView;


    public static MeFragment newInstance() {
        return new MeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_me, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MeViewModel.class);
        // 如果有登录，则直接更新页面
        if (UserUtils.getsInstance().isLogin()) {
            onLogin(BaseDataBean.build(Constants.LOGIN_SUCCESSFULLY, UserUtils.getsInstance().getUserInfo()));
        }
    }

    /**
     * 缓存大小刷新
     * 刷新粉丝数
     */
    @Override
    public void onResume() {
        super.onResume();
        initCacheSize();
        initAttentionAndFans();
    }

    /**
     * 初始化 浏览记录、关注、粉丝 数量
     */
    private void initAttentionAndFans() {
        if (UserUtils.getsInstance().isLogin()) {
            Disposable subscribe = RetrofitFactory.getsInstance(getContext())
                    .getMoreUserInfo()
                    .subscribeOn(Schedulers.io()) // 子线程执行方法
                    .observeOn(AndroidSchedulers.mainThread()) // 主线程回调
                    .subscribe(resp -> {   // 成功回调
                        RxjavaResponse.authentication(resp, getContext());
                        if (resp.getCode() == 0) {
                            UserInfoEntity userInfoEntity = resp.getData();
                            mBrowsingCountTextView.setText(String.valueOf(userInfoEntity.getBrowsingCount()));
                            mAttentionCountTextView.setText(String.valueOf(userInfoEntity.getAttentionCount()));
                            mFansCountTextView.setText(String.valueOf(userInfoEntity.getFansCount()));
                            // 再放回去
                            UserUtils.getsInstance().getUserInfo().setBrowsingCount(userInfoEntity.getBrowsingCount());
                            UserUtils.getsInstance().getUserInfo().setAttentionCount(userInfoEntity.getAttentionCount());
                            UserUtils.getsInstance().getUserInfo().setFansCount(userInfoEntity.getFansCount());
                        } else {
                            throw new BaseException(resp.getMsg());
                        }
                    }, (RxjavaThrowable) throwable -> { // 出错了继续调用
                        initAttentionAndFans();
                    });
            mDisposables.add(subscribe);
        } else {  // 没有登录， TODO 查询本地数据库的历史浏览记录
            mBrowsingCountTextView.setText(R.string.text_zero);
            mAttentionCountTextView.setText(R.string.text_null);
            mFansCountTextView.setText(R.string.text_null);
        }
    }

    /**
     * 初始化缓存大小
     */
    private void initCacheSize() {
        String cacheSize = GlideCatchUtil.getInstance().getCacheSize(getContext());
        mCacheSizeTextView.setText(cacheSize);
    }

    /**
     * 点击标题布局
     */
    @OnClick(R.id.title_layout)
    public void onClickTitleLayout() {
        if (UserUtils.getsInstance().isLogin()) {

        } else { // 如果是没有登录的状态，点击则跳转到登录页面
            LoginActivity.startActivity(getContext());
        }
    }

    /**
     * 用户登录、信息修改
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogin(BaseDataBean<UserInfoEntity> dataBean) {
        if (dataBean.getCode() == Constants.LOGIN_SUCCESSFULLY  // 登录成功
                || dataBean.getCode() == Constants.UPDATE_USER_INFORMATION) { // 信息修改
            UserInfoEntity user = dataBean.getData();
            if (user != null) { // 填充用户信息
                mLogoutButton.setVisibility(View.VISIBLE);
                Glide.with(this).load(user.getAvatarImage())
                        .transition(withCrossFade()).into(mAvatarImageView);
                mUserNameTextView.setText(user.getName());
                mTitleSubheadingTextView.setText(getString(R.string.sign_format, TextUtils.isEmpty(user.getSign()) ? getString(R.string.sign_null) : user.getSign()));
                initAttentionAndFans();
            }
        }
    }

    /**
     * 退出登录监听
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogout(BaseDataBean<Void> dataBean) {
        if (dataBean.getCode() == Constants.LOGOUT) { // 删除用户信息
            mLogoutButton.setVisibility(View.GONE);
            Glide.with(this).load(R.drawable.img_default_avatar)
                    .transition(withCrossFade()).into(mAvatarImageView);
            mUserNameTextView.setText(R.string.not_login_name);
            mTitleSubheadingTextView.setText(R.string.not_login_subheading);
            initAttentionAndFans();
        }
    }

    /**
     * 退出登录按钮
     */
    @OnClick(R.id.logout_btn)
    public void onLogoutClick() {
        new XPopup.Builder(getContext())
                .isDestroyOnDismiss(true)
                .asConfirm(null, getString(R.string.logout_hint), getString(R.string.cancel), getString(R.string.confirm),
                        () -> {
                            UserUtils.getsInstance().logout();
                            ToastUtils.show(R.string.successfully_logout);
                            SharedPreferenceUtils.putString(getContext(), Constants.TOKEN, "");
                        }, null, false, 0)
                .show();
    }

    /**
     * 点击历史记录按钮
     */
    @OnClick(R.id.browsing_button)
    public void onBrowsingClick() {
        ToastUtils.show("历史记录");
    }

    /**
     * 点击关注按钮
     */
    @OnClick(R.id.attention_button)
    public void onAttentionClick() {
        if (UserUtils.getsInstance().isLogin()) {
            FriendsActivity.startActivity(getContext(), FriendsActivity.FriendsType.ATTENTION,
                    UserUtils.getsInstance().getUserInfo().getAttentionCount());
        } else {
            ToastUtils.show(R.string.error_login);
        }
    }

    /**
     * 点击粉丝按钮
     */
    @OnClick(R.id.fans_button)
    public void onFansClick() {
        if (UserUtils.getsInstance().isLogin()) {
            FriendsActivity.startActivity(getContext(), FriendsActivity.FriendsType.FANS,
                    UserUtils.getsInstance().getUserInfo().getAttentionCount());
        } else {
            ToastUtils.show(R.string.error_login);
        }
    }

    /**
     * 点击点赞按钮
     */
    @OnClick(R.id.like_button)
    public void onLikeClick() {
        if (UserUtils.getsInstance().isLogin()) {
            LikeRecordActivity.startActivity(getContext());
        } else {
            ToastUtils.show(R.string.error_login);
        }
    }

    /**
     * 点击图文按钮
     */
    @OnClick(R.id.photo_button)
    public void onPhotoClick() {
        ToastUtils.show("图文");
    }

    /**
     * 点击粉丝按钮
     */
    @OnClick(R.id.album_button)
    public void onAlbumClick() {
        ToastUtils.show("图册");
    }

    /**
     * 点击帐号安全
     */
    @OnClick(R.id.account_security_layout)
    public void onAccountSecurityClick() {
        if (UserUtils.getsInstance().isLogin()) {
            AccountSecurityActivity.startActivity(getContext());
        } else {
            ToastUtils.show(R.string.error_login);
        }
    }

    /**
     * 点击消息通知
     */
    @OnClick(R.id.message_notification_layout)
    public void onMessageNotificationClick() {
        ToastUtils.show("消息通知");
    }

    /**
     * 点击屏蔽管理
     */
    @OnClick(R.id.shielding_management_layout)
    public void onShieldingManagementClick() {
        ToastUtils.show("屏蔽管理");
    }

    /**
     * 点击屏蔽管理
     */
    @OnClick(R.id.clear_cache_layout)
    public void onClearCacheClick() {
        new XPopup.Builder(getContext())
                .isDestroyOnDismiss(true)
                .asConfirm(null, getString(R.string.clear_cache_hint), getString(R.string.cancel), getString(R.string.confirm),
                        () -> {
                            GlideCatchUtil.getInstance().clearCacheMemory(getContext());
                            GlideCatchUtil.getInstance().clearCacheDiskSelf(getContext());
                            new XPopup.Builder(getContext())
                                    .isDestroyOnDismiss(true)
                                    .asLoading(null, R.layout.view_loading)
                                    .setTitle(getString(R.string.clearing_cache)).show()
                                    .delayDismissWith(1200, () -> {
                                        initCacheSize();
                                        ToastUtils.show(R.string.successfully_clear_cache);
                                    });
                        }, null, false, 0)
                .show();
    }

    /**
     * 点击关于软件
     */
    @OnClick(R.id.about_software_layout)
    public void onAboutSoftware() {
        AboutSoftwareActivity.startActivity(getContext());
    }

}