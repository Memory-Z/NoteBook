package com.inz.z.note_book.view.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.View
import androidx.annotation.IntDef
import androidx.appcompat.widget.TintTypedArray
import com.inz.z.note_book.R
import java.util.concurrent.atomic.AtomicBoolean

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/09/22 13:25.
 */
class CountDownTextView : View {
    companion object {
        private const val TAG = "CountDownTextView"
        const val MODE_COUNT_TIME_SECOND = 0x00EEAA01
        const val MODE_COUNT_TIME_FIXED = 0x00EEAA02

        private const val TIME_BASE_FORMAT = "%d.%02d"
        private const val TIME_MINUTE_FORMAT = "%d:%02d.%02d"
        private const val TIME_HOUR_FORMAT = "%d:%02d:%02d.%02d"

        private const val TIME_MODEL_TEXT = "0000:00:00.00"

        private const val MAX_SHOW_SECOND = 60

        private const val BASE_TEXT_SIZE = 16

        var countdownTextColor: Int = Color.BLACK
        var backgroundColor: Int = Color.LTGRAY
    }

    @IntDef(MODE_COUNT_TIME_SECOND, MODE_COUNT_TIME_FIXED)
    @Retention(AnnotationRetention.SOURCE)
    annotation class CountdownMode {}

    /**
     * 倒计时模式
     */
    @CountdownMode
    private var countdownMode: Int = MODE_COUNT_TIME_SECOND

    private var countdownTime = 60F
    private var currentTime = 0F

    /**
     * 固定时间
     */
    private var fixedTime = 0L

    /**
     * 固定开始时间
     */
    private var fixedStartTime = 100L

    /**
     * 开始状态 ： 默认未开始
     */
    private val startState = AtomicBoolean(false)

    private var mContext: Context? = null
    private lateinit var backgroundPaint: Paint
    private lateinit var countdownTextPaint: Paint

    private var textRect: Rect

    private var textSize = 0F

    private var progressHandler: Handler

