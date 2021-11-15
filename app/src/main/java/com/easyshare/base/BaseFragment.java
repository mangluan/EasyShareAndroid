package com.easyshare.base;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.easyshare.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.Disposable;

public class BaseFragment extends Fragment {


    protected List<Disposable> mDisposables = new ArrayList<>();
    private Unbinder unbinder;

    protected static final String TAG = "BaseFragment";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // init butter knife
        unbinder = ButterKnife.bind(this, view);
        // init event bus
        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // init toolbar
        try {
            BaseActivity activity = (BaseActivity) getActivity();
            activity.setSupportActionBar(getView().findViewById(R.id.toolbar));
            setHasOptionsMenu(true);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            String title = getActivity().getIntent().getStringExtra("title");
            if (!TextUtils.isEmpty(title))
                activity.getSupportActionBar().setTitle(title);
        } catch (Exception ignored) {
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // 图片比例不理想，弄动画不好看
//            ImageView image = getView().findViewById(R.id.image);
//            if (image != null && image.getVisibility() == View.VISIBLE){
//                getActivity().finishAfterTransition();
//            }else {
//                getActivity().finish();
//            }
            getActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDisposables != null && mDisposables.size() != 0) { // 统一释放资源
            for (Disposable disposable : mDisposables) {
                if (!disposable.isDisposed()) {
                    disposable.dispose();
                }
            }
        }
        if (unbinder != null) unbinder.unbind();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

}
