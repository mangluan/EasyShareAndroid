package com.easyshare.fragment.user;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.easyshare.R;
import com.easyshare.activity.LoginRecordActivity;
import com.easyshare.base.BaseFragment;
import com.easyshare.entity.UserInfoEntity;
import com.easyshare.utils.UserUtils;
import com.hjq.toast.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class AccountSecurityFragment extends BaseFragment {

    private AccountSecurityViewModel mViewModel;

    @BindView(R.id.mail_text)
    TextView mMailTextView;

    public static AccountSecurityFragment newInstance() {
        return new AccountSecurityFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account_security, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AccountSecurityViewModel.class);
        UserInfoEntity userInfo = UserUtils.getsInstance().getUserInfo();
        if (userInfo != null) {
            String[] split = userInfo.getEmail().split("@");
            mMailTextView.setText(split[0].length() > 3 ? split[0].substring(0, 3) + "***@" + split[1] : userInfo.getEmail());
        } 
    }

    /**
     * 点击个人信息
     */
    @OnClick(R.id.user_info_layout)
    public void onUserInfoClick() {
        ToastUtils.show("个人信息");
    }

    /**
     * 点击修改密码
     */
    @OnClick(R.id.modify_password_layout)
    public void onModifyPasswordClick() {
        ToastUtils.show("修改密码");
    }

    /**
     * 点击绑定邮箱
     */
    @OnClick(R.id.bind_mailbox_layout)
    public void onBindMailboxClick() {
        ToastUtils.show("绑定邮箱");
    }

    /**
     * 点击登录记录
     */
    @OnClick(R.id.login_record_layout)
    public void onLoginRecordClick() {
        LoginRecordActivity.startActivity(getContext());
    }


}