package com.inz.z.note_book.service.create_image

import android.content.Context
import android.graphics.*
import com.inz.z.base.util.L

/**
 *
 *
 * ====================================================
 * Create by 11654 in 2022/12/5 23:35
 */
class CreateImageRunnable(
    val context: Context,
    val bitmap: Bitmap,
    val num: Int,
    val listener: CreateDayImageListener
) : Runnable {

    companion object {
        private const val TAG = "CreateImageRunnable"
    }

    private val textPaint: Paint

    init {
        val textTypeface = Typeface.createFromAsset(context.assets, "fonts/RobotoSlab-Regular.ttf")
        textPaint = Paint()
            .apply {
                this.typeface = textTypeface
                this.color = Color.BLACK
                this.textSize = 600F
                this.textAlign = Paint.Align.LEFT
                this.style = Paint.Style.FILL
                this.isAntiAlias = true
            }
    }

    override fun run() {
        L.i(TAG, "run: Start createImage Runnable ")
        createCanvas()
    }


    /**
     * 创建画布
     */
    private fun createCanvas() {

        val newBitmap =
            Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        L.i(TAG, "createCanvas: w = ${bitmap.width}  , h = ${bitmap.height} ")
        val canvas = Canvas(newBitmap)
        canvas.drawBitmap(bitmap, 0F, 0F, null)
        canvas.drawText(num.toString(), 2870F, 3744F, textPaint)

        listener.onSavedBitmap(newBitmap)
    }

}