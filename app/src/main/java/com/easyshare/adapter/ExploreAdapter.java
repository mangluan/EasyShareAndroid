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
import com.easyshare.utils.RelativeDateFormatUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import me.samlss.broccoli.Broccoli;

@SuppressLint("NonConstantResourceId")
public class ExploreAdapter extends RecyclerView.Adapter<ExploreAdapter.ViewHolder> {

    private final static int NUMBER_OF_DEFAULT_PLACEHOLDERS = 3;
    private List<PhotoAlbumEntity> mList;
    private boolean isEmpty = false;

    public ExploreAdapter(List<PhotoAlbumEntity> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_explore_complex, parent, false);
        return new ComplexViewHolder(itemView);
//        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_explore_simple, parent, false);
//        return new SimpleViewHolder(itemView);
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
            holder.setDate(albumEntity);
        }
    }

    public void setDataEmpty(){
        isEmpty = true;
    }

    @Override
    public int getItemCount() {
        return mList.size() == 0  && !isEmpty ? NUMBER_OF_DEFAULT_PLACEHOLDERS : mList.size();
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

        @BindView(R.id.share_btn)
        TextView mShareButton;
        @BindView(R.id.comment_btn)
        TextView mCommentButton;
        @BindView(R.id.like_btn)
        TextView mLikeButton;

        @BindView(R.id.root_view)
        ConstraintLayout mRootConstraintLayout;

        @BindView(R.id.expand_more)
        ImageView mExpandMoreView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mBroccoli = new Broccoli();
            mBroccoli.addPlaceholders(mAvatarImageView, mNameTextView, mTimeTextView, mContentTextView);
            mBroccoli.addPlaceholders(mShareButton, mCommentButton, mLikeButton, mExpandMoreView);
        }

        public void setDate(PhotoAlbumEntity albumEntity) {
            // 用户
            Glide.with(itemView).load(albumEntity.getUserEntity().getAvatarImage())
                    .transition(withCrossFade()).into(mAvatarImageView);
            mNameTextView.setText(albumEntity.getUserEntity().getName());
            // 图册
            mContentTextView.setText(albumEntity.getTitle());
            mTimeTextView.setText(RelativeDateFormatUtils.relativeDateFormat(albumEntity.getTime()));
            List<PictureEntity> pictureEntities = albumEntity.getPictureEntities();
            setImages(pictureEntities);
            // 点赞
            Drawable drawable = ContextCompat.getDrawable(itemView.getContext(),
                    albumEntity.isLike() ? R.drawable.ic_like_selected_18 : R.drawable.ic_like_18);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            mLikeButton.setCompoundDrawables(drawable, null, null, null);
            mLikeButton.setTextColor(ContextCompat.getColor(itemView.getContext(),
                    albumEntity.isLike() ? R.color.colorPrimary : R.color.black_translucence));
            // 其他
            if (albumEntity.getCommentCount() != 0)
                mCommentButton.setText(String.valueOf(albumEntity.getCommentCount()));
            else
                mCommentButton.setText(R.string.comment);
            if (albumEntity.getLikeCount() != 0)
                mLikeButton.setText(String.valueOf(albumEntity.getLikeCount()));
            else
                mLikeButton.setText(R.string.like);
            // 点击
            setOnClickListener();
        }

        /**
         * 设置图片
         */
        protected void setImages(List<PictureEntity> pictureEntities) {
        }

        /**
         * 设置点击事件
         */
        protected void setOnClickListener() {
            itemView.setOnClickListener(view -> {
                if (mOnClickListener != null)
                    mOnClickListener.onClickItemListener(mList.get(getAbsoluteAdapterPosition()));
            });
        }

    }

    public class SimpleViewHolder extends ViewHolder {

        @BindView(R.id.image)
        ImageView mImageView;

        @BindView(R.id.ic_multiple)
        View mMultipleIconView;

        public SimpleViewHolder(@NonNull View itemView) {
            super(itemView);
            mBroccoli.addPlaceholders(mImageView);
            mBroccoli.addPlaceholders(mMultipleIconView);
        }

        @Override
        protected void setImages(List<PictureEntity> pictureEntities) {
            Glide.with(itemView).load(pictureEntities.get(0).getPicurl())
                    .transition(withCrossFade()).into(mImageView);
//            mContentTextView.setText("《阳》");
//            Glide.with(itemView).load(R.drawable.test)
//                    .transition(withCrossFade()).into(mImageView);
        }
    }


    public class ComplexViewHolder extends ViewHolder {

        @BindViews({R.id.image_1, R.id.image_2, R.id.image_3, R.id.image_4, R.id.image_5, R.id.image_6})
        ImageView[] mImageViews;

        public ComplexViewHolder(@NonNull View itemView) {
            super(itemView);
            mBroccoli.addPlaceholders(mImageViews);
        }

        /**
         * 设置图片
         */
        public void setImages(List<PictureEntity> pictureEntities) {
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

    }

    /**
     * 点击接口  供外部调用
     */
    public interface OnClickListener {
        void onClickItemListener(PhotoAlbumEntity entity);

        void onClickMoreListener(PhotoAlbumEntity entity);

        void onClickLikeListener(PhotoAlbumEntity entity);

        void onClickCommentListener(PhotoAlbumEntity entity);

        void onClickShareListener(PhotoAlbumEntity entity);
    }

    private OnClickListener mOnClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

}

