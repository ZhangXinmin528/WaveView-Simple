package com.zxm.wavelibrary;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.zxm.wavelibrary.AnimatorHelper.SimpleWaveAnimatorImp;
import com.zxm.wavelibrary.AnimatorHelper.WaveAnimatorHelper;

/**
 * Created by ZhangXinmin on 2017/7/10.
 * Copyright (c) 2017 . All rights reserved.
 * The class provide the effect of water ripple.
 * <p>
 * 正弦型函数解析式：y=Asin（ωx+φ）+h
 * 各常数值对函数图像的影响：
 * φ（初相位）：决定波形与X轴位置关系或横向移动距离（左加右减）
 * ω：决定周期（最小正周期T=2π/|ω|）
 * A：决定峰值（即纵向拉伸压缩的倍数）
 * h：表示波形在Y轴的位置关系或纵向移动距离（上加下减）
 */

public class WaveView extends View {
    // log tag
    private static final String TAG = WaveView.class.getSimpleName();

    //the shape type of view border
    private int mShapeType;
    public static final int SHAPE_CIRCLE = 0;//the border is circle
    public static final int SHAPE_RECTANGLE = 1;//the border is rectangle

    //params of water
    private static final float DEFAULT_AMPLITUDE_RATIO = 0.45f;//默认水位振幅比率
    private static final float DEFAULT_WATER_LEVEL_RATIO = 0.5f;//默认水位比率
    private static final float DEFAULT_WAVE_LENGTH_RATIO = 1.0f;//默认波长比率
    private static final float DEFAULT_WAVE_SHIFT_RATIO = 0.0f;//默认波浪偏移比率

    // if true,the shader will display the wave
    private boolean mShowWave;
    // shader containing repeated waves
    private BitmapShader mWaveShader;
    //shader matrix
    private Matrix mShaderMatrix;
    //draw view
    private Paint mViewPaint;
    //draw wave
    private Paint mWavePaint;
    // paint to draw border
    private Paint mBorderPaint;
    //view width
    private int mViewWidth;
    //view height
    private int mViewHeight;

    /**
     * the default water height
     * 默认的水位
     */
    private float mDefaultWaterLevel;

    /**
     * the ratio of water level ampltitude
     * 波浪偏离水面的垂直距离（振幅）对应的比率（振幅：A）
     */
    private float mAmplitudeRatio = DEFAULT_AMPLITUDE_RATIO;
    /**
     * the ratio of an complete wave length in horizontal
     * 完整波浪长度（波长）的比率
     */
    private float mWaveLengthRatio = DEFAULT_WAVE_LENGTH_RATIO;
    /**
     * the ratio of water height among the view height
     * 波浪静止时距离水面的距离（水位）(初相：h)
     */
    private float mWaterLevelRatio = DEFAULT_WATER_LEVEL_RATIO;
    /**
     * horizontal offset ratio relative to the initial position
     * 波浪相对于起始位置的偏移量
     */
    private float mWaveShiftRatio = DEFAULT_WAVE_SHIFT_RATIO;
    /**
     * the color of behind wave
     * 后浪颜色
     */
    private int mBehindWaveColor;
    /**
     * the color of front wave
     * 前浪颜色
     */
    private int mFrontWaveColor;

    /**
     * animator helper
     */
    private WaveAnimatorHelper mWaveAnimatorHelper;

    public WaveView(Context context) {
        this(context, null, 0);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initParamsAndValues(attrs);
    }

