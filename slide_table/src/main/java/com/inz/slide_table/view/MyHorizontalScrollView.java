package com.inz.slide_table.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.Scroller;


/**
 * 自定义 水平 滑动
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/11/03 11:19.
 */
public class MyHorizontalScrollView extends HorizontalScrollView {

    private static final String TAG = "MyHorizontalScrollView";

    private Context mContext;
    private MyHorizontalScrollView linkedScorllView;
//    private Scroller mScroller;
//    private VelocityTracker velocityTracker;

    public MyHorizontalScrollView(Context context) {
        this(context, null);
    }

    public MyHorizontalScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
//        velocityTracker = VelocityTracker.obtain();
//        mScroller = new Scroller(mContext);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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
//        View view = getChildAt(0);
//        int childWidth = view.getWidth();
//        float rawX = ev.getRawX();
//        Log.i(TAG, "onInterceptTouchEvent: " + intercept + " --- Width : " + childWidth + " VIEW = " + view);
//        velocityTracker.addMovement(ev);
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN: {
//                break;
//            }
//            case MotionEvent.ACTION_MOVE: {
//                float x = velocityTracker.getXVelocity();
//                Log.i(TAG, "onInterceptTouchEvent: " + rawX + " ----> " + x);
//                if (rawX == 0) {
//
//                }
//                break;
//            }
//            default: {
//                break;
//            }
//        }
        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
//        if (getChildCount() > 0) {
//            View lastView = getChildAt(0);
//            int[] lastViewLocation = new int[2];
//            lastView.getLocationOnScreen(lastViewLocation);
//            float windowX = getX();
//            Log.i(TAG, "onTouchEvent: " + lastViewLocation[0] + " - " + windowX);
//            float screenWidth = lastView.getContext().getResources().getDisplayMetrics().widthPixels;
//            float childViewWidth = lastView.getWidth();
//            // 屏幕宽度与子布局 x 坐标的位置 差， 结果等于 子布局宽度，表明子布局已处于最右侧 。
//            if (screenWidth + Math.abs(lastViewLocation[0]) == childViewWidth) {
//                return true;
//            }
//        }
//            Log.i(TAG, "onTouchEvent: " + lastViewLocation[0] + " --- " + lastViewLocation[1] + " --- " + getWidth()
//                    + "--- " + lastView.getWidth() + "===" + screenWidth);

//            return screenWidth - lastViewLocation[0] == childViewWidth;
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if (getChildCount() > 0) {
//            View lastView = getChildAt(0);
//            int[] lastViewLocation = new int[2];
//            lastView.getLocationOnScreen(lastViewLocation);
//            float screenWidth = lastView.getContext().getResources().getDisplayMetrics().widthPixels;
//            float childViewWidth = lastView.getWidth();
//            // 屏幕宽度与子布局 x 坐标的位置 差， 结果等于 子布局宽度，表明子布局已处于最右侧 。
//            return screenWidth + Math.abs(lastViewLocation[0]) == childViewWidth;
//        }
        boolean dispatch = super.dispatchTouchEvent(ev);
//        Log.i(TAG, "dispatchTouchEvent: " + dispatch);
        return dispatch;
    }

    ///////////////////////////////////////////////////////////////////////////
    // OPEN
    ///////////////////////////////////////////////////////////////////////////

    public void setLinkedScorllView(MyHorizontalScrollView linkedScorllView) {
        this.linkedScorllView = linkedScorllView;
    }
}
