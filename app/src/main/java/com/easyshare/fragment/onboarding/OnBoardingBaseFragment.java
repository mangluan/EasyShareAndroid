package com.easyshare.fragment.onboarding;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;

import com.easyshare.base.BaseFragment;


public abstract class OnBoardingBaseFragment extends BaseFragment implements SceneTransformer.SceneChangeListener {

    protected static final String KEY_POSITION = "KEY_POSITION";

    // we have to set a position tag to the root layout of every scene fragment
    // this is so the transformer will know who to make a callback to
    protected void setRootPositionTag(@NonNull View root) {
        root.setTag(getArguments().getInt(KEY_POSITION));
    }

    @Override
    public abstract void enterScene(@Nullable ImageView sharedElement, float position);

    @Override
    public abstract void centerScene(@Nullable ImageView sharedElement);

    @Override
    public abstract void exitScene(@Nullable ImageView sharedElement, float position);

    @Override
    public abstract void notInScene();

    public void setSharedImageRadius(ImageView imageView, float percent) {
        RoundedBitmapDrawable roundedDrawable = ((RoundedBitmapDrawable) imageView.getDrawable());
        float radius = roundedDrawable.getIntrinsicWidth() / 2f;
        roundedDrawable.setCornerRadius(radius * percent);
        imageView.setImageDrawable(roundedDrawable);
        imageView.requestLayout();
    }
}
