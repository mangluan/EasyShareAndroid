package com.easyshare.utils;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.ScaleAnimation;

public class AnimationUtils {

    public static ScaleAnimation getScaleAnimation() {
        // 随机生成 0.4 - 0.6 之间的数
        float toXF = (float) (Math.random() * (0.2) + 0.4);
        // x 轴 0.4 ~ 0.6 - 1
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, toXF, 1f, 1f);
        // 设置插值
//        scaleAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        // 随机生成 500 - 800 之间的数
        int durationMillis = (int) (Math.random() * (300) + 500);
        //设置动画持续时长
        scaleAnimation.setDuration(durationMillis);
        //设置动画的重复模式：反转REVERSE和重新开始RESTART
        scaleAnimation.setRepeatMode(ScaleAnimation.REVERSE);
        //设置动画播放次数
        scaleAnimation.setRepeatCount(ScaleAnimation.INFINITE);
        return scaleAnimation;
    }
}
