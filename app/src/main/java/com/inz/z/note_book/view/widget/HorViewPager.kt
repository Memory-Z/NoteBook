package com.inz.z.note_book.view.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager
import com.inz.z.base.util.L

/**
 * 横向滑动 View Pager
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/11/10 10:16.
 */
open class HorViewPager : ViewPager {

    companion object {
        const val TAG = "HorViewPager"
    }

    var canScroll = false

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val dispatch = super.dispatchTouchEvent(ev);
        L.i(TAG, "dispatchTouchEvent: $dispatch")
        return dispatch
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return if (canScroll) {
            super.onInterceptTouchEvent(ev)
        } else {
            false
        }
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return if (canScroll) {
            super.onTouchEvent(ev)
        } else {
            true
        }
    }
}
