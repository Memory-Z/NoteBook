package com.inz.z.note_book.util

import android.app.PendingIntent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import androidx.core.graphics.set
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.multi.qrcode.QRCodeMultiReader
import com.google.zxing.pdf417.decoder.ec.ErrorCorrection
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.google.zxing.qrcode.encoder.QRCode
import com.inz.z.base.util.BaseTools
import java.util.Hashtable

/**
 * 基础工具类
 *
 * ====================================================
 * Create by 11654 in 2022/5/2 15:01
 */
object BaseUtil : BaseTools() {


    // +bug, 11654, 2022/5/2 , modify , update pendingIntent FLAG .
    /**
     * 获取 PendingIntent FLAG
     */
    fun getPendingIntentFlag(): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE.or(
            PendingIntent.FLAG_UPDATE_CURRENT
        ) else PendingIntent.FLAG_UPDATE_CURRENT
    // -bug, 11654, 2022/5/2 , modify , update pendingIntent FLAG .

    fun getMutablePendingIntentFlag(): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE.or(
            PendingIntent.FLAG_CANCEL_CURRENT
        ) else PendingIntent.FLAG_CANCEL_CURRENT

    fun createQrCode(content: String, size: Int): Bitmap {
        val hints: Hashtable<EncodeHintType, Any> = Hashtable()
        hints[EncodeHintType.CHARACTER_SET] = "utf-8"
        hints[EncodeHintType.MARGIN] = 1
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H

        val multiFormatWriter = MultiFormatWriter()
        val bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, size, size, hints)


        val pixels = IntArray(size * size)
        for (x in 0 until size step 1) {
            for (y in 0 until size step 1) {
                if (bitMatrix.get(x, y)) {
                    pixels[x * size + y] = Color.BLACK
                }
            }
        }

        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

        bitmap.setPixels(pixels, 0, size, 0, 0, size, size)
        return bitmap
    }
}