package com.easyshare.adapter;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.easyshare.R;
import com.easyshare.entity.PictureEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ImageViewPagerAdapter extends RecyclerView.Adapter<ImageViewPagerAdapter.ViewHolder> {

    private List<PictureEntity> imagesUri;

    public ImageViewPagerAdapter(List<PictureEntity> imagesUri) {
        this.imagesUri = imagesUri;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_adaptive_image, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(holder.image).load(imagesUri.get(position).getPicurl())
                .transition(withCrossFade()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return imagesUri.size();
    }

     class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image)
        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v -> {
                if (mOnClickListener !=null)
                    mOnClickListener.onClickItemListener(getAbsoluteAdapterPosition());
            });
        }
    }

    /**
     * 点击接口  供外部调用
     */
    public interface OnClickListener {
        void onClickItemListener(int position);
    }

    private OnClickListener mOnClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }


}
