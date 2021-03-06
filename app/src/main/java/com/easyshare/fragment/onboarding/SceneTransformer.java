package com.easyshare.fragment.onboarding;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

public class SceneTransformer implements ViewPager.PageTransformer {

    public interface SceneChangeListener {

        void enterScene(@Nullable ImageView sharedElement, float position);

        void centerScene(@Nullable ImageView sharedElement);

        void exitScene(@Nullable ImageView sharedElement, float position);

        void notInScene();
    }

    private List<SceneChangeListener> mSceneChangeListeners = new ArrayList<>();
    private ImageView mSharedElement;

    public void transformPage(@NonNull View view, float position) {
//        Log.e("transformPage", "--------------------");
//        Log.e("transformPage", "view : "+ view.getTag() );
//        Log.e("transformPage", "position : "+ position );
        //cancel view pagers natural scroll
        view.setTranslationX(view.getWidth() * - position);

        for (int scene = 0; scene < mSceneChangeListeners.size(); scene++) {
            // get the shared element from the first fragment
            if (view.getTag().equals(0) && mSharedElement == null) {
                mSharedElement = (ImageView) view.findViewWithTag("shared_element");
            }

            // callback to fragment based on the tag set when the fragment is created
            if (view.getTag().equals(scene)) {
                makeSceneCallbacks(mSceneChangeListeners.get(scene), position);
            }
        }
    }

    public void addSceneChangeListener(@NonNull SceneChangeListener sceneChangeListener) {
        mSceneChangeListeners.add(sceneChangeListener);
    }

    private void makeSceneCallbacks(SceneChangeListener sceneChangeListener, float position) {
        if (centerScene(position)) {
            sceneChangeListener.centerScene(mSharedElement);
        } else if (enterScene(position)) {
            sceneChangeListener.enterScene(mSharedElement, position);
        } else if (exitScene(position)) {
            sceneChangeListener.exitScene(mSharedElement, position);
        } else if (notInScene(position)) {
            sceneChangeListener.notInScene();
        }
    }

    private boolean centerScene(float position) {
        return position == 0.0F;
    }

    private boolean exitScene(float position) {
        return position > -1.0f && position < 0.0f;
    }

    private boolean enterScene(float position) {
        return position < 1.0f && position > 0;
    }

    private boolean notInScene(float position) {
        return position <= -1.0F || position >= 1.0F;
    }
}