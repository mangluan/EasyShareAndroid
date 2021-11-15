package com.easyshare.fragment.main;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.easyshare.R;
import com.easyshare.base.BaseFragment;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;

public class ExploreRecommendGroupFragment extends BaseFragment {

    private ExploreRecommendGroupViewModel mViewModel;

    @BindView(R.id.magic_indicator)
    MagicIndicator mIndicator;
    @BindView(R.id.view_pager)
    ViewPager2 mViewPager;


    public static ExploreRecommendGroupFragment newInstance() {
        return new ExploreRecommendGroupFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_explore_recommend_group, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ExploreRecommendGroupViewModel.class);
        // 初始化ViewPag
        Fragment[] fragments = new Fragment[]{
                ExploreRecommendFragment.newInstance(),   // 推荐
                ExploreRecommendFragment.newInstance(),   // 推荐
                ExploreRecommendFragment.newInstance(),   // 推荐
                ExploreRecommendFragment.newInstance(),   // 推荐
                ExploreRecommendFragment.newInstance(),   // 推荐
                ExploreRecommendFragment.newInstance(),   // 推荐
                ExploreRecommendFragment.newInstance(),   // 推荐
                ExploreRecommendFragment.newInstance(),   // 推荐
                ExploreRecommendFragment.newInstance(),   // 推荐
                ExploreRecommendFragment.newInstance(),   // 推荐
                ExploreRecommendFragment.newInstance()   // 推荐
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
        Collections.addAll(mTitleDataList, "热门", "搞笑", "美食", "学习", "美妆", "摄影", "动漫", "历史", "艺术", "科普", "生活");
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

}