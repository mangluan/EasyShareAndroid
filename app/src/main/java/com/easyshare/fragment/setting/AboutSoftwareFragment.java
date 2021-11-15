package com.easyshare.fragment.setting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.easyshare.R;
import com.easyshare.activity.WebActivity;
import com.easyshare.base.BaseFragment;
import com.lxj.xpopup.XPopup;
import com.tencent.bugly.beta.Beta;

import butterknife.BindView;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class AboutSoftwareFragment extends BaseFragment {

    private AboutSoftwareViewModel mViewModel;

    @BindView(R.id.version_text)
    TextView mVersionTextView;

    public static AboutSoftwareFragment newInstance() {
        return new AboutSoftwareFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about_software, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AboutSoftwareViewModel.class);
        mVersionTextView.setText(getAppVersion(getContext()));
    }

    /**
     * 点击关于作者
     */
    @OnClick(R.id.about_author_layout)
    public void onAboutAuthorClick() {
        new XPopup.Builder(getContext())
                .asConfirm(null, null, null, null,
                        null, null, false, R.layout.layout_author_loading)
                .show();
    }

    /**
     * 点击检查更新
     */
    @OnClick(R.id.check_updates_layout)
    public void onCheckUpdatesClick() {
        Beta.checkAppUpgrade();
    }

    /**
     * 点击用户协议
     */
    @OnClick(R.id.user_agreement_layout)
    public void onUserAgreementClick() {
        WebActivity.startActivity(getContext(), "file:android_asset/用户协议.html");
    }

    /**
     * 获取应用版本号
     */
    public String getAppVersion(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return getString(R.string.text_null);
        }
    }

}