package com.inz.z.note_book.view.widget.layout

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.DrawableUtils
import androidx.core.graphics.BitmapCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.values
import com.inz.z.base.util.L
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * 滑动 ImageView
 *
 * ====================================================
 * Create by 11654 in 2021/11/1 20:35
 */
class ScrollImageView : AppCompatImageView {

    companion object {
        private const val TAG = "ScrollImageView"
    }

    private var mView: View? = null
    private var mContext: Context? = null
    private var gestureDetector: GestureDetector? = null

    private var viewWidth: Int = 0
    private var viewHeight: Int = 0

    private var currentMatrix: Matrix? = null
    private var imgHeight: Int = 0
    private var imgWidth: Int = 0
    private var saveMatrix: Matrix? = null


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

    private fun initViewStyle(attrs: AttributeSet?) {

    }

    private fun initView() {
        L.i(TAG, "initView: ")

//        this.scaleType = ScaleType.CENTER_CROP
        gestureDetector = GestureDetector(mContext, GestureListenerImpl())
        currentMatrix = Matrix()
        saveMatrix = Matrix()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        L.i(TAG, "onMeasure: ")
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        viewHeight = MeasureSpec.getSize(heightMeasureSpec)
        if (imgWidth != 0 && 0 != imgHeight) {
            initBitmap()
        }
    }

    private fun initBitmap() {
        L.i(TAG, "initBitmap: ")
        if (currentMatrix == null) {
            currentMatrix = Matrix()
        }
        if (viewWidth == 0 || viewHeight == 0) {
            L.w(TAG, "initBitmap:  this width or height is 0. ")
            return
        }
        val xScale = viewWidth / imgWidth.toFloat()
        val yScale = viewHeight / imgHeight.toFloat()
        val defaultScale = max(xScale, yScale)
        L.i(TAG, "initBitmap: ---> $xScale - $yScale - $defaultScale")
        val tranX = (viewWidth - imgWidth * defaultScale) / 2F
        val tranY = (viewHeight - imgHeight * defaultScale) / 2F
        L.i(TAG, "initBitmap: -->> translate $tranX , $tranY")
        currentMatrix?.let {
            it.reset()
            it.postScale(defaultScale, defaultScale)
            it.postTranslate(tranX, tranY)
            scaleType = ScaleType.MATRIX
            imageMatrix = it
        }
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        drawable?.let {
            imgWidth = it.intrinsicWidth
            imgHeight = it.intrinsicHeight
            L.i(TAG, "setImageDrawable: imgWidth = $imgWidth, imgHeight = $imgHeight")
            if (imgWidth != 0 && 0 != imgHeight) {
                initBitmap()
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let { event1 ->
            val touchX = event1.x
            val touchY = event1.y
            when (event1.action) {
                MotionEvent.ACTION_DOWN -> {
                    onDownX = touchX
                    onDownY = touchY
                    saveMatrix?.set(currentMatrix)
                }
                MotionEvent.ACTION_MOVE -> {
                    val distanceX = touchX - onDownX
                    val distanceY = touchY - onDownY
                    val dx = checkXBorder(distanceX)
                    val dy = checkYBorder(distanceY)
                    L.i(TAG, "onTouchEvent: ---> dx = $dx , dy = $dy")
                    saveMatrix?.let {
                        it.set(currentMatrix)
                        it.postTranslate(dx, dy)
                        this.imageMatrix = it
                    }
                }
                MotionEvent.ACTION_UP -> {
                    onDownX = 0F
                    onDownY = 0F
                    currentMatrix?.set(saveMatrix)
                }
                else -> {

                }
            }
        }
        return true
    }


    private fun checkXBorder(dx: Float): Float {
        val values = FloatArray(9)
        var x = dx;
        currentMatrix?.getValues(values)
        if (imgWidth * values[Matrix.MSCALE_X] < viewWidth) {
            x = 0F
        } else if (values[Matrix.MTRANS_X] + dx > 0) {
            // 如果 右移 ，图片最左侧 大于 0 时，默认按 图片 最左位置处理
            x = -values[Matrix.MTRANS_X]
        } else if (imgWidth * values[Matrix.MSCALE_X] + values[Matrix.MTRANS_X] + dx < viewWidth) {
            // 如果 左移， 图片 最右侧 小于 视图边缘时 ， 图片 位置 按 最右处理
            x = viewWidth - imgWidth * values[Matrix.MSCALE_X] - values[Matrix.MTRANS_X]
        }
        return x
    }

    private fun checkYBorder(dy: Float): Float {
        val values = FloatArray(9)
        var y = dy
        currentMatrix?.getValues(values)
        L.i(TAG, "checkYBorder: --->> values = ${values.contentToString()}")
        if (imgHeight * values[Matrix.MSCALE_Y] < viewHeight) {
            y = 0F
        } else if (values[Matrix.MTRANS_Y] + dy > 0) {
            y = -values[Matrix.MTRANS_Y]
        } else if (imgHeight * values[Matrix.MSCALE_Y] + values[Matrix.MTRANS_Y] + dy < viewHeight) {
            y = viewHeight - values[Matrix.MTRANS_Y] - imgHeight * values[Matrix.MSCALE_Y]
        }
        return y
    }

    private var onDownX: Float = 0F
    private var onDownY: Float = 0F

    /**
     * 触控 监听。
     */
    private inner class GestureListenerImpl : GestureDetector.OnGestureListener {

        override fun onDown(e: MotionEvent?): Boolean {
            e?.let {
                onDownX = it.x
                onDownY = it.y
            }
            L.i(TAG, "onDown: ---downX = $onDownX , downY = $onDownY")
            return true
        }

        override fun onShowPress(e: MotionEvent?) {

        }

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            return false
        }


        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {

            L.i(TAG,
                "onScroll: ---->> e1: {x = ${e1?.x}, y = ${e1?.y}} e2 = {x = ${e2?.x}, y = ${e2?.y}}, dx = $distanceX , dy = $distanceY")
            if (e2 != null && e1 != null) {
                val difX = e2.x - e1.x
                val difY = e2.y - e1.y
                L.i(TAG, "onScroll: --->>>>>>>>> difX = $difX , difY = $difY")
//                val dx = checkXBorder(difX)
//                val dy = checkYBorder(difY)
////                L.i(TAG, "onScroll: dx = $dx , dy = $dy")
//                currentMatrix?.let {
//                    it.postTranslate(difX, difY)
//                    imageMatrix = it
//                }
            }

            return true
        }

        override fun onLongPress(e: MotionEvent?) {

        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            return false
        }
    }


    fun getWallpaperRect(): RectF {
        val rectF = RectF()
        val values = FloatArray(9)
        currentMatrix?.getValues(values)
        L.i(TAG, "getWallpaperRect: --->>> values = $values")
        val left = -values[Matrix.MTRANS_X] / values[Matrix.MSCALE_X]
        val top = -values[Matrix.MTRANS_Y] / values[Matrix.MSCALE_Y]
        val right = left + viewWidth  / values[Matrix.MSCALE_X]
        val bottom = top + viewHeight / values[Matrix.MSCALE_Y]
        rectF.set(left, top, right, bottom)
        L.i(TAG, "getWallpaperRect: left - $left, top - $top, right - $right, bottom - $bottom ")
        return rectF
    }

    fun getWallpaperBitmap(): Bitmap {
        val drawable = drawable
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.BLACK)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return bitmap
    }

}