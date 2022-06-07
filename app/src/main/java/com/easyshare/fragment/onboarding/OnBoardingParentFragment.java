package com.easyshare.fragment.onboarding;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.easyshare.R;
import com.easyshare.activity.LoginActivity;
import com.easyshare.activity.MainActivity;
import com.easyshare.base.BaseFragment;
import com.easyshare.network.Constants;
import com.easyshare.utils.SharedPreferenceUtils;
import com.easyshare.utils.StringUtils;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.circlenavigator.CircleNavigator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTouch;

@SuppressLint("NonConstantResourceId")
public class OnBoardingParentFragment extends BaseFragment {

    @BindView(R.id.magic_indicator)
    MagicIndicator mMagicIndicator;
    @BindView(R.id.tutorial_pager)
    ViewPager mTutorialViewPager;
    @BindView(R.id.text_pager)
    ViewPager mTextViewPager;
    @BindView(R.id.start_btn)
    Button mStartButton;

    OnBoardingBaseFragment[] mFragments;


    public static OnBoardingParentFragment newInstance() {
        return new OnBoardingParentFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_onboarding_parent, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // init fragment
        mFragments = new OnBoardingBaseFragment[]{
                OnBoardingOneFragment.newInstance(0),
                OnBoardingTwoFragment.newInstance(1),
                OnBoardingThreeFragment.newInstance(2)
        };
        // init view pager
        SceneTransformer sceneTransformer = new SceneTransformer();
        for (OnBoardingBaseFragment fragment : mFragments)
            sceneTransformer.addSceneChangeListener(fragment);
        mTutorialViewPager.setOffscreenPageLimit(3);
        mTutorialViewPager.setPageTransformer(true, sceneTransformer);
        mTutorialViewPager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager(), 0) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }

            @Override
            public int getCount() {
                return mFragments.length;
            }
        });
        mTextViewPager.setOffscreenPageLimit(3);
        List<String> mTestList = Arrays.asList(getResources().getStringArray(R.array.OnBoardingTitle));
        mTextViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return mTestList.size();
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                TextView textView = new TextView(container.getContext());
                int dp28 = (int) getResources().getDimension(R.dimen.dp28);
                textView.setPadding(dp28,0,dp28,0);
                textView.setTextSize(16f);
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.black_translucence_eighty));
                textView.setText(StringUtils.textIndent(mTestList.get(position)));
                container.addView(textView);
                return textView;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }
        });
        // init view pager circle navigator
        CircleNavigator navigator = new CircleNavigator(getContext());
        navigator.setCircleCount(mFragments.length);
        navigator.setRadius(16);
        navigator.setCircleSpacing(30);
        navigator.setCircleColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        mMagicIndicator.setNavigator(navigator);
        mTextViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mMagicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
                // translate up start button
                if (position == 1) {
                    mStartButton.setVisibility(View.VISIBLE);
                    mStartButton.setTranslationY(mTextViewPager.getBottom() * (1 - positionOffset));
                    mMagicIndicator.setAlpha(1 - positionOffset);
                }
            }

            @Override
            public void onPageSelected(int position) {
                mMagicIndicator.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                mMagicIndicator.onPageScrollStateChanged(state);
            }
        });
    }

    @OnTouch(R.id.touch_interceptor_layout)
    public boolean onTouchListener(MotionEvent event) {
        mTutorialViewPager.onTouchEvent(event);
        mTextViewPager.onTouchEvent(event);
        return true;
    }

    /**
     * 点击开始按钮 TODO
     */
    @OnClick(R.id.start_btn)
    public void onStartClick(){
        SharedPreferenceUtils.putBoolean(getContext(), Constants.IS_INITIALIZATION_APPLICATION,true);
//        MainActivity.startActivity(getContext());
        LoginActivity.startActivity(getContext());
        getActivity().finish();
    }

}