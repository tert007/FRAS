package com.example.alexander.fastreading;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Simple single android view component that can be used to showing a round progress bar.
 * It can be customized with size, stroke size, colors and text etc.
 * Progress change will be animated.
 * Created by Kristoffer, http://kmdev.se
 */
public class CircularProgressBar extends View {

    private int mViewWidth;
    private int mViewHeight;


    private float mSweepAngle = 0;              // How long to sweep from mStartAngle
    private float mMaxSweepAngle = 360;         // Max degrees to sweep = full circle

    private int mStrokeWidth = 10;              // Width of outline
    private int currentProgress = 0;
    private int mMaxProgress = 1000;             // Max progress to use


    private boolean mDrawText = true;           // Set to true if progress text should be drawn

    private int mProgressColor = Color.BLACK;   // Outline color

    private final Paint mPaint;                 // Allocate paint outside onDraw to avoid unnecessary object creation

    public CircularProgressBar(Context context) {
        this(context, null);
    }

    public CircularProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public int getMax() {
        return mMaxProgress;
    }

    public void setMax(int maxProgress) {
        this.mMaxProgress = maxProgress;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initMeasurments();

        drawOutlineArc(canvas);
    }

    private void initMeasurments() {
        mViewWidth = getWidth();
        mViewHeight = getHeight();
    }

    private void drawOutlineArc(Canvas canvas) {

        final int diameter = Math.min(mViewWidth, mViewHeight) - (mStrokeWidth * 2);

        final RectF outerOval = new RectF(mStrokeWidth, mStrokeWidth, diameter, diameter);

        mPaint.setColor(mProgressColor);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawArc(outerOval, -90, mSweepAngle, false, mPaint);
    }

    private float calcSweepAngleFromProgress(int progress) {
        return (mMaxSweepAngle / mMaxProgress) * progress;
    }

    public void setProgress(int progress) {
        if (progress <= mMaxProgress) {
            currentProgress = progress;
            mSweepAngle = calcSweepAngleFromProgress(progress);
            invalidate();
        }
    }

    public void setProgressColor(int color) {
        mProgressColor = color;
        invalidate();
    }

    public void setProgressWidth(int width) {
        mStrokeWidth = width;
        invalidate();
    }


}