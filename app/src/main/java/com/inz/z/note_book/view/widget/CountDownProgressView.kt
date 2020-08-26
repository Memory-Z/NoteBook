package com.inz.z.note_book.view.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.View
import androidx.annotation.IntDef
import androidx.appcompat.widget.TintTypedArray
import com.inz.z.note_book.R
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 倒计时 进度
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/08/26 09:03.
 */
class CountDownProgressView : View {

    private val TAG = "CountDownProgressView"

    companion object {
        const val MODE_COUNT_TIME_SECOND = 0x00EEAA01
        const val MODE_COUNT_TIME_FIXED = 0x00EEAA02

        private const val TIME_BASE_FORMAT = "%d.%02d"
        private const val TIME_MINUTE_FORMAT = "%d:%d.%02d"

        private const val TIME_MODEL_TEXT = "0000:00.000"

        private const val MAX_SHOW_SECOND = 60 * 60


        var progressColor: Int = Color.GREEN
        var progressWidth = 20
        var countdownTextColor: Int = Color.BLACK
        var backgroundColor: Int = Color.LTGRAY
    }


    @IntDef(MODE_COUNT_TIME_SECOND, MODE_COUNT_TIME_FIXED)
    @Retention(AnnotationRetention.SOURCE)
    annotation class CountdownMode

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
     * 开始状态 ： 默认未开始
     */
    private val startState = AtomicBoolean(false)

    private var mContext: Context? = null
    private lateinit var backgroundPaint: Paint
    private lateinit var progressPaint: Paint
    private lateinit var secondProgressPaint: Paint
    private lateinit var countdownTextPaint: Paint

    private var ringRectF: RectF
    private var textRect: Rect

    private var textSize = 0F

    private var progressHandler: Handler

    constructor(context: Context?) : super(context) {
        this.mContext = context
        initPoint()
    }

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.mContext = context
        initStyle(attrs)
        initPoint()
    }

    @SuppressLint("RestrictedApi")
    private fun initStyle(attrs: AttributeSet?) {
        val array = TintTypedArray.obtainStyledAttributes(
            mContext,
            attrs,
            R.styleable.CountDownProgressView,
            0,
            0
        )
        backgroundColor = array.getColor(
            R.styleable.CountDownProgressView_countdown_background_color,
            Color.LTGRAY
        )
        progressColor =
            array.getColor(R.styleable.CountDownProgressView_countdown_progress_color, Color.GREEN)
        progressWidth = array.getDimensionPixelOffset(
            R.styleable.CountDownProgressView_countdown_progress_width,
            20
        )
        countdownTextColor =
            array.getColor(R.styleable.CountDownProgressView_countdown_text_color, Color.BLACK)

        array.recycle()

    }

    init {
        progressHandler = Handler(ProgressHandlerCallback())
        ringRectF = RectF()
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

    /**
     * 校准当前与系统时间差
     */
    private fun calibrationFixDifTime() {
        val currentDateTime = System.currentTimeMillis()
        val dif = fixedTime - currentDateTime
        if (dif > 0) {
            currentTime = dif.toFloat()
        }
    }


    fun initPoint() {

        backgroundPaint = Paint()
        backgroundPaint.apply {
            isAntiAlias = true
            style = Paint.Style.FILL_AND_STROKE
            color = backgroundColor
        }
        progressPaint = Paint()
        progressPaint.apply {
            isAntiAlias = true
            strokeCap = Paint.Cap.ROUND
            color = progressColor
            style = Paint.Style.STROKE
        }

        secondProgressPaint = Paint()
        secondProgressPaint.apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            color = Color.LTGRAY
            strokeCap = Paint.Cap.ROUND
        }

        countdownTextPaint = Paint()
        countdownTextPaint.apply {
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            textSize = 56F
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        progressHandler.removeCallbacksAndMessages(null)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val size = Math.min(width, height)
        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        onDrawProgress(canvas)
    }

    private fun onDrawProgress(canvas: Canvas?) {
        canvas?.let {
            val widthUsable = width - paddingStart - paddingEnd
            val heightUsable = height - paddingTop - paddingBottom
            val centerX = paddingStart + widthUsable / 2F
            val centerY = paddingTop + heightUsable / 2F
            val size = Math.min(widthUsable / 2F, heightUsable / 2F)
            ringRectF.apply {
                left = paddingStart + progressWidth / 2F
                top = paddingTop + progressWidth / 2F
                right = paddingStart + size * 2 - progressWidth / 2F
                bottom = paddingTop + size * 2 - progressWidth / 2F
            }
            backgroundPaint.strokeWidth = progressWidth + 8F
            backgroundPaint.color = backgroundColor
            it.drawCircle(centerX, centerY, size - progressWidth / 2F, backgroundPaint)

            if (countdownTime > 0) {
                val sweepAngle = currentTime * 360F / countdownTime
                if (sweepAngle > 0) {
                    progressPaint.strokeWidth = progressWidth.toFloat()
                    progressPaint.color = progressColor
                    it.drawArc(ringRectF, -90F, sweepAngle, false, progressPaint)
                }

            }
            onDrawCenterText(it, centerX, centerY, size)
            if (currentTime <= 0F) {
                listener?.countFinish()
                return
            }
            progressHandler.sendEmptyMessageDelayed(HANDLER_COUNT_DOWN_START, 10)
        }
    }

    private fun onDrawCenterText(canvas: Canvas, centerX: Float, centerY: Float, size: Float) {
        val timeStr = getTimeFormatStr()
        if (textSize == 0F) {
            resetTextSize(size, size)
        }
        countdownTextPaint.color = countdownTextColor
        val metricsInt = countdownTextPaint.fontMetricsInt
        val cY = centerY - (metricsInt.top + metricsInt.bottom) / 2
//        canvas.drawLine(0F, cY, size + centerX, cY, progressPaint)
//        canvas.drawLine(0F, centerY, size + centerX, centerY, progressPaint)
//        canvas.drawLine(centerX, 0F, centerX, centerY + size, progressPaint)
        canvas.drawText(timeStr, centerX, cY, countdownTextPaint)
    }

    /**
     * 设置文字大小
     */
    private fun resetTextSize(textSize: Float, size: Float) {
        countdownTextPaint.textSize = textSize
        countdownTextPaint.getTextBounds(TIME_MODEL_TEXT, 0, TIME_MODEL_TEXT.length, textRect)
        if (textRect.width() > size) {
            resetTextSize(textSize - 8F, size)
        } else {
            this.textSize = textSize
        }
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
            val mins = secondTime / 60
            val seconds = secondTime % 60
            return TIME_MINUTE_FORMAT.format(mins, seconds, millTime)
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

    ///////////////////////////////////////////////////////////////////////////
    // OPEN
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 倒计时监听
     */
    interface CountdownViewListener {
        /**
         * 倒计时
         * @param currentTime 剩余时间
         */
        fun countDown(currentTime: Float)

        /**
         * 倒计时结束
         */
        fun countFinish()
    }

    var listener: CountdownViewListener? = null

    /**
     * 开始倒计时
     */
    fun start(@CountdownMode countdownMode: Int, time: Long) {
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
                calibrationFixDifTime()
            }
        }
        startState.set(true)
        invalidate()
    }

    /**
     * 开始
     */
    fun start(){
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