package com.inz.slide_table.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;

import androidx.recyclerview.widget.RecyclerView;


/**
 * 自定义 水平 滑动
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/11/03 11:19.
 */
public class MyHorizontalScrollView extends HorizontalScrollView {

    private static final String TAG = "MyHorizontalScrollView";

    private MyHorizontalScrollView linkedScorllView;

    public MyHorizontalScrollView(Context context) {
        super(context);
    }

    public MyHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int width = MeasureSpec.getSize(widthMeasureSpec);
//        Log.i(TAG, "onMeasure: " + this);
//        Log.i(TAG, "onMeasure: " + width);
//        if (linkedScorllView != null) {
//            int linkedWidth = linkedScorllView.getMeasuredWidth();
//            Log.i(TAG, "onMeasure: " + linkedWidth);
//            width = Math.max(width, linkedWidth);
//        }
//        setMeasuredDimension(width, MeasureSpec.getSize(heightMeasureSpec));
//        if (getChildCount() > 0) {
//            final View child = getChildAt(0);
//            if (child instanceof RecyclerView) {
//                final int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.UNSPECIFIED);
//                child.measure(childWidthMeasureSpec, heightMeasureSpec);
//            }
//        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (linkedScorllView != null) {
            linkedScorllView.scrollTo(l, t);
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = super.onInterceptTouchEvent(ev);
        View view = getChildAt(0);
        int childWidth = view.getWidth();
        Log.i(TAG, "onInterceptTouchEvent: " + intercept + " --- Width : " + childWidth + " VIEW = " + view);
        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean touch = super.onTouchEvent(ev);
        Log.i(TAG, "onTouchEvent: " + touch);
        return touch;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean dispatch = super.dispatchTouchEvent(ev);
        Log.i(TAG, "dispatchTouchEvent: " + dispatch);
        return dispatch;
    }

    ///////////////////////////////////////////////////////////////////////////
    // OPEN
    ///////////////////////////////////////////////////////////////////////////

    public void setLinkedScorllView(MyHorizontalScrollView linkedScorllView) {
        this.linkedScorllView = linkedScorllView;
    }
}
