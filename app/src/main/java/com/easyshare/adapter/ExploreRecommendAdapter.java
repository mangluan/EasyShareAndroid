package com.easyshare.adapter;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.easyshare.R;
import com.easyshare.entity.PhotoAlbumEntity;
import com.easyshare.entity.PictureEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import me.samlss.broccoli.Broccoli;

public class ExploreRecommendAdapter extends RecyclerView.Adapter<ExploreRecommendAdapter.ViewHolder> {

    private final static int NUMBER_OF_DEFAULT_PLACEHOLDERS = 3;
    private List<PhotoAlbumEntity> mList;

    public ExploreRecommendAdapter(List<PhotoAlbumEntity> mList) {
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
            // 数据填充
            PhotoAlbumEntity albumEntity = mList.get(position);
            // 用户
            Glide.with(holder.itemView).load(albumEntity.getUserEntity().getAvatarImage())
                    .transition(withCrossFade()).into(holder.mAvatarImageView);
            holder.mNameTextView.setText(albumEntity.getUserEntity().getName());
            holder.mTimeTextView.setText(albumEntity.getTime());
            // 图册
            holder.mContentTextView.setText(albumEntity.getTitle());
            List<PictureEntity> pictureEntities = albumEntity.getPictureEntities();
            holder.setImages(pictureEntities, holder.itemView);
            // 点赞
            Drawable drawable = ContextCompat.getDrawable(holder.itemView.getContext(),
                    albumEntity.isLike() ? R.drawable.ic_like_selected_18 : R.drawable.ic_like_18);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            holder.mLikeButton.setCompoundDrawables(drawable, null, null, null);
            holder.mLikeButton.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),
                    albumEntity.isLike() ? R.color.colorPrimary : R.color.black_translucence));
            // 其他
            if (albumEntity.getCommentCount() != 0)
                holder.mCommentButton.setText(String.valueOf(albumEntity.getCommentCount()));
            else
                holder.mCommentButton.setText(R.string.comment);
            if (albumEntity.getLikeCount() != 0)
                holder.mLikeButton.setText(String.valueOf(albumEntity.getLikeCount()));
            else
                holder.mLikeButton.setText(R.string.like);
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

        @BindView(R.id.avatar_image)
        ImageView mAvatarImageView;
        @BindView(R.id.name_tv)
        TextView mNameTextView;
        @BindView(R.id.time_tv)
        TextView mTimeTextView;

        @BindView(R.id.content_tv)
        TextView mContentTextView;

        @BindViews({R.id.image_1, R.id.image_2, R.id.image_3, R.id.image_4, R.id.image_5, R.id.image_6})
        ImageView[] mImageViews;

        @BindView(R.id.share_btn)
        TextView mShareButton;
        @BindView(R.id.comment_btn)
        TextView mCommentButton;
        @BindView(R.id.like_btn)
        TextView mLikeButton;

        @BindView(R.id.root_view)
        ConstraintLayout mRootConstraintLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mBroccoli = new Broccoli();
            mBroccoli.addPlaceholders(mAvatarImageView, mNameTextView, mTimeTextView, mContentTextView);
            mBroccoli.addPlaceholders(mImageViews);
            mBroccoli.addPlaceholders(mShareButton, mCommentButton, mLikeButton);
        }

        /**
         * 设置分割线位置
         */
        public void setCutOffRuleSite(boolean isTop) {
            ConstraintSet mConstraintSet = new ConstraintSet();
            mConstraintSet.clone(mRootConstraintLayout);
            mConstraintSet.clear(R.id.cut_off_rule_2, ConstraintSet.TOP);
            mConstraintSet.connect(R.id.cut_off_rule_2, ConstraintSet.TOP, isTop ? R.id.image_1 : R.id.image_4, ConstraintSet.BOTTOM,
                    mContentTextView.getContext().getResources().getDimensionPixelSize(R.dimen.dp12));
            mConstraintSet.applyTo(mRootConstraintLayout);
        }

        /**
         * 设置图片
         */
        public void setImages(List<PictureEntity> pictureEntities, View itemView) {
            for (int i = 0; i < pictureEntities.size(); i++) {
                if (pictureEntities.size() == 4 && i > 1)
                    Glide.with(itemView).load(pictureEntities.get(i).getPicurl())
                            .transition(withCrossFade()).into(mImageViews[i + 1]);
                else
                    Glide.with(itemView).load(pictureEntities.get(i).getPicurl())
                            .transition(withCrossFade()).into(mImageViews[i]);
            }
            setCutOffRuleSite(pictureEntities.size() < 4);
            switch (pictureEntities.size()) {
                case 1:
                    mImageViews[0].setVisibility(View.VISIBLE);
                    mImageViews[1].setVisibility(View.INVISIBLE);
                    mImageViews[2].setVisibility(View.INVISIBLE);
                    mImageViews[3].setVisibility(View.GONE);
                    mImageViews[4].setVisibility(View.GONE);
                    mImageViews[5].setVisibility(View.GONE);
                    break;
                case 2: // 4-6 隐藏
                    mImageViews[0].setVisibility(View.VISIBLE);
                    mImageViews[1].setVisibility(View.VISIBLE);
                    mImageViews[2].setVisibility(View.INVISIBLE);
                    mImageViews[3].setVisibility(View.GONE);
                    mImageViews[4].setVisibility(View.GONE);
                    mImageViews[5].setVisibility(View.GONE);
                    break;
                case 3:
                    mImageViews[0].setVisibility(View.VISIBLE);
                    mImageViews[1].setVisibility(View.VISIBLE);
                    mImageViews[2].setVisibility(View.VISIBLE);
                    mImageViews[3].setVisibility(View.GONE);
                    mImageViews[4].setVisibility(View.GONE);
                    mImageViews[5].setVisibility(View.GONE);
                    break;
                case 4: // 后两个加载下面
                    mImageViews[0].setVisibility(View.VISIBLE);
                    mImageViews[1].setVisibility(View.VISIBLE);
                    mImageViews[2].setVisibility(View.INVISIBLE);
                    mImageViews[3].setVisibility(View.VISIBLE);
                    mImageViews[4].setVisibility(View.VISIBLE);
                    mImageViews[5].setVisibility(View.INVISIBLE);
                    break;
                case 5:
                    for (int i = 0; i < 5; i++)
                        mImageViews[i].setVisibility(View.VISIBLE);
                    mImageViews[5].setVisibility(View.INVISIBLE);
                    break;
                case 6:
                    for (int i = 0; i < 6; i++)
                        mImageViews[i].setVisibility(View.VISIBLE);
                    break;
            }
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

