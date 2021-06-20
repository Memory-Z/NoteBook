package com.inz.z.base.view.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.annotation.NonNull
import androidx.appcompat.widget.TintTypedArray
import com.inz.z.base.R

/**
 *
 * 标签 容器布局.
 * ====================================================
 * Create by 11654 in 2021/5/25 20:36
 */
class TagBoxLayout : RelativeLayout {


    companion object {
        private const val TAG = "TagBoxLayout"
        private const val DEFAULT_LAYOUT_MARGIN = 10
    }

    private var mView: View? = null
    private var mContext: Context? = null
    private val tagLayoutList: MutableList<TagLayout> = mutableListOf()

    constructor(context: Context?) : super(context) {
        this.mContext = context
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context,
        attrs,
        defStyleAttr) {
        this.mContext = context
        initViewStyle(attrs)
        initView()
    }


    @SuppressLint("RestrictedApi")
    private fun initViewStyle(attrs: AttributeSet?) {
        val array =
            TintTypedArray.obtainStyledAttributes(mContext, attrs, R.styleable.TagBoxLayout, 0, 0)
        array.recycle()
    }

    private fun initView() {
        this.mView = this
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        val baseWidth = r - l - paddingStart - paddingEnd
        val baseStart = paddingStart
        var usableWidth = baseWidth
        var startX = baseStart
        var startY = paddingTop
        for (tagLayout in tagLayoutList) {
            val layoutWidth = tagLayout.width
            val layoutHeight = tagLayout.height
            // 布局宽度 大于 可用宽度。
            if (layoutWidth + DEFAULT_LAYOUT_MARGIN > usableWidth) {
                // 非 行首内容， 换行，置首
                if (startX != baseStart) {
                    startY += layoutHeight + DEFAULT_LAYOUT_MARGIN
                    startX = baseStart
                    usableWidth = baseWidth
                }
            }
            tagLayout.layout(
                startX,
                startY,
                startX + layoutWidth,
                startY + layoutHeight
            )
            startX += layoutWidth + DEFAULT_LAYOUT_MARGIN
            usableWidth -= layoutWidth
            if (startX > baseWidth) {
                startY += layoutHeight + DEFAULT_LAYOUT_MARGIN
                startX = baseStart
                usableWidth = baseWidth
            }
        }
    }

    fun addTagLayout(@NonNull tagLayout: TagLayout) {
        if (tagLayout.parent != null) {
            Log.w(TAG, "addTagLayout: this tagLayout have parent. ")
            return
        }
        this.addView(tagLayout)
        tagLayoutList.add(tagLayout)
        invalidate()
    }
}