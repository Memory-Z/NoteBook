package com.inz.z.note_book.service.create_image

import android.content.Context
import android.graphics.*
import android.text.TextUtils
import com.haibin.calendarview.LunarCalendar
import com.inz.z.base.util.L
import com.inz.z.note_book.R
import com.inz.z.note_book.util.BaseUtil
import java.lang.ref.SoftReference
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import kotlin.math.max


/**
 * 创建每日 图片 线程
 *
 * ====================================================
 * Create by 11654 in 2022/12/4 16:02
 */
class CreateDayImageRunnable(
    context: Context,
    val bitmap: Bitmap,
    private val num: Int,
    private val qrCodeStr: String,
    val listener: CreateDayImageListener
) : Runnable {

    constructor(
        context: Context,
        bitmap: Bitmap,
        num: Int,
        listener: CreateDayImageListener
    ) : this(context, bitmap, num, "", listener)

    companion object {
        private const val TAG = "CreateDayImageRunnable"
        private const val TITLE_X = 400F
        private const val TITLE_Y = 500F
        private const val RIGHT_IMAGE_X = 614F
        private const val RIGHT_IMAGE_Y = 572F
        private const val DATE_X = 100F
        private const val DATE_Y = 1440F
        private const val DATE_WIDTH = 480
        private const val DAY_X = 100F
        private const val DAY_Y = 1360F
        private const val QRCODE_SIZE = 300
    }


    private val contextSoftReadable: SoftReference<Context>
    private val calendar: Calendar
    private val textPaint: Paint
    private val typefaceJetbrainsmonoBold: Typeface
    private val typefaceJetbrainsmonoRegular: Typeface
    private val typefaceFangzhengFangsongRegular: Typeface
    private val typefaceDeYiHeiRegular: Typeface

    init {
        this.contextSoftReadable = SoftReference(context)
        this.calendar = Calendar.getInstance(Locale.getDefault())
        typefaceJetbrainsmonoBold = context.resources.getFont(R.font.jetbrainsmono_bold)

        typefaceJetbrainsmonoRegular = context.resources.getFont(R.font.jetbrainsmono_regular)
        typefaceFangzhengFangsongRegular =
            context.resources.getFont(R.font.fangzheng_fangsong_jianti)
        typefaceDeYiHeiRegular = context.resources.getFont(R.font.smileysans_oblique)
        this.textPaint = Paint()
            .apply {
                val textTypeface = Typeface.createFromAsset(
                    context.assets,
                    "fonts/RobotoSlab-Regular.ttf"
                )
                this.typeface = textTypeface
                this.color = Color.BLACK
                this.textSize = 600F
                this.textAlign = Paint.Align.LEFT
                this.style = Paint.Style.FILL
                this.isAntiAlias = true
            }
    }

    override fun run() {
        if (contextSoftReadable.get() == null) {
            return
        }
        contextSoftReadable.get()?.let {
            val baseBitmap = BitmapFactory.decodeResource(it.resources, R.drawable.day_panel_0)
            createCanvas(it, baseBitmap)
        }


    }

    /**
     * 创建画布。
     */
    private fun createCanvas(context: Context, bitmap: Bitmap) {
        L.i(TAG, "createCanvas: ")
        val newBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        L.d(TAG, "createCanvas: w = ${bitmap.width}, h = ${bitmap.height} ")
        val canvas = Canvas(newBitmap)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        // 绘制右侧图片
        val rightImageBitmap = getRightImage(canvas)
        var matrix = Matrix()
        matrix.setTranslate(RIGHT_IMAGE_X, RIGHT_IMAGE_Y)
        // 绘制图片
//        canvas.drawBitmap(rightImageBitmap, matrix, null)

        // 绘制[早安]
        val titleStr = context.getString(R.string.good_morning)
        val titleAndImageBitmap = getTitleImageBitmap(rightImageBitmap, titleStr)
        matrix = Matrix()
        matrix.setTranslate(TITLE_X, RIGHT_IMAGE_Y)
        canvas.drawBitmap(titleAndImageBitmap, matrix, null)

        // 绘制 当前天数
        drawCurrentDay(canvas)

        // 绘制 时间
        val dateBitmap = getDateBitmap()
        canvas.drawBitmap(dateBitmap, DATE_X, DATE_Y, null)

        // 绘制图片上文字
        drawOverImageText(canvas, context.getString(R.string.lyric_ban_xin_shang_dong_feng))

        drawBelowDateText(canvas, context.getString(R.string.lyric_shi_zi))

        // 绘制二维码
        val qrBitmap = getQrCodeBitmap(qrCodeStr)
        val marginImage = QRCODE_SIZE / 4F
        val qrX = RIGHT_IMAGE_X + rightImageBitmap.width - marginImage - QRCODE_SIZE
        val qrY = RIGHT_IMAGE_Y + rightImageBitmap.height - marginImage - QRCODE_SIZE
        canvas.drawBitmap(qrBitmap, qrX, qrY, null)

        // 绘制装饰
        drawDecorate(canvas)

        listener.onSavedBitmap(newBitmap)

        qrBitmap.recycle()
        dateBitmap.recycle()
        titleAndImageBitmap.recycle()
        rightImageBitmap.recycle()
        newBitmap.recycle()
        L.d(TAG, "createCanvas: ")
    }

    /**
     * 绘制右侧图片
     */
    private fun getRightImage(canvas: Canvas): Bitmap {
        val imgWith = canvas.width * .64F
        val imgHeight = canvas.height * .64F

        val paint = Paint()
        val imgBitmap =
            Bitmap.createBitmap(imgWith.toInt(), imgHeight.toInt(), Bitmap.Config.ARGB_8888)
        val imgCanvas = Canvas(imgBitmap)
        // 设置图片透明度/颜色
        imgCanvas.drawColor(Color.BLACK)
        imgCanvas.drawBitmap(imgBitmap, 0f, 0f, paint)
        val wScale = imgWith / bitmap.width * 1F
        val hScale = imgHeight / bitmap.height * 1F
        val scale = max(wScale, hScale)
        L.d(TAG, "drawRightImage: scale = $scale ==> wScale = $wScale , hScale = $hScale .")
        val matrix = Matrix()
        matrix.setScale(scale, scale)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        imgCanvas.drawBitmap(bitmap, matrix, paint)
        paint.xfermode = null

        return imgBitmap
    }

    /**
     * 绘制标题
     * @param imgBitmap 右侧图片
     */
    private fun getTitleImageBitmap(imgBitmap: Bitmap, titleStr: String): Bitmap {
        textPaint.textSize = 220F
        textPaint.typeface = typefaceFangzhengFangsongRegular

        val difX = abs(TITLE_X - RIGHT_IMAGE_X)
        val difY = abs(TITLE_Y - RIGHT_IMAGE_Y)
        val imageHeight = imgBitmap.height
        val imageWidth = imgBitmap.width + difX
        L.d(
            TAG,
            "getTitleImageBitmap: difX = $difX , difY = $difY , iH = $imageHeight , iW = $imageWidth . "
        )
        val titleImageBitmap = Bitmap.createBitmap(
            imageWidth.toInt(), imageHeight, Bitmap.Config.ARGB_8888
        )
        val titleCanvas = Canvas(titleImageBitmap)
        titleCanvas.drawBitmap(imgBitmap, difX, 0F, null)

        val metrics = textPaint.fontMetrics
        val textHeight = metrics.bottom - metrics.top
        var top = difY + 200F
        textPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.XOR)
        titleStr.split("\n").forEach {
            L.d(TAG, "getTitleImageBitmap: text: $it ")
            titleCanvas.drawText(it, 0F, top, textPaint)
            top += textHeight + 20F
        }
        textPaint.xfermode = null

        val imgBgBitmap = Bitmap.createBitmap(
            titleImageBitmap.width,
            titleImageBitmap.height,
            Bitmap.Config.ARGB_8888
        )
        val imgBgCanvas = Canvas(imgBgBitmap)
        val colorPaint = Paint()
        colorPaint.color = Color.WHITE
        colorPaint.style = Paint.Style.FILL
        val rect = Rect(difX.toInt(), 0, imageWidth.toInt(), imageHeight)
        imgBgCanvas.drawRect(rect, colorPaint)
        imgBgCanvas.drawBitmap(titleImageBitmap, 0F, 0F, null)

        return imgBgBitmap
    }

    /**
     * 绘制当前 时间
     */
    private fun drawCurrentDay(canvas: Canvas) {
        textPaint.textSize = 110F
        textPaint.typeface = typefaceJetbrainsmonoBold

        val currentDay = String.format("DAY %s", num).uppercase()
        canvas.drawText(currentDay, DAY_X, DAY_Y, textPaint)
    }

    /**
     * 绘制日期
     */
    private fun getDateBitmap(): Bitmap {
        textPaint.textSize = 30F
        textPaint.typeface = typefaceJetbrainsmonoRegular

        val calendar = Calendar.getInstance(Locale.getDefault())
        val dateStr = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(calendar.time)
        val weekStr = SimpleDateFormat("E", Locale.getDefault()).format(calendar.time)
        L.d(TAG, "drawDate: date: $dateStr , weekStr = $weekStr")
        val metricsInt = textPaint.fontMetricsInt
        val fontHeight = metricsInt.bottom - metricsInt.top
        val width = DATE_WIDTH
        val height = fontHeight * 3 / 2
        val dateBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val dateCanvas = Canvas(dateBitmap)
        val borderLinePath = Path()
        borderLinePath.moveTo(0F, 0F)
        borderLinePath.lineTo(width.toFloat(), 0F)
        borderLinePath.lineTo(width.toFloat(), height.toFloat())
        borderLinePath.lineTo(0F, height.toFloat())
        val linePaint = Paint()
        linePaint.color = Color.BLACK
        linePaint.style = Paint.Style.STROKE
        linePaint.isAntiAlias = true
        linePaint.strokeCap = Paint.Cap.ROUND
        linePaint.strokeJoin = Paint.Join.ROUND
        linePaint.strokeWidth = 10F
        dateCanvas.drawPath(borderLinePath, linePaint)

        val rect = Rect(0, 0, width / 2, height)
        linePaint.style = Paint.Style.FILL
        dateCanvas.drawRect(rect, linePaint)

        val baseLineY = rect.centerY() - metricsInt.top / 2F - metricsInt.bottom / 2F

        textPaint.textAlign = Paint.Align.CENTER
        textPaint.color = Color.WHITE
        dateCanvas.drawText(dateStr, width / 4F, baseLineY, textPaint)
        textPaint.color = Color.BLACK
        dateCanvas.drawText(weekStr, width / 4F * 3, baseLineY, textPaint)


        textPaint.textAlign = Paint.Align.LEFT
        return dateBitmap
    }

    /**
     * 获取农历
     */
    private fun getLunarDateBitmap() {
        // TODO: 2022/12/5 Add Lunar ..
        val currentCalendar = Calendar.getInstance(Locale.getDefault())

        val hbCalendar = com.haibin.calendarview.Calendar()
            .apply {
                this.year = currentCalendar.get(Calendar.YEAR)
                this.month = currentCalendar.get(Calendar.MONTH) + 1
                this.day = currentCalendar.get(Calendar.DAY_OF_MONTH)
            }
        if (hbCalendar.isAvailable) {
            val lunar = LunarCalendar.getLunarText(hbCalendar)
            L.d(TAG, "getLunarDateBitmap: lunar = $lunar ")
        } else {
            L.d(TAG, "getLunarDateBitmap: 00>>> ")
        }

    }

    /**
     * 绘制图片上文字
     */
    private fun drawOverImageText(canvas: Canvas, content: String) {
        textPaint.textSize = 40F
        textPaint.typeface = typefaceJetbrainsmonoRegular

        textPaint.color = Color.BLACK
        val metricsInt = textPaint.fontMetricsInt
        val textHeight = metricsInt.bottom - metricsInt.top
        var top = RIGHT_IMAGE_Y + 660F
        val left = RIGHT_IMAGE_X + 40F
        content.split("\n").forEach {
            canvas.drawText(it, left, top, textPaint)
            top += textHeight + 10F
        }
    }

    private fun drawBelowDateText(canvas: Canvas, content: String) {
        textPaint.textSize = 50F
        textPaint.typeface = null
        canvas.save()
        canvas.rotate(90F, canvas.width / 2F, canvas.height / 2F)

        textPaint.color = Color.LTGRAY
        val metricsInt = textPaint.fontMetricsInt
        val textHeight = metricsInt.bottom - metricsInt.top
        var top = canvas.width - 120F
        val left = 1240F
        content.split("\n").forEach {
            canvas.drawText(it, left, top, textPaint)
            top += textHeight + 10F
        }

        canvas.restore()
    }

    /**
     * 获取二维码
     */
    private fun getQrCodeBitmap(qrCodeStr: String): Bitmap {
        val qrBitmap = Bitmap.createBitmap(QRCODE_SIZE, QRCODE_SIZE, Bitmap.Config.ARGB_8888)
        val qrCanvas = Canvas(qrBitmap)
        qrCanvas.drawColor(Color.WHITE)
        if (!TextUtils.isEmpty(qrCodeStr)) {
            val bitmap = BaseUtil.createQrCode(qrCodeStr, QRCODE_SIZE)
            qrCanvas.drawBitmap(bitmap, 0f, 0f, null)
        } else {
            qrCanvas.drawColor(Color.BLACK)
        }
        return qrBitmap
    }

    private fun drawDecorate(canvas: Canvas) {
        textPaint.strokeWidth = 20F
        textPaint.color = Color.BLACK
        val height = canvas.height
        val width = canvas.width
        var top = height * 4 / 5F
        var rect = RectF(width / 6F, top, width * 3 / 5F, top + 20F)
        canvas.drawRect(rect, textPaint)

        textPaint.color = Color.parseColor("#6A5C40")
        top -= 140F
        rect = RectF(width * 2 / 5F, top, width * 5 / 7F, top + 10F)
        canvas.drawRect(rect, textPaint)

    }


}