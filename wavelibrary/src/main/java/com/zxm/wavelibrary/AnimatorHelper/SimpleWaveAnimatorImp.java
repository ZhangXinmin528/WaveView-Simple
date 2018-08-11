package com.zxm.wavelibrary.AnimatorHelper;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.zxm.wavelibrary.WaveView;

import static com.zxm.wavelibrary.WaveView.SHAPE_CIRCLE;
import static com.zxm.wavelibrary.WaveView.SHAPE_RECTANGLE;

/**
 * Created by ZhangXinmin on 2017/7/10.
 * Copyright (c) 2017 . All rights reserved.
 * This is the implementation class of WaveAnimtorHelper.This class help developers implement
 * the wave effect.
 */

public class SimpleWaveAnimatorImp implements WaveAnimatorHelper {

    private WaveView mWaveView;
    private AnimatorSet mAnimatorSet;

    public SimpleWaveAnimatorImp() {
        this(null);
    }

    public SimpleWaveAnimatorImp(WaveView mWaveView) {
        this.mWaveView = mWaveView;
        initParams();
    }

    //init animators
    private void initParams() {
        if (mWaveView == null) return;
        setForBackGround(1000);
    }

    /**
     * the loading animation
     * 变成loading动画
     *
     * @param duration how long the loading animation should last in ms
     */
    public void setLoadingAnim(long duration) {
        if (mAnimatorSet == null) {
            mAnimatorSet = new AnimatorSet();
        }
        //circle shape
        mWaveView.setShapeType(SHAPE_CIRCLE);

        // horizontal animation.
        // wave waves infinitely.
        ObjectAnimator waveShiftAnim = ObjectAnimator.ofFloat(
                mWaveView, "waveShiftRatio", 0f, 1f);
        waveShiftAnim.setRepeatCount(ValueAnimator.INFINITE);
        waveShiftAnim.setDuration(1000);
        waveShiftAnim.setInterpolator(new LinearInterpolator());

        // vertical animation.
        // water level increases from 0 to 1 of WaveView
        ObjectAnimator waterLevelAnim = ObjectAnimator.ofFloat(
                mWaveView, "waterLevelRatio", 0.0f, 1.0f);
        waterLevelAnim.setDuration(duration);
        waterLevelAnim.setInterpolator(new DecelerateInterpolator());

        // amplitude animation.
        // wave grows big then grows small, repeatedly
        ObjectAnimator amplitudeAnim = ObjectAnimator.ofFloat(
                mWaveView, "amplitudeRatio", 0.0001f, 0.05f);
        amplitudeAnim.setRepeatCount(ValueAnimator.INFINITE);
        amplitudeAnim.setRepeatMode(ValueAnimator.REVERSE);
        amplitudeAnim.setDuration(5000);
        amplitudeAnim.setInterpolator(new LinearInterpolator());

        mAnimatorSet.playTogether(waveShiftAnim, waterLevelAnim, amplitudeAnim);
    }

    /**
     * The  water ripple background.
     *
     * @param duration how long the water shift animation should last in ms
     */
    public void setForBackGround(long duration) {
        if (mAnimatorSet == null) {
            mAnimatorSet = new AnimatorSet();
        }
        //circle shape
        mWaveView.setShapeType(SHAPE_RECTANGLE);

        // horizontal animation.
        // wave waves infinitely.
        ObjectAnimator waveShiftAnim = ObjectAnimator.ofFloat(
                mWaveView, "waveShiftRatio", 0f, 1f);
        waveShiftAnim.setRepeatCount(ValueAnimator.INFINITE);
        waveShiftAnim.setDuration(duration);
        waveShiftAnim.setInterpolator(new LinearInterpolator());

        // amplitude animation.
        // wave grows big then grows small, repeatedly
        ObjectAnimator amplitudeAnim = ObjectAnimator.ofFloat(
                mWaveView, "amplitudeRatio", 0.02f, 0.05f);
        amplitudeAnim.setRepeatCount(ValueAnimator.INFINITE);
        amplitudeAnim.setRepeatMode(ValueAnimator.REVERSE);
        amplitudeAnim.setDuration(5000);
        amplitudeAnim.setInterpolator(new LinearInterpolator());

        mAnimatorSet.playTogether(waveShiftAnim,amplitudeAnim);
    }

    @Override
    public void startAnimators() {
        mWaveView.setShowWave(true);
        if (mAnimatorSet != null) {
            mAnimatorSet.start();
        }
    }

    @Override
    public void endAnimators() {
        if (mAnimatorSet != null) {
            mAnimatorSet.end();
        }
    }

    public void setWaveView(WaveView waveView) {
        this.mWaveView = waveView;
    }

    public void setAnimatorSet(AnimatorSet animatorSet) {
        this.mAnimatorSet = animatorSet;
    }
}