    //init params
    private void initParamsAndValues(AttributeSet attrs) {
        Resources mResources = getResources();
        mShaderMatrix = new Matrix();
        //init paint
        mViewPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWavePaint.setStrokeWidth(2);

        TypedArray array = mResources.obtainAttributes(attrs, R.styleable.WaveView);
        mShapeType = array.getInt(R.styleable.WaveView_shape_type, SHAPE_RECTANGLE);//init border shape
        //init wave color
        mBehindWaveColor = array.getColor(R.styleable.WaveView_behind_color, 0x555351);
        mFrontWaveColor = array.getColor(R.styleable.WaveView_front_color, 0x656361);
        array.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        createShader();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(width, height);
        mViewHeight = getMeasuredHeight();
        mViewWidth = getMeasuredWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //modify paint shader according to mShowWave state
        if (mShowWave && mWaveShader != null) {
            //set shader for paint
            if (mViewPaint.getShader() == null) {
                mViewPaint.setShader(mWaveShader);
            }

            //Set the matrix to scale
            //the two object decides the size of wave:
            //mWaveLengthRatio for width, mAmplitudeRatio for height
            mShaderMatrix.setScale(
                    mWaveLengthRatio / DEFAULT_WAVE_LENGTH_RATIO,
                    mAmplitudeRatio / DEFAULT_AMPLITUDE_RATIO,
                    0,
                    mDefaultWaterLevel);

            //Postconcats the matrix with the specified translation.
            //the two object decides the start position
            mShaderMatrix.postTranslate(
                    mWaveShiftRatio * mViewWidth,
                    (DEFAULT_WATER_LEVEL_RATIO - mWaterLevelRatio) * mViewHeight);

            //Set the shader's local matrix.
            mWaveShader.setLocalMatrix(mShaderMatrix);

            //init border width
            float borderWidth = mBorderPaint == null ?
                    0f : mBorderPaint.getStrokeWidth();

            switch (mShapeType) {//the border shape
                case SHAPE_CIRCLE:
                    float cx = mViewWidth / 2.0f;
                    float cy = mViewHeight / 2.0f;

                    if (borderWidth > 0) {
                        canvas.drawCircle(cx, cy,
                                (mViewWidth - borderWidth) / 2.0f - 1.0f,
                                mBorderPaint);
                    }
                    float radius = mViewWidth / 2.0f - borderWidth;
                    canvas.drawCircle(cx, cy, radius, mViewPaint);
                    break;
                case SHAPE_RECTANGLE:

                    if (borderWidth > 0) {
                        canvas.drawRect(
                                borderWidth / 2.0f,
                                borderWidth / 2.0f,
                                mViewWidth - borderWidth / 2.0f - 0.5f,
                                mViewHeight - borderWidth / 2.0f - 0.5f,
                                mBorderPaint);
                    }
                    canvas.drawRect(borderWidth,
                            borderWidth,
                            mViewWidth - borderWidth,
                            mViewHeight - borderWidth,
                            mViewPaint);
                    break;
            }
        } else {
            mViewPaint.setShader(null);
        }
    }

