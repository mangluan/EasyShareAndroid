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

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import me.samlss.broccoli.Broccoli;

public class ExploreRecommendAdapter extends RecyclerView.Adapter<ExploreRecommendAdapter.ViewHolder> {

    private final  static  int NUMBER_OF_DEFAULT_PLACEHOLDERS = 3;
    private List<Object> mList;

    public ExploreRecommendAdapter(List<Object> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_explore_complicated, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 预加载
        if (getItemViewType(position) == 0) {
            holder.mBroccoli.show();
        } else {
            holder.mBroccoli.clearAllPlaceholders();
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
     */
    @Override
    public int getItemViewType(int position) {
        if (mList.size() == 0 && getItemCount() == NUMBER_OF_DEFAULT_PLACEHOLDERS) {
            return 0;
        } else return 1;
    }

    @SuppressLint("NonConstantResourceId")
    public class ViewHolder extends RecyclerView.ViewHolder {

        Broccoli mBroccoli;

        @BindView(R.id.head_image)
        ImageView mHeadImageView;
        @BindView(R.id.name_tv)
        TextView mNameTextView;
        @BindView(R.id.time_tv)
        TextView mTimeTextView;

        @BindView(R.id.content_tv)
        TextView mContentTextView;

        @BindViews({R.id.image_1,R.id.image_2,R.id.image_3,R.id.image_4,R.id.image_5,R.id.image_6})
        ImageView[] mImageView;

        @BindView(R.id.share_btn)
        TextView mShareButton;
        @BindView(R.id.comment_btn)
        TextView mCommentButton;
        @BindView(R.id.like_btn)
        TextView mLikeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mBroccoli = new Broccoli();
            mBroccoli.addPlaceholders(mHeadImageView, mNameTextView, mTimeTextView,mContentTextView);
            mBroccoli.addPlaceholders(mImageView);
            mBroccoli.addPlaceholders(mShareButton,mCommentButton,mLikeButton);
        }
    }


    /**
     * 点击接口  供外部调用
     */
    public interface OnClickListener {
        void onClickItemListener(Object entity);

        void onClickDeleteListener(Object entity);
    }

    private OnClickListener mOnClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

}

