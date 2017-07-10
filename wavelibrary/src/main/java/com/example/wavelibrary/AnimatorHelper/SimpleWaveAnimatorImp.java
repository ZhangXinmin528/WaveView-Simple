package com.example.wavelibrary.AnimatorHelper;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.example.wavelibrary.WaveView;

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

        mAnimatorSet = new AnimatorSet();

        // horizontal animation.
        // wave waves infinitely.
        ObjectAnimator waveShiftAnim = ObjectAnimator.ofFloat(
                mWaveView, "waveShiftRatio", 0f, 1f);
        waveShiftAnim.setRepeatCount(ValueAnimator.INFINITE);
        waveShiftAnim.setDuration(1000);
        waveShiftAnim.setInterpolator(new LinearInterpolator());

        // vertical animation.
        // water level increases from 0 to center of WaveView
        ObjectAnimator waterLevelAnim = ObjectAnimator.ofFloat(
                mWaveView, "waterLevelRatio", 0f, 0.5f);
        waterLevelAnim.setDuration(10000);
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

    public void setmWaveView(WaveView mWaveView) {
        this.mWaveView = mWaveView;
    }

    public void setmAnimatorSet(AnimatorSet mAnimatorSet) {
        this.mAnimatorSet = mAnimatorSet;
    }
}
