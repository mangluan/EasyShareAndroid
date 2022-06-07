package com.easyshare.fragment.album;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.easyshare.R;
import com.easyshare.adapter.ImageViewPagerAdapter;
import com.easyshare.base.BaseFragment;
import com.easyshare.entity.PhotoAlbumEntity;
import com.easyshare.entity.PictureEntity;
import com.easyshare.utils.RelativeDateFormatUtils;
import com.hjq.toast.ToastUtils;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.abs.IPagerNavigator;
import net.lucode.hackware.magicindicator.buildins.circlenavigator.CircleNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;

@SuppressLint({"NonConstantResourceId","NotifyDataSetChanged"})
public class DetailsImageTextFragment extends BaseFragment implements ImageViewPagerAdapter.OnClickListener {

    private DetailsImageTextViewModel mViewModel;

    @BindView(R.id.avatar_image)
    ImageView mAvatarImageView;
    @BindView(R.id.name_tv)
    TextView mNameTextView;

    @BindView(R.id.time_tv)
    TextView mTimeTextView;
    @BindView(R.id.content_tv)
    TextView mContentTextView;

    @BindView(R.id.image_view_page)
    ViewPager2 mImageViewPager;
    ImageViewPagerAdapter mImageAdapter;
    @BindView(R.id.magic_indicator)
    MagicIndicator mMagicIndicator;

    public static DetailsImageTextFragment newInstance() {
        return new DetailsImageTextFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_details_image_text, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DetailsImageTextViewModel.class);
        // 获取数据 -> 按理说，应该是传入id，然后服务器再次请求最新的数据。这里偷懒也不想改，所以不加todo
        if (getActivity() == null || getActivity().getIntent() == null) return;
        Serializable serializable = getActivity().getIntent().getSerializableExtra("entity");
        if (!(serializable instanceof PhotoAlbumEntity)) {
            ToastUtils.show("数据异常");
            getActivity().finish();
        } else
            mViewModel.setAlbumEntity((PhotoAlbumEntity) serializable);
        // 初始化页面
        initView();
        // 监听数据 -> 修改页面
        mViewModel.observePictureListData(this,PictureList->{
            mImageAdapter.notifyDataSetChanged();
            if (mMagicIndicator.getNavigator() instanceof CircleNavigator)
                ((CircleNavigator)mMagicIndicator.getNavigator()).setCircleCount(PictureList.size());
        });
        mViewModel.observeAlbumEntityData(this, entity -> {
            // 作者信息
            Glide.with(this).load(entity.getUserEntity().getAvatarImage())
                    .transition(withCrossFade()).into(mAvatarImageView);
            mNameTextView.setText(entity.getUserEntity().getName());
            // 图文信息
            mContentTextView.setText(entity.getTitle());
            mTimeTextView.setText(RelativeDateFormatUtils.relativeDateFormat(entity.getTime()));
            mViewModel.setPictureList(entity.getPictureEntities());
            // 评论点赞信息

        });
    }

    /**
     * 初始化页面
     */
    private void initView() {
        // ViewPager
        mImageAdapter = new ImageViewPagerAdapter(mViewModel.getPictureList());
        mImageAdapter.setOnClickListener(this);
        mImageViewPager.setAdapter(mImageAdapter);
//        if (true) return;
        // view page 指示器
        CircleNavigator navigator = new CircleNavigator(getContext());
        navigator.setRadius(16);
        navigator.setCircleSpacing(30);
        navigator.setCircleColor(ContextCompat.getColor(getContext(), R.color.white_translucence));
        mMagicIndicator.setNavigator(navigator);
        // viewpager indicator 绑定
        mImageViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                mMagicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mMagicIndicator.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                mMagicIndicator.onPageScrollStateChanged(state);
            }
        });
    }

    /**
     * 加载右上角按钮
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_details_nav_bottom, menu);
    }

    /**
     * 点击图片项
     */
    @Override
    public void onClickItemListener(int position) {

    }
}