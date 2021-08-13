package com.inz.z.note_book.view.widget.add_content

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.TintTypedArray
import androidx.core.content.ContextCompat
import com.inz.z.base.util.L
import com.inz.z.note_book.R

/**
 *
 * 标签编辑 EditText
 * ====================================================
 * Create by 11654 in 2021/4/18 21:35
 */
class TagEditText : AppCompatEditText, View.OnFocusChangeListener {

    companion object {
        private const val TAG = "TagEditText"
        private val gapColorStateList = ColorStateList.valueOf(Color.BLACK)
        private const val BASE_STROKE_WIDTH = 1
        private const val BASE_DASH_WIDTH = 10
        private const val BASE_DASH_GAP = 2
    }

    interface TagListener {
        fun onFinish(v: View?)
    }

    var listener: TagListener? = null

    private var mContext: Context? = null
    private var cornerRadius: Float = 0F
    private var dashWidth = 0F
    private var dashGap = 0F
    private var strokeWidth = 2

    constructor(context: Context) : super(context) {
        this.mContext = context
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context,
        attrs,
        defStyleAttr) {
        this.mContext = context
        initViewStyle(attrs)
        initView()
    }


    @SuppressLint("RestrictedApi")
    private fun initViewStyle(attrs: AttributeSet?) {
        val array =
            TintTypedArray.obtainStyledAttributes(mContext, attrs, R.styleable.TagEditText, 0, 0)
        array.recycle()
    }

    private fun initView() {
        isFocusableInTouchMode = true
        gravity = Gravity.CENTER_VERTICAL
        maxLines = 1
        isSingleLine = true
        dashWidth = dp2px(BASE_DASH_WIDTH).toFloat()
        dashGap = dp2px(BASE_DASH_GAP).toFloat()
        strokeWidth = dp2px(BASE_STROKE_WIDTH)
        this.onFocusChangeListener = this
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        L.i(TAG, "onMeasure: --->> ")
//        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
//        val width = MeasureSpec.getSize(widthMeasureSpec)
//        val height = MeasureSpec.getSize(heightMeasureSpec)
        // 角度为绘制高度一半
        cornerRadius = measuredHeight / 2F
        minWidth = (cornerRadius * 3).toInt()
        updatePadding()
        updateBackgroundDrawable(true)
    }

    private fun updatePadding() {
        val pStart = cornerRadius / 2
        val pTop = paddingTop
        val pEnd = cornerRadius / 2
        val pBottom = paddingBottom
        setPadding(pStart.toInt(), pTop, pEnd.toInt(), pBottom)
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        L.i(TAG, "onFocusChange: --- hasFocus: $hasFocus")
        if (!hasFocus) {
            loseFocus()
        }
    }

    private fun dp2px(value: Int): Int {
        val density = resources.displayMetrics.density
        return (density * value + .5F).toInt()
    }

    private fun updateBackgroundDrawable(hasFocus: Boolean) {
        val drawable = ContextCompat.getDrawable(context, R.drawable.bg_tag_edit_text)
        if (drawable is GradientDrawable) {
            drawable.cornerRadius = cornerRadius
            if (hasFocus) {
                drawable.setStroke(strokeWidth, gapColorStateList, dashWidth, dashGap)
            } else {
                drawable.setStroke(strokeWidth, gapColorStateList)
            }
        }
        background = drawable
    }

    /**
     * 失去焦点
     */
    private fun loseFocus() {
        val contentStr = text.toString()
        if (!TextUtils.isEmpty(contentStr)) {
            updateBackgroundDrawable(false)
            isFocusableInTouchMode = false
            isFocusable = false
            isClickable = false
            listener?.onFinish(this)
        }
    }


    public fun onTouchOut() {
        loseFocus()
    }
}