    /**
     * Create the shader with default waves which repeat horizontally,
     * and clamp vertically.
     */
    private void createShader() {
        //默认的频率
        double defaultAngularFrequency = 2.0f * Math.PI / DEFAULT_WAVE_LENGTH_RATIO / mViewWidth;
        //the default water anplitude（默认的水位振幅）
        float defaultAmplitude = mViewHeight * DEFAULT_AMPLITUDE_RATIO;
        mDefaultWaterLevel = mViewHeight * DEFAULT_WATER_LEVEL_RATIO;//水位高度
        //the default complete wave length(默认的波长)
        float defaultWaveLength = mViewWidth;

        Bitmap bitmap = Bitmap.createBitmap(mViewWidth, mViewHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        //draw default waves into the bitmap
        // y=Asin(ωx+φ)+h
        int endX = mViewWidth + 1;
        int endY = mViewHeight + 1;

        float[] waveY = new float[endX];
        mWavePaint.setColor(mBehindWaveColor);

        for (int beginX = 0; beginX < endX; beginX++) {
            double wx = beginX * defaultAngularFrequency;
            float beginY = (float) (defaultAmplitude * Math.sin(wx) + mDefaultWaterLevel);
            canvas.drawLine(beginX, beginY, beginX, endY, mWavePaint);

            waveY[beginX] = beginY;
        }

        mWavePaint.setColor(mFrontWaveColor);
        final int wave2Shift = (int) (defaultWaveLength / 5);

        for (int beginX = 0; beginX < endX; beginX++) {
            canvas.drawLine(beginX, waveY[(beginX + wave2Shift) % endX],
                    beginX, endY, mWavePaint);
        }

        //use bitmap to create the shader
        mWaveShader = new BitmapShader(bitmap,
                Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
        mViewPaint.setShader(mWaveShader);
    }

    /**
     * Set simple animator helper to view.
     */
    public void setSimpleAnimatorToView() {
        SimpleWaveAnimatorImp helper = new SimpleWaveAnimatorImp(this);
        setWaveAnimatorHelper(helper);
    }

    /**
     * Get animator helper to view.
     *
     * @return the wave ripple helper
     */
    public WaveAnimatorHelper getWaveAnimatorHelper() {
        if (mWaveAnimatorHelper == null)
            return null;
        else
            return mWaveAnimatorHelper;
    }

    /**
     * Set customer animator helper to view.
     *
     * @param waveAnimatorHelper the wave ripple helper
     */
    public void setWaveAnimatorHelper(WaveAnimatorHelper waveAnimatorHelper) {
        if (waveAnimatorHelper == null) return;
        this.mWaveAnimatorHelper = waveAnimatorHelper;
    }

    /**
     * Set width and color for border.
     *
     * @param width the border width
     * @param color the border color
     */
    public void setBorder(int width, int color) {
        if (mBorderPaint == null) {
            mBorderPaint = new Paint();
            mBorderPaint.setAntiAlias(true);
            mBorderPaint.setStyle(Paint.Style.STROKE);
        }
        mBorderPaint.setColor(color);
        mBorderPaint.setStrokeWidth(width);

        invalidate();
    }

    public void setWaveColor(int behindWaveColor, int frontWaveColor) {
        mBehindWaveColor = behindWaveColor;
        mFrontWaveColor = frontWaveColor;

        if (getWidth() > 0 && getHeight() > 0) {
            // need to recreate shader when color changed
            mWaveShader = null;
            createShader();
            invalidate();
        }
    }

    /**
     * Change the border shape.
     *
     * @param shapeType shapetype is an integer value {@see SHAPE_CIRCLE &SHAPE_RECTANGLE}
     */
    public void setShapeType(int shapeType) {
        mShapeType = shapeType;
        invalidate();
    }

    public float getWaveShiftRatio() {
        return mWaveShiftRatio;
    }

    /**
     * Shift the wave hoorizontally according to <code>waveShiftRatio</code>.
     *
     * @param WaveShiftRatio Should be 0 ~ 1. Default to be 0.
     *                       Result of waveShiftRatio multiples
     *                       width of WaveView is the length to shift.
     */
    public void setWaveShiftRatio(float WaveShiftRatio) {
        if (mWaveShiftRatio != WaveShiftRatio) {
            this.mWaveShiftRatio = WaveShiftRatio;
            invalidate();
        }
    }

    public float getWaterLevelRatio() {
        return mWaterLevelRatio;
    }

    /**
     * Set water level according to <code>waterLevelRatio</code>.
     *
     * @param waterLevelRatio Should be 0 ~ 1. Default to be 0.5.
     *                        Ratio of water level to WaveView height.
     */
    public void setWaterLevelRatio(float waterLevelRatio) {
        if (mWaterLevelRatio != waterLevelRatio) {
            this.mWaterLevelRatio = waterLevelRatio;
            invalidate();
        }
    }

    public float getAmplitudeRatio() {
        return mAmplitudeRatio;
    }

    /**
     * Set vertical size of wave according to <code>amplitudeRatio</code>
     *
     * @param amplitudeRatio Default to be 0.05. Result of amplitudeRatio + waterLevelRatio should be less than 1.
     *                       Ratio of amplitude to height of WaveView.
     */
    public void setAmplitudeRatio(float amplitudeRatio) {
        if (mAmplitudeRatio != amplitudeRatio) {
            this.mAmplitudeRatio = amplitudeRatio;
            invalidate();
        }
    }

    public float getWaveLengthRatio() {
        return mWaveLengthRatio;
    }

    /**
     * Set horizontal size of wave according to <code>waveLengthRatio</code>
     *
     * @param waveLengthRatio Default to be 1.
     *                        Ratio of wave length to width of WaveView.
     */
    public void setWaveLengthRatio(float waveLengthRatio) {
        this.mWaveLengthRatio = waveLengthRatio;
    }

    public void setShowWave(boolean showWave) {
        this.mShowWave = showWave;
    }
}
