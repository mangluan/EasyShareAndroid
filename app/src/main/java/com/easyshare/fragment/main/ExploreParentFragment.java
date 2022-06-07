package com.easyshare.fragment.main;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.easyshare.R;
import com.easyshare.activity.SearchActivity;
import com.easyshare.base.BaseDataBean;
import com.easyshare.base.BaseFragment;
import com.easyshare.entity.UserInfoEntity;
import com.easyshare.network.Constants;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class ExploreParentFragment extends BaseFragment {

    private ExploreParentViewModel mViewModel;

    @BindView(R.id.magic_indicator)
    MagicIndicator mIndicator;

    @BindView(R.id.view_pager)
    ViewPager2 mViewPager;

    public static ExploreParentFragment newInstance() {
        return new ExploreParentFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_explore_parent, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ExploreParentViewModel.class);
        // 初始化ViewPag
        Fragment[] fragments = new Fragment[]{
                ExploreFragment.newInstance(),   // TODO
                ExploreFragment.newInstance(),
                ExploreFragment.newInstance(),
                ExploreFragment.newInstance(),
                ExploreFragment.newInstance(),
                ExploreFragment.newInstance(),
                ExploreFragment.newInstance(),
                ExploreFragment.newInstance(),
                ExploreFragment.newInstance(),
                ExploreFragment.newInstance()
        };
//        mViewPager.setAdapter(new FragmentStateAdapter(getChildFragmentManager(),getLifecycle()) {
        mViewPager.setAdapter(new FragmentStateAdapter(this) {
            @Override
            public int getItemCount() {
                return fragments.length;
            }

            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return fragments[position];
            }
        });
        // view page 指示器
        List<String> mTitleDataList = new ArrayList<>();
        Collections.addAll(mTitleDataList, "热门", "美食", "生活", "学习", "美妆", "摄影", "动漫", "历史", "艺术", "科普");
        CommonNavigatorAdapter commonNavigatorAdapter = new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return mTitleDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                ColorTransitionPagerTitleView titleView = new ColorTransitionPagerTitleView(context);
                titleView.setNormalColor(getContext().getColor(R.color.black_translucence_eighty));
                titleView.setSelectedColor(getContext().getColor(R.color.colorPrimary));
                titleView.setText(mTitleDataList.get(index));
                titleView.setTextSize(14f);
                titleView.setOnClickListener(view -> mViewPager.setCurrentItem(index));
                return titleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                return null;
            }
        };
        CommonNavigator commonNavigator = new CommonNavigator(getContext());
        commonNavigator.setAdapter(commonNavigatorAdapter);
        mIndicator.setNavigator(commonNavigator);
        // viewpager indicator 绑定
        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                mIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mIndicator.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                mIndicator.onPageScrollStateChanged(state);
            }
        });
    }

    /**
     * 点击 更多分类 TODO
     */
    @OnClick(R.id.btn_edit_group)
    public void onClick(){
        EventBus.getDefault().post(BaseDataBean.build(Constants.OPEN_SELECT_CLASSIFICATION_ACTIVITY));
    }

    /**
     * 点击搜索，跳转 搜索页面
     */
    @OnClick(R.id.search_view)
    public void onSearchClick(){
        SearchActivity.startActivity(getContext());
    }

}