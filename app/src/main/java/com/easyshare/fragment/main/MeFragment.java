package com.easyshare.fragment.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.easyshare.activity.LoginActivity;
import com.easyshare.base.BaseDataBean;
import com.easyshare.base.BaseFragment;
import com.easyshare.entity.UserInfoEntity;
import com.easyshare.network.Constants;
import com.easyshare.utils.GlideCatchUtil;
import com.easyshare.utils.SharedPreferenceUtils;
import com.easyshare.utils.UserUtils;
import com.hjq.toast.ToastUtils;
import com.lxj.xpopup.XPopup;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

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
    }

    /**
     * 缓存大小刷新
     */
    @Override
    public void onResume() {
        super.onResume();
        initCacheSize();
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
     * 用户登录
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogin(BaseDataBean<UserInfoEntity> dataBean) {
        if (dataBean.getCode() == Constants.LOGIN_SUCCESSFULLY) {
            UserInfoEntity user = dataBean.getData();
            if (user != null) { // 填充用户信息
                mLogoutButton.setVisibility(View.VISIBLE);
                Glide.with(this).load(user.getAvatarImage()).into(mAvatarImageView);
                mUserNameTextView.setText(user.getName());
                // 如果有签名，则显示签名，如果没有签名则显示邮件
                mTitleSubheadingTextView.setText(TextUtils.isEmpty(user.getSign()) ? user.getEmail() : user.getSign());
                // TODO 整合本地记录
                mBrowsingCountTextView.setText(String.valueOf(user.getBrowsingCount()));
                mAttentionCountTextView.setText(String.valueOf(user.getAttentionCount()));
                mFansCountTextView.setText(String.valueOf(user.getFansCount()));
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
            Glide.with(this).load(R.drawable.img_default_avatar).into(mAvatarImageView);
            mUserNameTextView.setText(R.string.not_login_name);
            mTitleSubheadingTextView.setText(R.string.not_login_subheading);
            // TODO 直接加载本地记录
            mBrowsingCountTextView.setText(R.string.text_zero);
            mAttentionCountTextView.setText(R.string.text_null);
            mFansCountTextView.setText(R.string.text_null);
        }
    }

    /**
     * 退出登录按钮
     */
    @OnClick(R.id.logout_btn)
    public void onLogoutClick() {
        new XPopup.Builder(getContext())
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
        ToastUtils.show("关注");
    }

    /**
     * 点击粉丝按钮
     */
    @OnClick(R.id.fans_button)
    public void onFansClick() {
        ToastUtils.show("粉丝");
    }

    /**
     * 点击点赞按钮
     */
    @OnClick(R.id.like_button)
    public void onLikeClick() {
        ToastUtils.show("点赞");
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
        AccountSecurityActivity.startActivity(getContext());
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
                .asConfirm(null, getString(R.string.clear_cache_hint), getString(R.string.cancel), getString(R.string.confirm),
                        () -> {
                            GlideCatchUtil.getInstance().clearCacheMemory(getContext());
                            GlideCatchUtil.getInstance().clearCacheDiskSelf(getContext());
                            new XPopup.Builder(getContext()).asLoading(null, R.layout.view_loading)
                                    .setTitle(getString(R.string.clear_cacheing)).show().delayDismissWith(1200, () -> {
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
    public void onAboutSoftware(){
        AboutSoftwareActivity.startActivity(getContext());
    }

}