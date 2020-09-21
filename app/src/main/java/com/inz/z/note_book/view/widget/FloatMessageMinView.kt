package com.inz.z.note_book.view.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.TintTypedArray
import androidx.core.view.LayoutInflaterCompat
import com.inz.z.note_book.R

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/09/18 09:28.
 */
class FloatMessageMinView : LinearLayout {
    companion object {
        private const val TAG = "FloatMessageMinView"
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

        }
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