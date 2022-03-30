package com.easyshare.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.easyshare.R;
import com.hjq.toast.ToastUtils;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class PublishImageAdapter extends RecyclerView.Adapter<PublishImageAdapter.ViewHolder> {

    public static final String TAG = "PublishImageAdapter";

    public static final int TYPE_CAMERA = 1;
    public static final int TYPE_PICTURE = 2;
    private final LayoutInflater mInflater;
    private final ArrayList<LocalMedia> list = new ArrayList<>();
    private int selectMax = 6;

    /**
     * 删除
     */
    public void delete(int position) {
        try {
            if (position != RecyclerView.NO_POSITION && list.size() > position) {
                list.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, list.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PublishImageAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    public PublishImageAdapter(Context context, List<LocalMedia> result) {
        this.mInflater = LayoutInflater.from(context);
        this.list.addAll(result);
    }

    public void setSelectMax(int selectMax) {
        this.selectMax = selectMax;
    }

    public int getSelectMax() {
        return selectMax;
    }

    public ArrayList<LocalMedia> getData() {
        return list;
    }

    public void remove(int position) {
        if (position < list.size()) {
            list.remove(position);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mImageView;
        ImageView mImageDelete;

        public ViewHolder(View view) {
            super(view);
            mImageView = view.findViewById(R.id.image);
            mImageDelete = view.findViewById(R.id.image_del);
        }
    }

    @Override
    public int getItemCount() {
        if (list.size() < selectMax) {
            return list.size() + 1;
        } else {
            return list.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowAddItem(position)) {
            return TYPE_CAMERA;
        } else {
            return TYPE_PICTURE;
        }
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.view_filter_image, viewGroup, false);
        return new ViewHolder(view);
    }

    private boolean isShowAddItem(int position) {
        int size = list.size();
        return position == size;
    }

    /**
     * 设置值
     */
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        //少于MaxSize张，显示继续添加的图标
        if (getItemViewType(position) == TYPE_CAMERA) {
            viewHolder.mImageView.setImageResource(R.drawable.ic_add_image);
            viewHolder.mImageView.setOnClickListener(view -> {
                if (mItemClickListener != null) {
                    mItemClickListener.openPicture();
                }
            });
            viewHolder.mImageDelete.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.mImageDelete.setVisibility(View.VISIBLE);
            viewHolder.mImageDelete.setOnClickListener(view -> {
                int index = viewHolder.getAbsoluteAdapterPosition();
                if (index != RecyclerView.NO_POSITION && list.size() > index) {
                    if (list.size() == 1){
                        ToastUtils.show(mInflater.getContext().getString(R.string.error_image_delete));
                    }else {
                        list.remove(index);
                        notifyItemRemoved(index);
                        notifyItemRangeChanged(index, list.size());
                    }
                }
            });
            LocalMedia media = list.get(position);

//            int chooseModel = media.getChooseModel();
//            String path;
//            if (media.isCut() && !media.isCompressed()) {
//                // 裁剪过
//                path = media.getCutPath();
//            } else if (media.isCut() || media.isCompressed()) {
//                // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
//                path = media.getCompressPath();
//            } else {
//                // 原图
//                path = media.getPath();
//            }
//
//            Log.i(TAG, "原图地址::" + media.getPath());
//            Log.i(TAG, "Available地址::" + media.getAvailablePath());
//
//            if (media.isCut()) {
//                Log.i(TAG, "裁剪地址::" + media.getCutPath());
//            }
//            if (media.isCompressed()) {
//                Log.i(TAG, "压缩地址::" + media.getCompressPath());
//                Log.i(TAG, "压缩后文件大小::" + new File(media.getCompressPath()).length() / 1024 + "k");
//            }
//            if (media.isToSandboxPath()) {
//                Log.i(TAG, "Android Q特有地址::" + media.getSandboxPath());
//            }
//            if (media.isOriginal()) {
//                Log.i(TAG, "是否开启原图功能::" + true);
//                Log.i(TAG, "开启原图功能后地址::" + media.getOriginalPath());
//            }

            Glide.with(viewHolder.itemView.getContext())
//                    .load(PictureMimeType.isContent(path) && !media.isCut() && !media.isCompressed() ? Uri.parse(path) : path)
                    .load(media.getAvailablePath())
                    .centerCrop()
                    .placeholder(R.color.white_translucence_eighty)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.mImageView);

            //itemView 的点击事件
            if (mItemClickListener != null) {
                viewHolder.itemView.setOnClickListener(v -> {
                    int adapterPosition = viewHolder.getAbsoluteAdapterPosition();
                    mItemClickListener.onItemClick(v, adapterPosition);
                });
            }
            if (mItemLongClickListener != null) {
                viewHolder.itemView.setOnLongClickListener(v -> {
                    int adapterPosition = viewHolder.getAbsoluteAdapterPosition();
                    mItemLongClickListener.onItemLongClick(viewHolder, adapterPosition, v);
                    return true;
                });
            }
        }
    }

    private OnItemClickListener mItemClickListener;

    public void setOnItemClickListener(OnItemClickListener l) {
        this.mItemClickListener = l;
    }

    public interface OnItemClickListener {
        /**
         * Item click event
         */
        void onItemClick(View v, int position);

        /**
         * Open PictureSelector
         */
        void openPicture();
    }

    private OnItemLongClickListener mItemLongClickListener;

    public void setItemLongClickListener(OnItemLongClickListener l) {
        this.mItemLongClickListener = l;
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(RecyclerView.ViewHolder holder, int position, View v);
    }

}
