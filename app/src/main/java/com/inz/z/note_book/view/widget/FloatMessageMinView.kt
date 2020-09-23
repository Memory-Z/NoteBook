package com.inz.z.note_book.view.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.TintTypedArray
import androidx.core.view.LayoutInflaterCompat
import com.inz.z.note_book.R
import kotlinx.android.synthetic.main.float_view_message_min.view.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/09/18 09:28.
 */
class FloatMessageMinView : LinearLayout {
    companion object {
        private const val TAG = "FloatMessageMinView"

        /**
         * min touch time
         */
        private const val MIN_TOUCH_TIME = 300L

        /**
         * max click time
         */
        private const val MAX_CLICK_TIME = 1000L
    }

    private var mContext: Context? = null
    private var mView: View? = null

    constructor(context: Context?) : super(context) {
        this.mContext = context
        initView()

    }

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.mContext = context
        initViewStyle(attrs)
        initView()
    }

    @SuppressLint("RestrictedApi")
    private fun initViewStyle(attrs: AttributeSet?) {
        val tint = TintTypedArray.obtainStyledAttributes(
            mContext,
            attrs,
            R.styleable.FloatMessageMinView,
            0,
            0
        )
        tint.recycle()
    }

    /**
     * 初始化 View
     */
    private fun initView() {
        if (mView == null) {
            mView =
                LayoutInflater.from(mContext).inflate(R.layout.float_view_message_min, this, true)
            float_message_min_iv.setOnClickListener {
                floatMessageMinViewListener?.onImageClick(it)
            }
            float_message_min_iv.isFocusable = false
            float_message_min_iv.isClickable = false


        }
    }

    private var lastOnTouchTime: Long = 0L
    private var touchMove = AtomicBoolean(false)

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val time = System.currentTimeMillis()
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                lastOnTouchTime = time
                touchMove.set(false)
                return false
            }
            MotionEvent.ACTION_MOVE -> {
                touchMove.set(true)
            }
            MotionEvent.ACTION_UP -> {
                val difTime = time - lastOnTouchTime
                if (difTime < MIN_TOUCH_TIME && MAX_CLICK_TIME > difTime && !touchMove.get()) {
                    this.performClick()
                    float_message_min_iv?.performClick()
                }
                touchMove.set(false)
            }
        }
        return !touchMove.get()
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    /**
     * 监听
     */
    interface FloatMessageMinViewListener {
        /**
         * 图像点击
         */
        fun onImageClick(v: View?)
    }

    var floatMessageMinViewListener: FloatMessageMinViewListener? = null

}