    init {
        progressHandler = Handler(ProgressHandlerCallback())
        textRect = Rect()
        when (countdownMode) {
            MODE_COUNT_TIME_SECOND -> {
                if (countdownTime > 0) {
                    currentTime = countdownTime
                }
            }
            MODE_COUNT_TIME_FIXED -> {
                calibrationFixDifTime()
            }
        }
    }

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
        initViewStyle(attrs)
        initPaint()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        progressHandler.removeCallbacksAndMessages(null)
    }

    private fun initPaint() {
        if (textSize == 0F) {
            val fontScale = mContext?.resources?.displayMetrics?.scaledDensity ?: 1F
            textSize = (BASE_TEXT_SIZE * fontScale + .5F).toInt().toFloat()
        }
        countdownTextPaint = Paint()
        countdownTextPaint.apply {
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            textSize = this@CountDownTextView.textSize
        }
        backgroundPaint = Paint()
        backgroundPaint.apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            strokeCap = Paint.Cap.SQUARE
            strokeJoin = Paint.Join.ROUND


        }
    }

    @SuppressLint("RestrictedApi")
    private fun initViewStyle(attrs: AttributeSet?) {
        val type = TintTypedArray.obtainStyledAttributes(
            mContext,
            attrs,
            R.styleable.CountDownTextView,
            0,
            0
        )
        textSize = type.getDimension(R.styleable.CountDownTextView_cdt_text_size, 0F)
        countdownTextColor =
            type.getColor(R.styleable.CountDownTextView_cdt_color, countdownTextColor)
        backgroundColor =
            type.getColor(
                R.styleable.CountDownTextView_cdt_background_color,
                Color.TRANSPARENT
            )

        type.recycle()
    }

    /**
     * 校准当前与系统时间差
     */
    private fun calibrationFixDifTime() {
        val currentDateTime = System.currentTimeMillis()
        val dif = (fixedTime - currentDateTime) / 1000F
        if (dif > 0) {
            currentTime = dif
        }
    }

    private val HANDLER_COUNT_DOWN_START = 0x0EFA0001

    inner class ProgressHandlerCallback : Handler.Callback {
        override fun handleMessage(msg: Message): Boolean {
            when (msg.what) {
                HANDLER_COUNT_DOWN_START -> {
                    if (startState.get()) {
                        when (countdownMode) {
                            MODE_COUNT_TIME_SECOND -> {
                                currentTime -= 1 / 100F
                            }
                            MODE_COUNT_TIME_FIXED -> {
                                calibrationFixDifTime()
                            }
                        }
                        listener?.countDown(currentTime)
                        invalidate()
                    }
                }
            }
            return true
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val fontMetricsInt = countdownTextPaint.fontMetricsInt
        val textHeight = fontMetricsInt.bottom - fontMetricsInt.top
        val textHeightTwo = textHeight * 2

        if (textHeightTwo < height) {
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), textHeightTwo)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas != null) {
            canvas.drawColor(backgroundColor)
            val widthUsable = width - paddingStart - paddingEnd
            val heightUsable = height - paddingTop - paddingBottom
            val centerX = paddingStart + widthUsable / 2F
            val centerY = paddingTop + heightUsable / 2F
            onDrawCenterText(canvas, centerX, centerY)
            if (currentTime <= 0F) {
                listener?.countFinish()
                return
            }
            progressHandler.sendEmptyMessageDelayed(HANDLER_COUNT_DOWN_START, 10)
        }
    }

    private fun onDrawCenterText(canvas: Canvas, centerX: Float, centerY: Float) {
        val timeStr = getTimeFormatStr()
        if (textSize == 0F) {
            val fontScale = mContext?.resources?.displayMetrics?.scaledDensity ?: 1F
            textSize = (BASE_TEXT_SIZE * fontScale + .5F).toInt().toFloat()
        }
        countdownTextPaint.textSize = textSize
        countdownTextPaint.color = countdownTextColor
        val metricsInt = countdownTextPaint.fontMetricsInt
        val cY = centerY - (metricsInt.top + metricsInt.bottom) / 2
        canvas.drawText(timeStr, centerX, cY, countdownTextPaint)
    }

    /**
     * 获取格式化 后的时间
     */
    private fun getTimeFormatStr(): String {
        val secondTime = (currentTime).toInt()
        var millTime = ((currentTime - secondTime) % 1 * 100).toInt()
        if (millTime < 0) {
            millTime = 0
        }
        if (secondTime < MAX_SHOW_SECOND) {
            return TIME_BASE_FORMAT.format(secondTime, millTime)
        } else {
            var mins = secondTime / 60
            val seconds = secondTime - mins * 60
            if (mins <= MAX_SHOW_SECOND) {
                return TIME_MINUTE_FORMAT.format(mins, seconds, millTime)
            } else {
                val hour = mins / 60
                mins = mins - hour * 60
                return TIME_HOUR_FORMAT.format(hour, mins, seconds, millTime)
            }
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    // OPEN
    ///////////////////////////////////////////////////////////////////////////

    interface CountDownTextViewListener {
        /**
         * count down current time.
         */
        fun countDown(currentTime: Float)

        /**
         * 倒计时结束
         */
        fun countFinish()
    }

    var listener: CountDownTextViewListener? = null


    /**
     * 开始倒计时
     */
    fun start(@CountdownRingView.CountdownMode countdownMode: Int, time: Long) {
        if (time <= 0) {
            throw IllegalArgumentException("time con't lt or equals 0. ")
        }

        this.countdownMode = countdownMode
        when (countdownMode) {
            MODE_COUNT_TIME_SECOND -> {
                countdownTime = time.toFloat()
                if (countdownTime > 0) {
                    currentTime = countdownTime
                }
            }
            MODE_COUNT_TIME_FIXED -> {
                fixedTime = time
                fixedStartTime = (time - System.currentTimeMillis()) / 1000L
                calibrationFixDifTime()
            }
        }
        startState.set(true)
        invalidate()
    }

    /**
     * 开始
     */
    fun start() {
        startState.set(true)
        invalidate()
    }

    /**
     * 停止
     */
    fun stop() {
        startState.set(false)
    }
}