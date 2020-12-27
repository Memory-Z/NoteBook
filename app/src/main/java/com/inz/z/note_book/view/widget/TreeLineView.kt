package com.inz.z.note_book.view.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.TintTypedArray
import androidx.core.content.ContextCompat
import com.inz.z.note_book.R

/**
 * Tree Left Line view
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/09/07 11:50.
 */
class TreeLineView : View {
    companion object {
        private const val TAG = "TreeLineView"

        private const val MAX_LINE_SIZE = 3

    }

    var lineLevel = 0
    var lineHaveChild = false
    var lineHaveParent = false
    var lineHaveNext = false
    var lineTitle = "Title"
    var lineSingleWidth = 48F
    var lineColor: Int = Color.BLACK

    /**
     * show center limlit line view.
     */
    private var showLimitLine = false

    private var mContext: Context? = null
    private lateinit var linePaint: Paint

    constructor(context: Context?) : super(context) {
        this.mContext = context
        mContext?.let {
            initViewValue(it)
        }
        initPaint()
    }

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.mContext = context
        mContext?.let {
            initViewValue(it)
        }
        initViewStyle(attrs)
        initPaint()
    }


    @SuppressLint("RestrictedApi")
    private fun initViewStyle(attrs: AttributeSet?) {
        val tintArray =
            TintTypedArray.obtainStyledAttributes(mContext, attrs, R.styleable.TreeLineView, 0, 0)
        lineColor = tintArray.getColor(
            R.styleable.TreeLineView_tree_line_color,
            ContextCompat.getColor(mContext!!, R.color.colorPrimary)
        )
        lineLevel = tintArray.getInt(R.styleable.TreeLineView_tree_line_level, 1)
        lineSingleWidth =
            tintArray.getDimension(R.styleable.TreeLineView_tree_line_unit_width, lineSingleWidth)
        lineHaveChild = tintArray.getBoolean(R.styleable.TreeLineView_tree_line_have_child, false)
        lineHaveParent = tintArray.getBoolean(R.styleable.TreeLineView_tree_line_have_parent, false)
        lineHaveNext = tintArray.getBoolean(R.styleable.TreeLineView_tree_line_have_next, false)
        if (lineHaveChild) {
            lineHaveNext = true
        }
        tintArray.recycle()
    }

    private fun initViewValue(context: Context) {
        lineColor = ContextCompat.getColor(context, R.color.colorPrimary)
        lineSingleWidth = (context.resources.displayMetrics.density * 48 + .45f).toInt().toFloat()
    }

    private fun initPaint() {
        linePaint = Paint()
        linePaint.apply {
            isAntiAlias = true
            color = lineColor
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            strokeWidth = 4F
            style = Paint.Style.FILL
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        val width = MeasureSpec.getSize(widthMeasureSpec)
//
//        val lineNum = width.div(lineSingleWidth).toInt()
        showLimitLine = lineLevel >= MAX_LINE_SIZE
        val size = if (showLimitLine) 3 else lineLevel
        setMeasuredDimension(
            (lineSingleWidth * size).toInt(),
            MeasureSpec.getSize(heightMeasureSpec)
        )

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        linePaint.color = lineColor
        onDrawLine(canvas)

    }

    /**
     * draw line
     */
    private fun onDrawLine(canvas: Canvas?) {
        if (showLimitLine) {
            onDrawSingleLine(canvas, 1)
            onDrawSingleLine(canvas, 2)
            onDrawSingleLine(canvas, MAX_LINE_SIZE)
        } else {
            for (i in 1..lineLevel) {
                onDrawSingleLine(canvas, i)
            }
        }
    }

    private fun onDrawSingleLine(canvas: Canvas?, position: Int) {
        canvas?.let {

            val startY = 0F
            val centerY = height / 2F
            val endY = height
            val startX = (position - 1) * lineSingleWidth
            val centerX = startX + lineSingleWidth / 2F
            val endX = position * lineSingleWidth
            val lines = mutableListOf<Float>()
            if (position == 1) {
                if (lineHaveParent) {
                    lines.addAll(mutableListOf(centerX, startY, centerX, centerY))
                } else {
                    lines.addAll(mutableListOf(startX, centerY, centerX, centerY))
                }
                if (lineHaveNext) {
                    lines.addAll(mutableListOf(centerX, centerY, centerX, endY.toFloat()))
                }
                lines.addAll(mutableListOf(centerX, centerY, endX, centerY))
            } else if (MAX_LINE_SIZE == position || (position == lineLevel) || lineLevel == MAX_LINE_SIZE) {
                lines.addAll(mutableListOf(startX, centerY, centerX, centerY))
//                if (lineHaveChild) {
//                    lines.addAll(mutableListOf(centerX, centerY, centerX, endY.toFloat()))
//                }
                lines.addAll(mutableListOf(centerX, centerY, endX, centerY))
            } else {
                val lh = centerY / 4F
                val lStartY = centerY - lh
                val lEndY = centerY + lh
                val lw = lineSingleWidth / 4

                lines.addAll(
                    mutableListOf(
                        startX, centerY, startX + lw, lStartY,
                        startX + lw, lStartY, startX + lw, lEndY,
                        startX + lw, lEndY, startX + 3.times(lw), lStartY,

                        startX + 3.times(lw), lStartY, startX + 3.times(lw), lEndY,
                        startX + 3.times(lw), lEndY, endX, centerY
                    )
                )
            }
            it.drawLines(lines.toFloatArray(), linePaint)

        }
    }
}