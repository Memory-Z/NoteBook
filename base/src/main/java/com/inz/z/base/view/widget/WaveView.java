package com.inz.z.base.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

/**
 * Wave View
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/07/09 09:45.
 */
public class WaveView extends LinearLayout {

    private static final String TAG = WaveView.class.getSimpleName();

    private Context mContext;
    private Paint linePaint;
    private int dotNum = 30;
    private int layoutHeight = 0, layoutWidth = 0;

    private DotView[] dotViews;
    private int dotSize = 24;
    private int startX = 0;
    private float spaceY = 1F;

    public WaveView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
        linePaint = new Paint();
        linePaint.setStrokeWidth(2);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.GREEN);

        initView();
    }

    private void initView() {
        dotViews = new DotView[dotNum];
        for (int i = 0; i < dotNum; i++) {
            DotView dotView = new DotView(mContext);
            dotView.setPointSize(dotSize);
            dotViews[i] = dotView;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        layoutHeight = MeasureSpec.getSize(heightMeasureSpec);
        layoutWidth = MeasureSpec.getSize(widthMeasureSpec);
        startX = (layoutWidth - dotSize * dotNum) / (dotNum + 1);
        if (startX < 0) {
            layoutWidth = dotSize * dotNum + dotSize / 2 * (dotNum + 1);
            startX = dotSize / 2;
        }
        if (dotNum > 1) {
            spaceY = (layoutHeight * 1F - dotSize) / (dotNum - 1);
        } else {
            spaceY = layoutHeight / 2F;
        }
        setMeasuredDimension(layoutWidth, layoutHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int centerX = (r - l - getPaddingStart() - getPaddingEnd()) / 2;
        int centerY = (b - t - getPaddingTop() - getPaddingBottom()) / 2;
        int top = getPaddingTop();
        int bottom = b - getPaddingBottom() - t;
        for (int i = 0; i < dotNum; i++) {
            DotView dotView = dotViews[i];
            int dotSize = dotView.getPointSize();
            int left = startX * (i + 1) + dotSize * i;
            int right = left + dotSize;
            int cTop = top + dotSize / 2;
            int cBottom = bottom - dotSize / 2;
            if (dotView.getParent() != null) {
                removeView(dotView);
            }
            int y = (int) Math.floor(spaceY * i);
            dotView.setStartY(y);
            dotView.layout(left, cTop, right, cBottom);
            addView(dotView);
        }
    }

}
