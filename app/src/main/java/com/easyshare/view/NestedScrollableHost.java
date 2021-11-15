package com.easyshare.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.viewpager2.widget.ViewPager2;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Layout to wrap a scrollable component inside a ViewPager2. Provided as a solution to the problem
 * where pages of ViewPager2 have nested scrollable elements that scroll in the same direction as
 * ViewPager2. The scrollable element needs to be the immediate and only child of this host layout.
 *
 * This solution has limitations when using multiple levels of nested scrollable elements
 * (e.g. a horizontal RecyclerView in a vertical RecyclerView in a horizontal ViewPager2).
 */
public final class NestedScrollableHost extends FrameLayout {

    private int touchSlop;
    private float initialX;
    private float initialY;

    public NestedScrollableHost(@NotNull Context context) {
        super(context);
        this.touchSlop = ViewConfiguration.get(this.getContext()).getScaledTouchSlop();
    }

    public NestedScrollableHost(@NotNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.touchSlop = ViewConfiguration.get(this.getContext()).getScaledTouchSlop();
    }

    private ViewPager2 getParentViewPager() {
        ViewParent parent = this.getParent();
        if (!(parent instanceof View)) {
            parent = null;
        }
        View view;
        for (view = (View) parent; view != null && !(view instanceof ViewPager2); view = (View) parent) {
            parent = view.getParent();
            if (!(parent instanceof View)) {
                parent = null;
            }
        }
        return view instanceof ViewPager2 ? (ViewPager2) view : null;
    }

    private View getChild() {
        return this.getChildCount() > 0 ? this.getChildAt(0) : null;
    }

    private boolean canChildScroll(int orientation, float delta) {
        int direction = -((int) Math.signum(delta));
        View child = this.getChild();
        switch (orientation) {
            case 0:
                return child != null && child.canScrollHorizontally(direction);
            case 1:
                return child != null && child.canScrollVertically(direction);
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(@NotNull MotionEvent e) {
        this.handleInterceptTouchEvent(e);
        return super.onInterceptTouchEvent(e);
    }

    private void handleInterceptTouchEvent(MotionEvent e) {
        ViewPager2 parentViewPager = this.getParentViewPager();
        if (parentViewPager != null) {
            int orientation = parentViewPager.getOrientation();
            // Early return if child can't scroll in same direction as parent
            if (!canChildScroll(orientation, -1.0F) && !canChildScroll(orientation, 1.0F)) {
                return;
            }
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                this.initialX = e.getX();
                this.initialY = e.getY();
                this.getParent().requestDisallowInterceptTouchEvent(true);
            } else if (e.getAction() == MotionEvent.ACTION_MOVE) {
                float dx = e.getX() - this.initialX;
                float dy = e.getY() - this.initialY;
                boolean isVpHorizontal = orientation == 0;
                // assuming ViewPager2 touch-slop is 2x touch-slop of child
                float scaledDx = Math.abs(dx) * (isVpHorizontal ? 0.5F : 1.0F);
                float scaledDy = Math.abs(dy) * (isVpHorizontal ? 1.0F : 0.5F);

                if (scaledDx > touchSlop || scaledDy > touchSlop) {
                    if (isVpHorizontal == (scaledDy > scaledDx)) {
                        // Gesture is perpendicular, allow all parents to intercept
                        this.getParent().requestDisallowInterceptTouchEvent(false);
                    } else {
                        // Gesture is parallel, query child if movement in that direction is possible
                        if (canChildScroll(orientation,  isVpHorizontal ?  dx : dy)) {
                            // Child can scroll, disallow all parents to intercept
                            this.getParent().requestDisallowInterceptTouchEvent(true);
                        } else {
                            // Child cannot scroll, allow all parents to intercept
                            this.getParent().requestDisallowInterceptTouchEvent(false);
                        }
                    }
                }
            }
        }
    }

}
