package com.inz.z.note_book.view.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.TintTypedArray
import androidx.core.content.ContextCompat
import com.inz.z.note_book.R

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/05/22 15:06.
 */
class ScreenRefreshView : View {
    companion object {
        private const val TAG = "ScreenRefreshView"

        private const val HANDLER_REFRESH = 0x00770001
    }

    private var mContext: Context?
    private val bitmapList = mutableListOf<Bitmap>()

    private var bitmapMaxSize = 100F
    private var bitmapHeight: Float = 1F
    private var speed: Long = 20L
    private var granularity = 40
    private lateinit var dotPaint: Paint
    private lateinit var drawablePaint: Paint
    private val refreshHandler = Handler(RefreshHandlerCallback())

    constructor(context: Context?) : super(context) {
        this.mContext = context
        initPaint()
    }

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.mContext = context
        attrs?.let { initStyle(it) }
        initPaint()

    }

    @SuppressLint("RestrictedApi")
    private fun initStyle(attrs: AttributeSet) {
        val typedArray = TintTypedArray.obtainStyledAttributes(
            mContext!!,
            attrs,
            R.styleable.ScreenRefreshView,
            0, 0
        )
        typedArray.recycle()
    }

    /**
     * 初始化 画笔
     */
    private fun initPaint() {
        dotPaint = Paint()
        dotPaint.apply {
            this.isAntiAlias = true
            this.color = ContextCompat.getColor(mContext!!, R.color.colorPrimary)
        }
        drawablePaint = Paint().apply {
            this.isAntiAlias = true
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        if (granularity > 0) {
            bitmapMaxSize = height.toFloat() / granularity
            bitmapHeight = bitmapMaxSize * 4
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // current need drawable index .
        val currentDrawableIndex = bitmapList.size
        val lineBitmap = getLineBitmap()

        canvas?.drawBitmap(
            lineBitmap,
            0F,
            0F,
            drawablePaint
        )
        for (bitmap in bitmapList.withIndex()) {
            canvas?.drawBitmap(
                bitmap.value,
                0F,
                (bitmapHeight + .5F) * (bitmap.index + 1).toFloat(),
                drawablePaint
            )
        }

        bitmapList.add(0, lineBitmap)
        if (currentDrawableIndex == granularity) {
            bitmapList.removeAt(granularity)
        }
//        postInvalidateDelayed(speed)
        postDelayed(
            {
                refreshHandler.sendEmptyMessage(HANDLER_REFRESH)
            },
            speed
        )
    }


    private fun getLineBitmap(): Bitmap {
        val size = (Math.random() * (height / granularity) + 1).toInt()
        val h = (bitmapHeight + .5F).toInt().toFloat()
        val bitmap =
            Bitmap.createBitmap(width, h.toInt(), Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        val dotSize = (bitmapMaxSize + .5F).toInt().toFloat()
        val dif = (h - dotSize) / 2
        for (i in 0..size) {
            val y = (Math.random() * width + 1).toInt()
            canvas.drawOval(
                y - (dotSize / 2),
                dif,
                y + (dotSize / 2),
                h - dif,
                dotPaint
            )
        }
        return bitmap
    }


    private inner class RefreshHandlerCallback : Handler.Callback {
        override fun handleMessage(msg: Message): Boolean {
            when (msg.what) {
                HANDLER_REFRESH -> {
                    invalidate()
                }
                else -> {

                }
            }
            return true
        }
    }


}