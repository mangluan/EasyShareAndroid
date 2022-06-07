package com.easyshare.fragment.album;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.easyshare.R;
import com.easyshare.base.BaseFragment;
import com.example.library.AutoFlowLayout;
import com.example.library.FlowAdapter;
import com.hjq.toast.ToastUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class SearchFragment extends BaseFragment {

    private SearchViewModel mViewModel;

    @BindView(R.id.flow_layout)
    AutoFlowLayout<String> mAutoFlowLayout;
    List<String> mSearchHistoryData;
    @BindView(R.id.search_edit)
    EditText mSearchEditText;

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        initView();
    }

    private void initView() {
        initFlowView();
        mSearchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (event.getAction() == EditorInfo.IME_ACTION_SEARCH){
                String searchKey = mSearchEditText.getText().toString().trim();
                if (TextUtils.isEmpty(searchKey)) {
                    ToastUtils.show("请输入搜索关键字");
                    return false;
                }
                // TODO
                ToastUtils.show("没有搜索到任务内容哦");
                mSearchHistoryData.add(0, searchKey);
                return true;
            }
            return false;
        });
    }

    private void initFlowView() {
        mSearchHistoryData  = new ArrayList<>();
        Collections.addAll(mSearchHistoryData, "机械键盘", "iOS 手游", "绊爱", "鼠标", "运动套装", "卡西欧手表", "重庆");
        Collections.addAll(mSearchHistoryData, "今天是个好日子", "北美洲的小企鹅", "企鹅", "番茄炒蛋 菜谱", "菜谱", "阿巴阿巴");
        mAutoFlowLayout.setAdapter(new FlowAdapter<String>(mSearchHistoryData) {
            @Override
            public View getView(int position) {
                View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_search_history, mAutoFlowLayout, false);
                ((TextView) itemView.findViewById(R.id.text)).setText(mSearchHistoryData.get(position));
                return itemView;
            }
        });
        // 点击事件 TODO
        mAutoFlowLayout.setOnItemClickListener((position, view) -> {
//            data.remove(position);
            mAutoFlowLayout.deleteView(position);
        });
    }

    @BindView(R.id.search_history_state)
    TextView mSearchHistoryStateTextView;

    /**
     * 点击 显示更多搜索历史
     */
    @OnClick(R.id.search_history_state)
    public void onSearchHistoryStateClick() {
        if ("hide".equals(mSearchHistoryStateTextView.getTag().toString())) {
            // 隐藏态，转为多行显示
            mAutoFlowLayout.setMaxLines(6);
            mSearchHistoryStateTextView.setText(R.string.search_history_state_hide);
            mSearchHistoryStateTextView.setTag("show");
            // TODO 这个View目前从多变少有问题，这里直接隐藏
            mSearchHistoryStateTextView.setVisibility(View.INVISIBLE);
        } else { // 显示态，转为3行显示
            mAutoFlowLayout.setMaxLines(3);
            mSearchHistoryStateTextView.setText(R.string.search_history_state_show);
            mSearchHistoryStateTextView.setTag("hide");
        }
        ToastUtils.show(mAutoFlowLayout.hasMoreData() + "");
    }

    /**
     * 点击取消搜索
     */
    @OnClick(R.id.cancel_btn)
    public void onCancelClick(){
        getActivity().finish();
    }

}