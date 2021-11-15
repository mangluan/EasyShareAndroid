package com.easyshare.fragment.onboarding;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.easyshare.R;
import com.easyshare.utils.ViewUtils;

import butterknife.BindView;


@SuppressLint("NonConstantResourceId")
public class OnBoardingTwoFragment extends OnBoardingBaseFragment {

    @BindView(R.id.root)
    View mRootView;
    @BindView(R.id.qq_icon)
    ImageView qqIcon;
    @BindView(R.id.weibo_icon)
    ImageView weiboIcon;
    @BindView(R.id.wechat_icon)
    ImageView wechatIcon;

    public static OnBoardingTwoFragment newInstance(int position) {
        OnBoardingTwoFragment fragment = new OnBoardingTwoFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        View rootView = inflater.inflate(R.layout.fragment_onboarding_two, container, false);
        setRootPositionTag(rootView);
        return rootView;
    }

    @Override
    public void enterScene(ImageView sharedElement, float position) {
        mRootView.setAlpha(1);

        moveSnsIconOut(position, sharedElement, qqIcon);
        moveSnsIconOut(position, sharedElement, weiboIcon);
        moveSnsIconOut(position, sharedElement, wechatIcon);

        // scale icons up
        scaleIcon(qqIcon, 1 - position);
        scaleIcon(weiboIcon, 1 - position);
        scaleIcon(wechatIcon, 1 - position);
    }

    @Override
    public void centerScene(ImageView sharedElement) {
        sharedElement.setTranslationY(-getResources().getDimension(R.dimen.tutorial_shared_element_translate_y));
        setSharedImageRadius(sharedElement, 1);
    }

    //position goes from -1.0 to 0.0
    @Override
    public void exitScene(ImageView sharedElement, float position) {
        mRootView.setAlpha(1);

        // scale icons down
        scaleIcon(qqIcon, 1 + position);
        scaleIcon(weiboIcon, 1 + position);
        scaleIcon(wechatIcon, 1 + position);

        qqIcon.setAlpha(1 + position);
        weiboIcon.setAlpha(1 + position);
        wechatIcon.setAlpha(1 + position);
    }

    @Override
    public void notInScene() {
        mRootView.setAlpha(0);
    }

    private void moveSnsIconOut(float position, ImageView sharedElement, ImageView icon) {
        Point iconCenter = ViewUtils.getViewCenterPoint(icon);
        Point sharedCenter = ViewUtils.getViewCenterPoint(sharedElement);

        float distanceX = (sharedCenter.x - iconCenter.x) * position;
        float distanceY = (sharedCenter.y - iconCenter.y) * position;

        // multiple position again for an accelerate effect
        icon.setTranslationX(distanceX * position);
        icon.setTranslationY(distanceY * position);
    }

    private void scaleIcon(ImageView icon, float position) {
        icon.setScaleX(position);
        icon.setScaleY(position);
    }
}
