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
import com.easyshare.entity.ClassificationEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.samlss.broccoli.Broccoli;

public class ClassificationAdapter extends RecyclerView.Adapter<ClassificationAdapter.ViewHolder> {

    private final static int NUMBER_OF_DEFAULT_PLACEHOLDERS = 10;

    private List<ClassificationEntity> mList;

    public ClassificationAdapter(List<ClassificationEntity> mList) {
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
            holder.mTitleTextView.setText(mList.get(position).getName());
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
            itemView.setOnClickListener(view -> {
                if (mOnClickListener != null) {
                    mOnClickListener.onClickItemListener(mList.get(getAbsoluteAdapterPosition()), getAbsoluteAdapterPosition());
                }
            });
        }

    }

    /**
     * 点击接口  供外部调用
     */
    public interface OnClickListener {
        void onClickItemListener(ClassificationEntity entity, int position);
    }

    private OnClickListener mOnClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

}

