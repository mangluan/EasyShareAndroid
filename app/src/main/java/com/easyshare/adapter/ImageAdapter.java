package com.easyshare.adapter;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.IntDef;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {


    /*********** 限定开始 *************/

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    @IntDef({HORIZONTAL, VERTICAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Orientation {
    }

    /*********** 限定结束 *************/

    private List<String> imagesUri;
    private int orientation;
    private int margin;



    public ImageAdapter(List<String> imagesUri, @Orientation int orientation) {
        this(imagesUri, orientation, 10);
    }

    public ImageAdapter(List<String> imagesUri, @Orientation int orientation, float margin) {
        this.imagesUri = imagesUri;
        this.orientation = orientation;
        this.margin = (int) margin;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 如果是水平的，则纵向占满
        int width = orientation == VERTICAL ? ViewGroup.LayoutParams.MATCH_PARENT : ViewGroup.LayoutParams.WRAP_CONTENT;
        int height = orientation == HORIZONTAL ? ViewGroup.LayoutParams.MATCH_PARENT : ViewGroup.LayoutParams.WRAP_CONTENT;
        int marginVertical = orientation == VERTICAL ? margin : 0;
        int marginHorizontal = orientation == HORIZONTAL ? margin : 0;
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
        layoutParams.setMargins(marginHorizontal, marginVertical, marginHorizontal, marginVertical);
        ImageView imageView = new ImageView(parent.getContext());
        imageView.setLayoutParams(layoutParams);
        imageView.setAdjustViewBounds(true);
//        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new ViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(holder.image).load(imagesUri.get(position))
                .transition(withCrossFade()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return imagesUri.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;

        public ViewHolder(ImageView view) {
            super(view);
            image = view;
        }
    }

    /**
     * 点击接口  供外部调用
     */
    public interface OnClickListener {
        void onClickItemListener(String image);
    }

    private OnClickListener mOnClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }


}
