package com.inz.z.note_book.view.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.TintTypedArray
import com.inz.z.note_book.R

/**
 * 名字头像
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/07/20 10:05.
 */
public class NameImageView : View {

    private var mContext: Context? = null

    companion object {
        private val TAG = NameImageView::class.simpleName
    }

    private lateinit var nameTvPaint: Paint
    private lateinit var borderPaint: Paint
    private lateinit var backgroundPaint: Paint
    private var nameStr = ""
    private var nameTextSize = 16F
    private var nameColor: Int = 0x000000
    private var imageBackgroundColor: Int = 0xFFFFFF
    private var borderColor: Int = 0xFFF000
    private var borderWidth: Float = 0F
    private var borderVisibility = true
    private var nameLength = 2
    private lateinit var nameRect: Rect

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
        initStyle(attrs)
        initPaint()

    }

    /**
     * 初始化画笔
     */
    private fun initPaint() {
        nameTvPaint = Paint()
        nameTvPaint.apply {
            isAntiAlias = true
            textSize = nameTextSize
            textAlign = Paint.Align.CENTER
            strokeCap = Paint.Cap.ROUND
            color = nameColor
        }

        borderPaint = Paint()
        borderPaint.apply {
            isAntiAlias = true
            strokeCap = Paint.Cap.ROUND
            style = Paint.Style.STROKE
            strokeWidth = borderWidth
            color = borderColor
            strokeJoin = Paint.Join.ROUND

        }

        backgroundPaint = Paint()
        backgroundPaint.apply {
            isAntiAlias = true
            color = imageBackgroundColor
            style = Paint.Style.FILL
        }

        nameRect = Rect()
    }

    @SuppressLint("RestrictedApi")
    private fun initStyle(attrs: AttributeSet?) {
        val tintTypedArray = TintTypedArray.obtainStyledAttributes(
            mContext!!,
            attrs,
            R.styleable.NameImageView,
            0,
            0
        )
        nameStr = tintTypedArray.getString(R.styleable.NameImageView_niv_text) ?: ""
        nameColor = tintTypedArray.getColor(R.styleable.NameImageView_niv_text_color, nameColor)
        borderColor =
            tintTypedArray.getColor(R.styleable.NameImageView_niv_border_color, borderColor)
        nameTextSize =
            tintTypedArray.getDimension(R.styleable.NameImageView_niv_text_size, nameTextSize)
        borderWidth = tintTypedArray.getDimension(
            R.styleable.NameImageView_niv_border_width_size,
            borderWidth
        )
        imageBackgroundColor = tintTypedArray.getColor(
            R.styleable.NameImageView_niv_background_color,
            imageBackgroundColor
        );
        borderVisibility =
            tintTypedArray.getBoolean(R.styleable.NameImageView_niv_border_visibility, true)
        tintTypedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val size = Math.min(height, width)
        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val ableWidth = width - paddingStart - paddingEnd - 2 * borderWidth.toInt()
        val ableHeight = height - paddingBottom - paddingTop - 2 * borderWidth.toInt()
        val usableSize = Math.min(ableWidth, ableHeight)
        val cPx = if (usableSize > ableWidth) (usableSize - ableWidth) / 2 else 0
        val cPy = if (usableSize > ableHeight) (usableSize - ableHeight) / 2 else 0
        val centerX = paddingStart + (ableWidth / 2F) + cPx + borderWidth.toInt()
        val centerY = paddingTop + (ableHeight / 2F) + cPy + borderWidth.toInt()

        var contentStr = nameStr
        if (!TextUtils.isEmpty(nameStr)) {
            if (nameStr.length > nameLength) {
                contentStr = nameStr.substring(0, nameLength)
            }
            measureTextSize((usableSize - 4 * borderWidth).toInt(), contentStr)
        }
        val radius = usableSize / 2F

        // background
        backgroundPaint.color = imageBackgroundColor
        canvas?.drawCircle(centerX, centerY, radius, backgroundPaint)

        // text
        nameTvPaint.apply {
            color = nameColor
            textSize = nameTextSize
        }
        val fontMetricsInt = nameTvPaint.fontMetricsInt
        val centerTextY =
            centerY - (fontMetricsInt.bottom + fontMetricsInt.top) / 2
        canvas?.drawText(contentStr, centerX, centerTextY, nameTvPaint)

        if (borderVisibility) {
            borderPaint.apply {
                color = borderColor
                strokeWidth = borderWidth
            }
            canvas?.drawCircle(
                centerX,
                centerY,
                radius + borderWidth / 2,
                borderPaint
            )
        }

    }

    private fun measureTextSize(usableWidth: Int, str: String) {
        nameTvPaint.getTextBounds(str, 0, str.length, nameRect)
        if (nameRect.width() >= usableWidth) {
            val textSize = nameTvPaint.textSize - dp2px(mContext!!, 2F)
            if (textSize <= 0) {
                return
            }
            nameTvPaint.textSize = textSize
            this.nameTextSize = textSize
            measureTextSize(usableWidth, str)

        }
    }

    /**
     * 将 dp 转换成 px
     *
     * @return px(像素)
     */
    fun dp2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

}