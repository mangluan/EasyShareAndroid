package com.easyshare.adapter;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.easyshare.R;
import com.easyshare.activity.FriendsActivity;
import com.easyshare.entity.UserInfoEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.samlss.broccoli.Broccoli;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    private final static int NUMBER_OF_DEFAULT_PLACEHOLDERS = 10;

    private List<UserInfoEntity> mList;
    private FriendsActivity.FriendsType mType;

    public FriendsAdapter(List<UserInfoEntity> mList, FriendsActivity.FriendsType type) {
        this.mList = mList;
        this.mType = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friends, parent, false);
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
            UserInfoEntity entity = mList.get(position);
            Glide.with(holder.itemView).load(entity.getAvatarImage())
                    .transition(withCrossFade()).into(holder.mAvatarImageView);
            holder.mNameTextView.setText(entity.getName());
            holder.mSignTextView.setText(entity.getSign());
            holder.mSignTextView.setText(holder.itemView.getContext().getString(R.string.sign_format,
                    TextUtils.isEmpty(entity.getSign()) ? holder.itemView.getContext().getString(R.string.sign_null) : entity.getSign()));
            // 判断是否是好友
            if (entity.isFriend()) { // 是朋友
                holder.mSubmitButton.setText(R.string.friend);
            } else if (mType == FriendsActivity.FriendsType.ATTENTION && entity.getIsFriend() == 0) { // 关注页面 ： 我的单项关注
                holder.mSubmitButton.setText(R.string.cancel_attention);
            } else { // 粉丝页面：关注我的人 ， 关注页面：不是关注用户(已经取关z
                holder.mSubmitButton.setText(R.string.append_attention);
            }
            // 控制分割线
            holder.cuttingLine.setVisibility(getItemViewType(position) == 1 ? View.VISIBLE : View.INVISIBLE);
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
     * 2   ： 最后一项
     */
    @Override
    public int getItemViewType(int position) {
        if (mList.size() == 0 && getItemCount() == NUMBER_OF_DEFAULT_PLACEHOLDERS) {
            return 0;
        }
        if (mList.size() - 1 == position) return 2;
        else return 1;
    }

    @SuppressLint("NonConstantResourceId")
    public class ViewHolder extends RecyclerView.ViewHolder {

        Broccoli mBroccoli;

        @BindView(R.id.avatar_image)
        ImageView mAvatarImageView;
        @BindView(R.id.item_name)
        TextView mNameTextView;
        @BindView(R.id.item_sign)
        TextView mSignTextView;
        @BindView(R.id.item_submit)
        Button mSubmitButton;

        @BindView(R.id.CuttingLine)
        View cuttingLine;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mBroccoli = new Broccoli();
            mBroccoli.addPlaceholders(mAvatarImageView, mNameTextView, mSignTextView, mSubmitButton);
            itemView.setOnClickListener(v -> {
                if (mOnClickListener != null && FriendsAdapter.this.getItemViewType(getBindingAdapterPosition()) != 0)
                    mOnClickListener.onClickItemListener(mList.get(getBindingAdapterPosition()), mType, getBindingAdapterPosition());
            });
            mSubmitButton.setOnClickListener(v -> {
                if (mOnClickListener != null && FriendsAdapter.this.getItemViewType(getBindingAdapterPosition()) != 0)
                    mOnClickListener.onClickSubmitListener(mSubmitButton, mList.get(getBindingAdapterPosition()), mType, getBindingAdapterPosition());
            });
        }

    }


    /**
     * 点击接口  供外部调用
     */
    public interface OnClickListener {
        void onClickItemListener(UserInfoEntity entity, FriendsActivity.FriendsType type, int position);

        void onClickSubmitListener(Button button, UserInfoEntity entity, FriendsActivity.FriendsType type, int position);
    }

    private OnClickListener mOnClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

}

