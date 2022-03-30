package com.easyshare.fragment.user;

import static com.easyshare.fragment.user.LoginViewModel.ActionState.AUTH_CODE_LOGIN;
import static com.easyshare.fragment.user.LoginViewModel.ActionState.PASSWORD_LOGIN;
import static com.easyshare.fragment.user.LoginViewModel.ActionState.WAITING_INPUT_MAIL;
import static com.easyshare.fragment.user.LoginViewModel.ShowState.HIDDEN;
import static com.easyshare.fragment.user.LoginViewModel.ShowState.SHOW;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.lifecycle.ViewModelProvider;
import androidx.transition.ChangeBounds;
import androidx.transition.Transition;
import androidx.transition.TransitionListenerAdapter;
import androidx.transition.TransitionManager;

import com.easyshare.R;
import com.easyshare.activity.ImproveUserDataActivity;
import com.easyshare.activity.WebActivity;
import com.easyshare.base.BaseFragment;
import com.easyshare.base.RxjavaThrowable;
import com.easyshare.base.SimpleTextWatcher;
import com.easyshare.network.Constants;
import com.easyshare.network.RetrofitFactory;
import com.easyshare.utils.SharedPreferenceUtils;
import com.easyshare.utils.StringUtils;
import com.easyshare.utils.UserUtils;
import com.hjq.toast.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 登录注册页
 */
@SuppressLint("NonConstantResourceId")
public class LoginFragment extends BaseFragment {

    private LoginViewModel mViewModel;

    private ConstraintSet mConstraintSet;
    @BindView(R.id.rootConstraintLayout)
    ConstraintLayout mRootConstraintLayout;

