package com.inz.z.note_book.view.widget

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.view.drawToBitmap
import com.inz.z.base.util.L
import java.security.MessageDigest

/**
 * 倒计时
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/08/19 16:18.
 */
class CountDownView : View {
    companion object {
        private const val TAG = "CountDownView"
    }

    private var mContext: Context?


    private lateinit var dotPaint: Paint
    private var ringSize = 20
    private var dotColor = Color.BLACK
    private lateinit var ringPath: Path

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
        initPaint()
    }

    private fun initPaint() {
        dotPaint = Paint()
        dotPaint.apply {
            this.isAntiAlias = true
            this.color = dotColor
            this.strokeCap = Paint.Cap.ROUND
            this.style = Paint.Style.FILL
        }

        ringPath = Path()

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val size = Math.min(height, width)

        val maxLength = size / 4
        var angle: Double = currentAngle.toDouble()
        val step = 360F / maxLength / 20F
        ringPath.reset()
        ringPath.moveTo(size / 2F, size / 2F)
        for (i in 0..maxLength) {
            val xPath = i * Math.cos(angle)
            val yPath = i * Math.sin(angle)
            L.i(TAG, "xPath $xPath ! yPath $yPath  ~ $angle --- $step")
            ringPath.lineTo(size / 2F + xPath.toFloat(), size / 2F + yPath.toFloat())
//            ringPath.arcTo()
            angle += step
        }
        setMeasuredDimension(size, size)
    }

    private var currentAngle: Float = -180F

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val x = width / 2F
        val y = height / 4F

        dotPaint.style = Paint.Style.STROKE
        dotPaint.color = Color.RED
        canvas?.drawPath(ringPath, dotPaint)

        dotPaint.style = Paint.Style.FILL
        dotPaint.color = Color.BLACK
        canvas?.save()
        canvas?.rotate(currentAngle, width / 2F, height / 2F)
        canvas?.drawLine(x, height / 2F, x, y, dotPaint)
        canvas?.drawOval(x - ringSize, y - ringSize, x + ringSize, y + ringSize, dotPaint)
        canvas?.restore()
//        dotPaint.color = Color.GREEN
//        canvas?.drawOval(
//            width / 2F - ringSize,
//            height / 2F - ringSize,
//            width / 2F + ringSize,
//            height / 2F + ringSize,
//            dotPaint
//        )
//        this.isDrawingCacheEnabled = true
//        this.buildDrawingCache()
//        backgroundBitmap = this.drawingCache
//        this.isDrawingCacheEnabled = false

        if (currentAngle > 360) {
            currentAngle = currentAngle % 360
        }
        currentAngle += 6
//        L.i(TAG, "onDraw: $currentAngle")
//        postInvalidateDelayed(1000)
        invalidate()
//        postInvalidate()
    }

    ///////////////////////////////////////////////////////////////////////////
    // Open
    ///////////////////////////////////////////////////////////////////////////

    interface CountDownViewListener {
//        fun
    }
}