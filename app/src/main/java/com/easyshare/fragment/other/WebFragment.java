package com.easyshare.fragment.other;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.easyshare.R;
import com.easyshare.base.BaseFragment;
import com.easyshare.utils.StringUtils;
import com.hjq.toast.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import butterknife.BindView;

@SuppressLint("NonConstantResourceId")
public class WebFragment extends BaseFragment implements ViewTreeObserver.OnGlobalLayoutListener {

    private static final String WEB_URL = "web url";

    private WebViewModel mViewModel;

    @BindView(R.id.web_view)
    WebView mWebView;
    @BindView(R.id.SmartRefreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    public static WebFragment newInstance(String webURL) {
        WebFragment fragment = new WebFragment();
        Bundle args = new Bundle();
        args.putString(WEB_URL, webURL);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_web, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(WebViewModel.class);
        mViewModel.setWebURL(getArguments().getString(WEB_URL));
        mViewModel.observeWebURL(this, url -> {
            // 解析url
            if (url.startsWith("file:android_asset/") || url.startsWith("http")) {
                // 加载页面
                mWebView.loadUrl(url);
            } else {
                ToastUtils.show(getString(R.string.error_web_source));
                getActivity().finish();
            }
        });
        // 刷新网页
        mSmartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            mViewModel.setWebURL(mViewModel.getWebURL());
            mSmartRefreshLayout.finishRefresh();
        });
        mSmartRefreshLayout.getRefreshHeader().getView().getViewTreeObserver().addOnGlobalLayoutListener(this);
        // web view 添加监听
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mToolbar.setTitle(view.getTitle());
            }
        });
    }

    @Override
    public void onGlobalLayout() {
        if (mSmartRefreshLayout != null) {
            View srl_classics_update = mSmartRefreshLayout.findViewById(R.id.srl_classics_update);
            if (srl_classics_update instanceof TextView) {
                String url = mViewModel.getWebURL();
                Log.e(TAG, "onGlobalLayout:  update title" );
                if (url.startsWith("file:android_asset/")) {
                    ((TextView) srl_classics_update).setText(getString(R.string.web_source, getText(R.string.app_name)));
                } else {
                    String domain = StringUtils.getDomainHost(url);
                    if (domain != null) {
                        ((TextView) srl_classics_update).setText(getString(R.string.web_source, domain));
                    }
                }
            } // end instanceof
        } // end if srl !=null
    } // end method
}