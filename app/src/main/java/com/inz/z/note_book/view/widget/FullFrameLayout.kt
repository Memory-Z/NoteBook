package com.inz.z.note_book.view.widget

import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import com.inz.z.base.util.L
import com.inz.z.note_book.R

/**
 * 全屏界面
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/11/12 14:23.
 */
class FullFrameLayout : FrameLayout {
    companion object {
        private const val TAG = "FullFrameLayout"

    }

    private var mContext: Context? = null
    private var childView: View? = null
    private var velocityTracker: VelocityTracker? = null

    private val childViewRect = Rect()


    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.mContext = context
        initView()

    }

    private fun initView() {
        velocityTracker = VelocityTracker.obtain()
        setBackgroundColor(ContextCompat.getColor(mContext!!, R.color.card_thread_color))
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = mContext?.resources?.displayMetrics?.widthPixels
            ?: MeasureSpec.getSize(widthMeasureSpec)
        val height = mContext?.resources?.displayMetrics?.heightPixels
            ?: MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        velocityTracker?.clear()
    }

    private inner class ChildViewTouchListenerImpl : OnTouchListener {

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {

            val rawX = event?.rawX ?: 0F
            val rawY = event?.rawY ?: 0F
            L.i(TAG, "onTouch: $rawX -- $rawY")
            velocityTracker?.addMovement(event)
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastX = rawX
                    lastY = rawY
                    childView?.getLocationInWindow(locationArray)
                    scrollOrientation = 0
                }
                MotionEvent.ACTION_MOVE -> {
                    var disX = rawX - lastX
                    var disY = rawY - lastY
                    L.i(
                        TAG,
                        "MotionEvent.ACTION_MOVE: $disX -- $disY -- $scrollOrientation --- ${velocityTracker?.xVelocity} -- ${velocityTracker?.yVelocity}"
                    )
                    when (scrollOrientation) {
                        0 -> {
                            // 默认无滑动
                            if (disY >= disX) {
                                // 竖向
                                scrollOrientation = 2
                                disX = 0F
                            } else {
                                // 竖向
                                scrollOrientation = 1
                                disY = 0F
                            }
                        }
                        1 -> {
                            // 横向
                            disY = 0F
                        }
                        2 -> {
                            // 竖向
                            disX = 0F
                        }
                    }
                    moveChildView(disX.toInt(), disY.toInt())
                    lastX = rawX
                    lastY = rawY

                }
                MotionEvent.ACTION_UP -> {
                    scrollOrientation = 0
                }
            }
            return false
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // OPEN
    ///////////////////////////////////////////////////////////////////////////


    fun addContentView(@LayoutRes layout: Int, layoutParams: LayoutParams) {
        childView = LayoutInflater.from(mContext).inflate(layout, null, false)
        childView?.apply {
            isClickable = true
            setOnTouchListener(ChildViewTouchListenerImpl())
        }
        addView(childView, layoutParams)
        childView?.getDrawingRect(childViewRect)
        L.i(TAG, "addContentView:  $childViewRect")
    }

    private var lastX = 0F
    private var lastY = 0F
    private val locationArray = IntArray(2)

    /**
     * 滑动方向，0：默认无滑动。1：横向；2：竖向
     */
    private var scrollOrientation = 0

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    /**
     * 移动子view
     * @param disX 移动 x 位置
     * @param disY 移动 y 位置
     */
    private fun moveChildView(disX: Int, disY: Int) {
        val screenWidth = this@FullFrameLayout.width
        val screenHeight = this@FullFrameLayout.height
        childView?.apply {
//            val rect = Rect()
//            getLocalVisibleRect(rect)
//            rect.offset(disX.toInt(), disY.toInt())
//            matrix.postTranslate(disX, disY)
            var start = left + disX
            start = Math.max(0, start)
            var top = top + disY
            top = Math.max(top, 0)
            var end = right + disX
            end = Math.min(end, screenWidth)
            var bottom = bottom + disY
            bottom = Math.min(bottom, screenHeight)
            if (start == 0) {
                if (end != screenWidth) {
                    end = width
                }
            }
            if (top == 0) {
                if (bottom != screenHeight) {
                    bottom = height
                }
            }
            if (end == screenWidth) {
                if (start != 0) {
                    start = end - width
                }
            }
            if (bottom == screenHeight) {
                if (top != 0) {
                    top = bottom - height
                }
            }
            L.i(TAG, "moveChildView: -  $start - $top - $end - $bottom")
            this.layout(start, top, end, bottom)
        }
    }
}