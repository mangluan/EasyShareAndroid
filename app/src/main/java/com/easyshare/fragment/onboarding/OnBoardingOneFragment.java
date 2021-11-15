package com.easyshare.fragment.onboarding;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.easyshare.R;

import butterknife.BindView;


@SuppressLint("NonConstantResourceId")
public class OnBoardingOneFragment extends OnBoardingBaseFragment {

    @BindView(R.id.deviceImage)
    ImageView mDeviceImageView;
    @BindView(R.id.sharedImage)
    ImageView mSharedImageView;
    @BindView(R.id.deviceText)
    TextView mDeviceTextView;

    public static OnBoardingOneFragment newInstance(int position) {
        OnBoardingOneFragment fragment = new OnBoardingOneFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        View rootView = inflater.inflate(R.layout.fragment_onboarding_one, container, false);
        setRootPositionTag(rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_onboarding_shared);
        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        drawable.setAntiAlias(true);
        mSharedImageView.setImageDrawable(drawable);
    }

    @Override
    public void enterScene(ImageView sharedElement, float position) {
        // starts center so no entrance needed
    }

    @Override
    public void centerScene(ImageView sharedElement) {
        mSharedImageView.setTranslationY(0);
        setSharedImageRadius(mSharedImageView, 0);

        mDeviceImageView.setAlpha(1.0f);
        mDeviceTextView.setAlpha(1.0f);
        mDeviceImageView.setAlpha(1.0f);
        mDeviceImageView.setScaleX(1.0f);
    }

    //position goes from -1.0 to 0.0
    @Override
    public void exitScene(ImageView sharedElement, float position) {
        mSharedImageView.setTranslationY(getResources().getDimension(R.dimen.tutorial_shared_element_translate_y) * position);
        setSharedImageRadius(mSharedImageView, -position);
        mDeviceTextView.setAlpha(1 + position);
        mDeviceImageView.setAlpha(1 + position);
        mDeviceImageView.setScaleX(1 - position); // stretch
    }

    @Override
    public void notInScene() {
        mDeviceImageView.setAlpha(0.0f);
        mDeviceTextView.setAlpha(0.0f);
    }
}
