package com.inz.z.base.view.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;

import com.inz.z.base.util.L;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Dot view
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/07/09 09:40.
 */
public class DotView extends View implements ValueAnimator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {
    private static final String TAG = DotView.class.getSimpleName();

    private Paint dotPaint;
    private int pointSize = 32;
    private int layoutHeight = 0;
    private ValueAnimator valueAnimator;
    private boolean isDown = false;
    private AtomicInteger dotY = new AtomicInteger(0);
    private int startY = 0;

    public DotView(Context context) {
        super(context);
        init();
    }

    public DotView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DotView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        dotPaint = new Paint();
        dotPaint.setAntiAlias(true);
        dotPaint.setColor(Color.BLACK);
        dotPaint.setStrokeCap(Paint.Cap.ROUND);
        dotPaint.setStrokeWidth(pointSize);
        dotPaint.setStyle(Paint.Style.FILL);

        dotY.set(Math.max(startY, pointSize / 2));
    }

    private void initAnimator(int start, int end, boolean restart) {
        L.i(TAG, "initAnimator: start - " + start + "<---->" + end);
        isDown = start < end;
        if (valueAnimator != null) {
            valueAnimator.end();
        }
        valueAnimator = ValueAnimator.ofInt(start, end);
        valueAnimator.setRepeatMode(restart ? ValueAnimator.RESTART : ValueAnimator.REVERSE);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(this);
        long duration = 1000;
        if (!restart) {
            duration = (int) (Math.abs(end - start) * 1F / Math.max(start, end) * 1000);
        }
        valueAnimator.setDuration(duration);
        valueAnimator.addListener(this);
        valueAnimator.start();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        layoutHeight = height - pointSize;
        setMeasuredDimension(pointSize, height);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (dotPaint.getStrokeWidth() != pointSize) {
            dotPaint.setStrokeWidth(pointSize);
        }
//        canvas.drawColor(Color.GREEN);
        canvas.drawPoint(getWidth() / 2F, dotY.get(), dotPaint);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        layoutHeight = bottom - top - pointSize / 2 - getPaddingBottom() - getPaddingTop();
        int dotY = Math.max(startY, pointSize / 2);
        if (dotY >= layoutHeight - pointSize / 2) {
            dotY = layoutHeight - pointSize / 2 - 1;
        }
        this.dotY.set(dotY);
        initAnimator(dotY, layoutHeight, false);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (valueAnimator != null) {
            valueAnimator.end();
            valueAnimator = null;
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        int dotY = (int) animation.getAnimatedValue();
        int dif = this.dotY.get() - dotY;
        int s = Math.abs(dif);
        float b = layoutHeight / 4F * 3;
        if (s > b) {
            return;
        }
        this.dotY.set(dotY);
        invalidate();
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {

    }

    @Override
    public void onAnimationCancel(Animator animation) {
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
        ValueAnimator valueAnimator = (ValueAnimator) animation;
        if (ValueAnimator.RESTART != valueAnimator.getRepeatMode()) {
            if (isDown) {
                initAnimator(layoutHeight, pointSize / 2, true);
            } else {
                initAnimator(pointSize / 2, layoutHeight, true);
            }
            return;
        }
        if (isDown) {
            valueAnimator.setIntValues(layoutHeight, pointSize / 2);
        } else {
            valueAnimator.setIntValues(pointSize / 2, layoutHeight);
        }
        isDown = !isDown;
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.setDuration(1000);
    }

    public int getPointSize() {
        return pointSize;
    }

    public void setPointSize(int pointSize) {
        this.pointSize = pointSize;
        if (pointSize / 2 > this.dotY.get()) {
            this.dotY.set(pointSize / 2);
        }
    }

    public void setStartY(int startY) {
        this.startY = startY;
        if (startY > pointSize / 2) {
            this.dotY.set(startY);
        }
    }
}
