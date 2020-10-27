package com.inz.z.note_book.view.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.TintTypedArray
import com.inz.z.note_book.R
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 编辑光标
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/10/23 14:31.
 */
class EditCursorView : View {

    companion object {
        private const val TAG = "EditCursorView"

        /**
         * 刷新
         */
        private const val HANDLER_REFRESH = 0x0000E031

        private const val REFRESH_DELAYED = 1000L

        private const val DEFAULT_CURSOR_WIDTH = 4F
    }

    private var mContext: Context? = null
    private lateinit var cursorPaint: Paint
    private var cursorBitmap: Bitmap? = null
    private var cursorColor: Int = Color.BLACK
    private var cursorWidth: Float = 2F

    /**
     * 是否显示 光标
     */
    private val showCursor = AtomicBoolean(false)
    private var refreshDelayTime = REFRESH_DELAYED

    private val cursorHandler: Handler = Handler(CursorHandlerCallback())

    constructor(context: Context?) : super(context) {
        this.mContext = context
        initPaint()
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.mContext = context
        initStyle(attrs)
        initPaint()
        initView()
    }

    @SuppressLint("RestrictedApi")
    private fun initStyle(attrs: AttributeSet?) {
        val array =
            TintTypedArray.obtainStyledAttributes(mContext, attrs, R.styleable.EditCursorView, 0, 0)
        cursorColor = array.getColor(R.styleable.EditCursorView_ecursor_color, Color.WHITE)
        refreshDelayTime =
            array.getFloat(R.styleable.EditCursorView_ecursor_delay, REFRESH_DELAYED.toFloat())
                .toLong()

        array.recycle()
    }

    private fun initView() {

    }

    private fun initPaint() {
        cursorPaint = Paint()
        cursorPaint.apply {
            isAntiAlias = true
            color = cursorColor
            strokeCap = Paint.Cap.SQUARE
            style = Paint.Style.FILL
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val width: Int
        if (cursorBitmap != null) {
            val bW = this.width
            val bH = this.height
            val bitmapScale = bH * 1F / height
            width = (bitmapScale * bW).toInt()
        } else {
            width = dp2px(DEFAULT_CURSOR_WIDTH)
        }
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            if (showCursor.get()) {
                it.drawRect(0F, 0F, width.toFloat(), height.toFloat(), cursorPaint)
            }
        }
        // 延迟更新
        cursorHandler.sendEmptyMessageDelayed(HANDLER_REFRESH, refreshDelayTime)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        cursorHandler.removeCallbacksAndMessages(null)
    }

    /**
     * 转换
     */
    private fun dp2px(dpiValue: Float): Int {
        return ((mContext?.resources?.displayMetrics?.density ?: 1F) * dpiValue + .5F).toInt()
    }

    private inner class CursorHandlerCallback : Handler.Callback {
        override fun handleMessage(msg: Message): Boolean {
            when (msg.what) {
                HANDLER_REFRESH -> {
                    val show = showCursor.get()
                    showCursor.set(!show)
                    // TODO: 2020/10/23  REFRESH View
                    this@EditCursorView.invalidate()
                }
            }
            return true
        }
    }
}