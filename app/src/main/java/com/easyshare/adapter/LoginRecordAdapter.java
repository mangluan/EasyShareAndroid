package com.easyshare.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.easyshare.R;
import com.easyshare.entity.LoginRecordEntity;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.samlss.broccoli.Broccoli;

public class LoginRecordAdapter extends RecyclerView.Adapter<LoginRecordAdapter.ViewHolder> {

    private final static int NUMBER_OF_DEFAULT_PLACEHOLDERS = 10;

    private List<LoginRecordEntity> mList;

    public LoginRecordAdapter(List<LoginRecordEntity> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_line, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 预加载
        if (getItemViewType(position) == 0) {
            holder.mBroccoli.show();
        } else {
            holder.mBroccoli.clearAllPlaceholders();
            // 数据填充
            holder.mTitleTextView.setText(mList.get(position).getSource());
            if (getItemViewType(position) == 2) {
                holder.mSubheadingTextView.setText("本机");
            } else { // 时间处理一下  过滤掉当年
                String loginTime = mList.get(position).getLoginTime();
                loginTime = loginTime.replace(Calendar.getInstance().get(Calendar.YEAR) + "年", "");
                holder.mSubheadingTextView.setText(loginTime);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() == 0 ? NUMBER_OF_DEFAULT_PLACEHOLDERS : mList.size();
    }

    /**
     * @return -
     * 0   ： 预加载 ： 无数据，占位
     * 1   ： 正常项
     * 2   ： 第一项
     */
    @Override
    public int getItemViewType(int position) {
        if (mList.size() == 0 && getItemCount() == NUMBER_OF_DEFAULT_PLACEHOLDERS) {
            return 0;
        } else if (position == 0) return 2;
        else return 1;
    }

    @SuppressLint("NonConstantResourceId")
    public class ViewHolder extends RecyclerView.ViewHolder {

        Broccoli mBroccoli;

        @BindView(R.id.item_title)
        TextView mTitleTextView;
        @BindView(R.id.item_subheading)
        TextView mSubheadingTextView;
        @BindView(R.id.item_image)
        ImageView mImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mBroccoli = new Broccoli();
            mBroccoli.addPlaceholders(mTitleTextView, mSubheadingTextView, mImageView);
        }

    }

}

