package com.easyshare.fragment.user;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.easyshare.R;
import com.easyshare.adapter.SelectClassificationAdapter;
import com.easyshare.base.BaseDataBean;
import com.easyshare.base.BaseException;
import com.easyshare.base.BaseFragment;
import com.easyshare.base.RxjavaThrowable;
import com.easyshare.entity.ClassificationEntity;
import com.easyshare.network.Constants;
import com.easyshare.network.RetrofitFactory;
import com.easyshare.utils.UserUtils;
import com.hjq.toast.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SelectClassificationFragment extends BaseFragment implements SelectClassificationAdapter.OnClickListener {

    private SelectClassificationViewModel mViewModel;

    @BindView(R.id.RecyclerView)
    RecyclerView mRecyclerView;
    SelectClassificationAdapter mSelectClassificationAdapter;
    @BindView(R.id.title)
    TextView mTitleTextView;
    @BindView(R.id.submit_btn)
    Button mSubmitButton;

    public static SelectClassificationFragment newInstance() {
        return new SelectClassificationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_classification, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SelectClassificationViewModel.class);
        mSelectClassificationAdapter = new SelectClassificationAdapter(mViewModel.getClassificationListData());
        mSelectClassificationAdapter.setOnClickListener(this);
        mRecyclerView.setAdapter(mSelectClassificationAdapter);
        initClassificationData();
        mViewModel.observeSelectData(this, list -> {
            if (list.size() != 0 && !mViewModel.isInitCount()) mViewModel.initCount();
            if (mViewModel.isInitCount())
                mTitleTextView.setText(getString(R.string.select_classification_title_formart, list.size()));
            mSubmitButton.setEnabled(list.size() != 0);
        });
    }

    /**
     * 加载跳过按钮
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_select_classification_nav_bottom, menu);
    }

    /**
     * 点击发布按钮 -- 标题
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (R.id.nav_publish == item.getItemId()) {
            getActivity().finish();
            return true;
        } else return super.onOptionsItemSelected(item);
    }

    /**
     * 点击提交
     */
    @OnClick(R.id.submit_btn)
    public void onSubmitClick() {
        mSubmitButton.setEnabled(false);
        StringBuffer classificationIds = new StringBuffer();
        for (ClassificationEntity entity : mViewModel.getSelectData()) {
            classificationIds.append(entity.getClassificationId()).append(",");
        }
        classificationIds.deleteCharAt(classificationIds.length() - 1);
        Log.e(TAG, "onSubmitClick: " + classificationIds.toString() );
        Disposable subscribe = RetrofitFactory.getsInstance(getContext())
                .setClassificationPreferences(classificationIds.toString())
                .subscribeOn(Schedulers.io()) // 子线程执行方法
                .observeOn(AndroidSchedulers.mainThread()) // 主线程回调
                .subscribe(resp -> {   // 成功回调
                    if (resp.getCode() == 0) {
                        ToastUtils.show(R.string.successfully_setting);
                        getActivity().finish();
                        // 发送通知，那边更新分类
                        EventBus.getDefault().post(BaseDataBean.build(Constants.UPDATE_USER_PREFERENCES));
                    } else {
                        throw new BaseException(resp.getMsg());
                    }
                    mSubmitButton.setEnabled(true);
                }, (RxjavaThrowable) throwable -> {   // 出错回调
                    mSubmitButton.setEnabled(true);
                });
        mDisposables.add(subscribe);
    }

    /**
     * 初始化分类数据
     */
    private void initClassificationData() {
        Disposable subscribe = RetrofitFactory.getsInstance(getContext())
                .getClassificationList()
                .subscribeOn(Schedulers.io()) // 子线程执行方法
                .observeOn(AndroidSchedulers.mainThread()) // 主线程回调
                .subscribe(resp -> {   // 成功回调
                    if (resp.getCode() == 0) {
                        mSelectClassificationAdapter.notifyDataSetChanged();
                        mViewModel.setClassificationListData(resp.getData());
                    } else {
                        throw new BaseException(resp.getMsg());
                    }
                }, (RxjavaThrowable) throwable -> {   // 出错回调
                });
        mDisposables.add(subscribe);
    }

    /**
     * 点击分类
     */
    @Override
    public void onClickItemListener(CardView view, ClassificationEntity entity, int position) {
        if (mViewModel.getSelectData().contains(entity)) { // 取消
            view.setCardBackgroundColor(Color.parseColor("#F3F3F3"));
            mViewModel.removeSelectData(entity);
        } else { // 选择
            // 最多选6个
            if (mViewModel.getSelectData().size() >= 6) {
                ToastUtils.show(getString(R.string.error_select_classification_over_maximum));
                return;
            }
            view.setCardBackgroundColor(Color.parseColor("#E3F3F3"));
            mViewModel.addSelectData(entity);
        }
    }
}