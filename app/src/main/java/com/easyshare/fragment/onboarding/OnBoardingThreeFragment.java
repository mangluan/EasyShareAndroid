package com.easyshare.fragment.onboarding;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.easyshare.R;
import com.easyshare.adapter.ImageAdapter;
import com.easyshare.utils.ViewUtils;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;


@SuppressLint("NonConstantResourceId")
public class OnBoardingThreeFragment extends OnBoardingBaseFragment {

    private static final int SCROLL_OFFSET = 200;

    private Point transitionDistance;
    private int scrollOffsetX;
    private int finishWidth, finishHeight;

    @BindViews({R.id.recycler_1, R.id.recycler_2, R.id.recycler_3, R.id.recycler_4, R.id.recycler_5})
    RecyclerView[] mRecyclerViews;
    @BindView(R.id.root)
    View mRootView;

    public static OnBoardingThreeFragment newInstance(int position) {
        OnBoardingThreeFragment scene = new OnBoardingThreeFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_POSITION, position);
        scene.setArguments(args);
        return scene;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        View rootView = inflater.inflate(R.layout.fragment_onboarding_three, container, false);
        setRootPositionTag(rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // init image
        setRecyclerView(mRecyclerViews[0], Arrays.asList(getResources().getStringArray(R.array.images_1)));
        setRecyclerView(mRecyclerViews[1], Arrays.asList(getResources().getStringArray(R.array.images_2)));
        setRecyclerView(mRecyclerViews[2], Arrays.asList(getResources().getStringArray(R.array.images_3)));
        setRecyclerView(mRecyclerViews[3], Arrays.asList(getResources().getStringArray(R.array.images_4)));
        setRecyclerView(mRecyclerViews[4], Arrays.asList(getResources().getStringArray(R.array.images_5)));

        if (savedInstanceState != null) {
            transitionDistance = savedInstanceState.getParcelable("transitionDistance");
            finishWidth = savedInstanceState.getInt("finishWidth");
            finishHeight = savedInstanceState.getInt("finishHeight");
            scrollOffsetX = savedInstanceState.getInt("scrollOffsetX");

            moveScrollViews(0);

            // make sure finish view is invisible
            ImageView finishView = (ImageView) mRecyclerViews[1].getLayoutManager().findViewByPosition(3);
            if (finishView != null) {
                finishView.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void setRecyclerView(RecyclerView recyclerView, List<String> url) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        ImageAdapter adapter = new ImageAdapter(url, ImageAdapter.HORIZONTAL, getResources().getDimension(R.dimen.dp4));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("transitionDistance", transitionDistance);
        outState.putInt("scrollOffsetX", scrollOffsetX);
        outState.putInt("finishWidth", finishWidth);
        outState.putInt("finishHeight", finishHeight);
    }

    @Override
    public void enterScene(ImageView sharedElement, float position) {
        if (transitionDistance == null) {
            setTransition(sharedElement);
        }

        mRootView.setAlpha(1 - position);

        sharedElement.setX(transitionDistance.x * (1 - position));
        sharedElement.setY(-getResources().getDimension(R.dimen.tutorial_shared_element_translate_y)
                + (transitionDistance.y * (1 - position)));
        scaleSharedElement(position, sharedElement);
        setSharedImageRadius(sharedElement, position);

        moveScrollViews(position);
    }

    private void setTransition(ImageView sharedElement) {
        // get the finish view
        ImageView finishView = (ImageView) mRecyclerViews[1].getLayoutManager().findViewByPosition(2);
        finishView.setVisibility(View.INVISIBLE);

        finishHeight = finishView.getHeight();
        finishWidth = finishView.getWidth();

        Point finishViewLocation = ViewUtils.getViewLocation(finishView);

        // find the point of the screen(middle - half image) and for final point to be centered
        int screenCenterX = ViewUtils.getDisplaySize(getActivity()).x / 2;
        int finishWidth = finishView.getWidth() / 2;
        int finishX = screenCenterX - finishWidth;

        // the distance the recyclerview needs to scroll for the finish view to be centered
        scrollOffsetX = finishX - finishViewLocation.x;

        Point sharedLocation = ViewUtils.getViewLocation(sharedElement);
        transitionDistance = new Point();
        transitionDistance.x = finishX - sharedLocation.x;
        transitionDistance.y = finishViewLocation.y - sharedLocation.y;
    }

    @Override
    public void centerScene(ImageView sharedElement) {
        mRootView.setAlpha(1.0f);

        // make sure shared element is set in the correct place
        sharedElement.setX(transitionDistance.x);
        sharedElement.setY(-getResources().getDimension(R.dimen.tutorial_shared_element_translate_y)
                + transitionDistance.y);
        scaleSharedElement(0, sharedElement);

        setSharedImageRadius(sharedElement, 0);
        moveScrollViews(0);
    }

    //position goes from -1.0 to 0.0
    @Override
    public void exitScene(ImageView sharedElement, float position) {
        // last scene, it wont exit
    }

    @Override
    public void notInScene() {
        // reset scroll views
        moveScrollViews(1.0f);
        mRootView.setAlpha(0);
    }

    private void moveScrollViews(float position) {
        // use and odd and even scroll so images don't end up perfectly aligned
        int scroll = (int) (scrollOffsetX * (1 - position) + SCROLL_OFFSET);

        for (int i = 0; i < mRecyclerViews.length; i++) {
            scrollRecycleViewTo(mRecyclerViews[i], i % 2 == 0 ? -scroll : scroll);
        }
    }

    private void scrollRecycleViewTo(RecyclerView recyclerView, int offset) {
        LinearLayoutManager layoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
        layoutManager.scrollToPositionWithOffset(2, offset);
    }

    private void scaleSharedElement(float position, ImageView sharedElement) {
        float scaleX = 1 - ((float) finishWidth / sharedElement.getWidth());
        float scaleY = 1 - ((float) finishHeight / sharedElement.getHeight());

        // scale around the center
        sharedElement.setPivotX(1.0f);
        sharedElement.setPivotY(1.0f);

        // scale the shared image to the finish views size
        sharedElement.setScaleX((1 - (scaleX * (1 - position))));
        sharedElement.setScaleY((1 - (scaleY * (1 - position))));
    }
}