    @BindView(R.id.login_title)
    TextView mTitleTextView;
    @BindView(R.id.login_subheading)
    TextView mSubheadingTextView;
    @BindView(R.id.mail_edit)
    EditText mMailEditText;
    @BindView(R.id.clear_mail_btn)
    ImageView mClearMailButton;
    @BindView(R.id.auth_code_edit)
    EditText mAuthCodeEditText;
    @BindView(R.id.password_edit)
    EditText mPasswordEditText;
    @BindView(R.id.password_switch_btn)
    ImageView mPasswordSwitchButton;
    @BindView(R.id.submit)
    Button mSubmitButton;
    @BindView(R.id.transition_login_btn)
    TextView mTransitionLoginButton;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        // 初始化必要参数
        mConstraintSet = new ConstraintSet();
        mConstraintSet.clone(mRootConstraintLayout);
        String lastLoginMail = SharedPreferenceUtils.getString(getContext(), Constants.USER_MAIL);
        if (!TextUtils.isEmpty(lastLoginMail)) {
            mMailEditText.setText(lastLoginMail);
            mViewModel.setMail(lastLoginMail);
        }
        initListener();
        initObserver();
    }

    /**
     * 初始化监听
     */
    private void initListener() {
        // 邮件输入监听
        mMailEditText.addTextChangedListener((SimpleTextWatcher) (text, start, before, count) -> {
            // 如果在输入验证码状态时候改变了邮件，那么则重新发送邮件
            if (mViewModel.getActionState() == AUTH_CODE_LOGIN) {
                mViewModel.setActionState(WAITING_INPUT_MAIL);
            }
            mViewModel.setMail(text.toString());
        });
        // 验证码输入监听
        mAuthCodeEditText.addTextChangedListener((SimpleTextWatcher) (text, start, before, count) -> {
            if (mViewModel.getActionState() == AUTH_CODE_LOGIN) {
                mSubmitButton.setEnabled(text.length() == 6);
            }
        });
        // 密码输入监听
        mPasswordEditText.addTextChangedListener((SimpleTextWatcher) (text, start, before, count) -> {
            if (mViewModel.getActionState() == PASSWORD_LOGIN) {
                mSubmitButton.setEnabled(text.length() >= 6);
            }
        });
        // 邮件焦点监听
        mMailEditText.setOnFocusChangeListener((view, hasFocus) -> {
            mClearMailButton.setVisibility(hasFocus && !TextUtils.isEmpty(mMailEditText.getText().toString()) ? View.VISIBLE : View.INVISIBLE);
        });
    }

    /**
     * 初始化页面数据监听
     */
    private void initObserver() {
        // mail有变化的时候
        mViewModel.observeMail(this, mail -> {
            mClearMailButton.setVisibility(TextUtils.isEmpty(mail) ? View.INVISIBLE : View.VISIBLE);
            if (mViewModel.getActionState() != PASSWORD_LOGIN) // 不是密码登录
                mSubmitButton.setEnabled(StringUtils.isEmail(mail));
        });
        // 页面状态切换
        mViewModel.observeActionState(this, state -> {
            ChangeBounds changeBounds = new ChangeBounds();
            changeBounds.setDuration(300);
            switch (state) {
                case WAITING_INPUT_MAIL:
                    // 隐藏验证码、密码输入框
                    if (mAuthCodeEditText.getVisibility() == View.VISIBLE)
                        mAuthCodeEditText.setVisibility(View.INVISIBLE);
                    else if (mPasswordEditText.getVisibility() == View.VISIBLE) {
                        mPasswordEditText.setVisibility(View.INVISIBLE);
                        mPasswordSwitchButton.setVisibility(View.INVISIBLE);
                    } else return; // 两个都没有，说明不需要改变
                    mConstraintSet.connect(mSubmitButton.getId(), ConstraintSet.TOP, mMailEditText.getId(), ConstraintSet.BOTTOM);
                    mConstraintSet.connect(R.id.transition_login_btn, ConstraintSet.TOP, mMailEditText.getId(), ConstraintSet.BOTTOM);
                    TransitionManager.beginDelayedTransition(mRootConstraintLayout, changeBounds);
                    mConstraintSet.applyTo(mRootConstraintLayout);
                    // 更改页面显示
                    mTitleTextView.setText(R.string.mail_code_login);
                    mSubheadingTextView.setText(R.string.mail_code_login_hint);
                    mSubmitButton.setText(R.string.send_auth_code);
                    mViewModel.setPasswordShowState(HIDDEN);
                    mSubmitButton.setEnabled(StringUtils.isEmail(mMailEditText.getText().toString()));
                    break;
                case AUTH_CODE_LOGIN:
                    // 显示验证码登陆框
                    mAuthCodeEditText.setText(null);
                    mConstraintSet.connect(mSubmitButton.getId(), ConstraintSet.TOP, mAuthCodeEditText.getId(), ConstraintSet.BOTTOM);
                    mConstraintSet.connect(R.id.transition_login_btn, ConstraintSet.TOP, mAuthCodeEditText.getId(), ConstraintSet.BOTTOM);
                    changeBounds.addListener(new TransitionListenerAdapter() {
                        @Override
                        public void onTransitionEnd(@NonNull Transition transition) {
                            super.onTransitionEnd(transition);
                            mAuthCodeEditText.setVisibility(View.VISIBLE);
                            mAuthCodeEditText.requestFocus();
                        }
                    });
                    TransitionManager.beginDelayedTransition(mRootConstraintLayout, changeBounds);
                    mConstraintSet.applyTo(mRootConstraintLayout);
                    // 更改页面显示
                    mSubmitButton.setText(getString(R.string.login));
                    break;
                case PASSWORD_LOGIN:
                    // 显示密码登陆框
                    mPasswordEditText.setText(null);
                    if (mAuthCodeEditText.getVisibility() == View.VISIBLE) { // 验证码的框还在，直接显示
                        mAuthCodeEditText.setVisibility(View.INVISIBLE);
                        mPasswordEditText.setVisibility(View.VISIBLE);
                        mPasswordSwitchButton.setVisibility(View.VISIBLE);
                        mPasswordEditText.requestFocus();
                    } else { // 添加动画后显示
                        mConstraintSet.connect(mSubmitButton.getId(), ConstraintSet.TOP, mPasswordEditText.getId(), ConstraintSet.BOTTOM);
                        mConstraintSet.connect(R.id.transition_login_btn, ConstraintSet.TOP, mPasswordEditText.getId(), ConstraintSet.BOTTOM);
                        changeBounds.addListener(new TransitionListenerAdapter() {
                            @Override
                            public void onTransitionEnd(@NonNull Transition transition) {
                                super.onTransitionEnd(transition);
                                mPasswordEditText.setVisibility(View.VISIBLE);
                                mPasswordSwitchButton.setVisibility(View.VISIBLE);
                                if (StringUtils.isEmail(mMailEditText.getText().toString())) {
                                    mPasswordEditText.requestFocus(); // 如果帐号已经输入完毕了，密码获取焦点
                                } else {
                                    mMailEditText.requestFocus();
                                }
                            }
                        });
                        TransitionManager.beginDelayedTransition(mRootConstraintLayout, changeBounds);
                        mConstraintSet.applyTo(mRootConstraintLayout);
                    }
                    // 更改页面显示
                    mTitleTextView.setText(R.string.mail_password_login);
                    mSubheadingTextView.setText(null);
                    mSubmitButton.setText(R.string.login);
                    break;
            }
        });
        // 密码显示状态
        mViewModel.observePasswordShowState(this, showState -> {
            switch (showState) {
                case SHOW:
                    mPasswordEditText.setInputType(145);
                    mPasswordEditText.setSelection(mPasswordEditText.getText().toString().length());
                    mPasswordSwitchButton.setImageDrawable(getContext().getDrawable(R.drawable.ic_password_show));
                    break;
                case HIDDEN:
                    mPasswordEditText.setInputType(129);
                    mPasswordEditText.setSelection(mPasswordEditText.getText().toString().length());
                    mPasswordSwitchButton.setImageDrawable(getContext().getDrawable(R.drawable.ic_password_hidden));
                    break;
            }
        });
    }

    /**
     * 清除 Mail EditText 内容按钮
     */
    @OnClick(R.id.clear_mail_btn)
    public void onClearMailContent() {
        mMailEditText.setText(null);
    }

    /**
     * 点击 密码\验证码 切换按钮
     */
    @OnClick(R.id.transition_login_btn)
    public void onTransitionLoginState() {
        switch (mViewModel.getActionState()) {
            case WAITING_INPUT_MAIL:
            case AUTH_CODE_LOGIN:
                mViewModel.setActionState(PASSWORD_LOGIN);
                break;
            case PASSWORD_LOGIN:
                mViewModel.setActionState(WAITING_INPUT_MAIL);
                break;
        }
    }

    /**
     * 点击切换密码显示
     */
    @OnClick(R.id.password_switch_btn)
    public void onPasswordSwitch() {
        if (mViewModel.getPasswordShowState() == SHOW) {
            mViewModel.setPasswordShowState(HIDDEN);
        } else {
            mViewModel.setPasswordShowState(SHOW);
        }
    }

    /**
     * 点击用户协议
     */
    @OnClick(R.id.user_agreement_btn)
    public void onUserAgreementClick() {
        WebActivity.startActivity(getContext(), "file:android_asset/用户协议.html");
    }

    /**
     * 点击提交按钮
     */
    @OnClick(R.id.submit)
    public void onSubmit() {
        mSubmitButton.setEnabled(false); // 防止重复点击
        String mail = mMailEditText.getText().toString().trim();
        Disposable subscribe = null;
        switch (mViewModel.getActionState()) {
            case WAITING_INPUT_MAIL: // 发送验证码
                subscribe = RetrofitFactory.getsInstance(getContext())
                        .sendAuthCode(mail)
                        .subscribeOn(Schedulers.io()) // 子线程执行方法
                        .observeOn(AndroidSchedulers.mainThread()) // 主线程回调
                        .subscribe(resp -> {   // 成功回调
                            if (resp.getCode() == 0) {
                                ToastUtils.show(R.string.successfully_send_auth_code);
                                mViewModel.setActionState(AUTH_CODE_LOGIN);
                            } else {
                                ToastUtils.show(resp.getMsg());
                            }
                            mSubmitButton.setEnabled(true);
                        }, (RxjavaThrowable) throwable -> {   // 出错回调
                            mSubmitButton.setEnabled(true);
                        });
                break;
            case AUTH_CODE_LOGIN: // 验证码登录
                subscribe = RetrofitFactory.getsInstance(getContext())
                        .loginForMail(mail, mAuthCodeEditText.getText().toString().trim())
                        .subscribeOn(Schedulers.io()) // 子线程执行方法
                        .observeOn(AndroidSchedulers.mainThread()) // 主线程回调
                        .subscribe(resp -> {   // 成功回调
                            ToastUtils.show(resp.getMsg());
                            if (resp.getCode() == 0) {
                                // 保存登录帐号
                                SharedPreferenceUtils.putString(getContext(), Constants.USER_MAIL, mail);
                                // 保存所有用户信息
                                UserUtils.getsInstance().login(resp.getData());
                                if (getString(R.string.successfully_registered).equals(resp.getMsg())) {
                                    // 注册成功提醒用户设置一些参数
                                    ImproveUserDataActivity.startActivity(getContext());
                                } // 关闭页面
                                getActivity().finish();
                            }
                            mSubmitButton.setEnabled(true);
                        }, (RxjavaThrowable) throwable -> {   // 出错回调
                            mSubmitButton.setEnabled(true);
                        });
                break;
            case PASSWORD_LOGIN: // 密码登录
                subscribe = RetrofitFactory.getsInstance(getContext())
                        .loginForPassword(mail, mPasswordEditText.getText().toString().trim())
                        .subscribeOn(Schedulers.io()) // 子线程执行方法
                        .observeOn(AndroidSchedulers.mainThread()) // 主线程回调
                        .subscribe(resp -> {   // 成功回调
                            ToastUtils.show(resp.getMsg());
                            if (resp.getCode() == 0) {
                                // 保存登录帐号
                                SharedPreferenceUtils.putString(getContext(), Constants.USER_MAIL, mail);
                                // 保存所有用户信息
                                UserUtils.getsInstance().login(resp.getData());
                                // 关闭页面
                                getActivity().finish();
                                // 进入测试页面 : 注册后进入的初始化用户信息页
//                                ImproveUserDataActivity.startActivity(getContext());
                                mSubmitButton.setEnabled(true);
                            }
                        }, (RxjavaThrowable) throwable -> {   // 出错回调
                            mSubmitButton.setEnabled(true);
                        });
                break;
        }
        mDisposables.add(subscribe);
    }

}