package com.inz.z.base.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import com.inz.z.base.entity.Constants
import com.inz.z.base.entity.MergeBitmapOrientation

/**
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2019/1/29 10:39.
 */
object ImageUtils {
    /**
     * 通过Bitmap 获取Bitmap
     *
     * @param drawable Drawable
     * @return Bitmap
     */
    @JvmStatic
    fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        }
        return if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else try {
            val bitmap: Bitmap
            val config = Bitmap.Config.ARGB_8888
            bitmap = if (drawable is ColorDrawable) {
                Bitmap.createBitmap(2, 2, config)
            } else {
                Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, config)
            }
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    /**
     * 图片大小
     */
    fun resizeBitmap(bitmap: Bitmap, rate: Float): Bitmap {
        return resizeBitmap(bitmap, rate, true)
    }

    /**
     * 图片大小
     */
    fun resizeBitmap(bitmap: Bitmap, rate: Float, recycleOrigin: Boolean): Bitmap {
        val width = bitmap.width * rate
        val height = bitmap.height * rate
        return resizeBitmap(bitmap, width.toInt(), height.toInt(), recycleOrigin)
    }

    /**
     * 重置图片大小
     * @param bitmap 原图
     * @param width 目标宽
     * @param height 目标高
     * @param recycleOrigin 是否释放原图
     *
     */
    fun resizeBitmap(bitmap: Bitmap, width: Int, height: Int, recycleOrigin: Boolean): Bitmap {
        val newBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)
        if (newBitmap != bitmap && recycleOrigin) {
            bitmap.recycle()
        }
        return newBitmap
    }

    /**
     * 合并图片
     * @param bitmap 第一个
     * @param mergeBitmap 第二个
     * @param orientation 合并方向
     */
    fun mergeBitmap(
        bitmap: Bitmap,
        mergeBitmap: Bitmap,
        @MergeBitmapOrientation orientation: Int
    ): Bitmap {
        return mergeBitmap(bitmap, mergeBitmap, orientation, false, 0, 0)
    }

    /**
     * 合并图片
     * @param bitmap 第一
     * @param mergeBitmap 第二
     * @param orientation 方向
     * @param scaleBitmap 是否缩放
     * @param scaleHeight 缩放高 仅水平方向合并时有效
     * @param scaleWidth 缩放宽 仅竖直方向合并时有效
     */
    fun mergeBitmap(
        bitmap: Bitmap,
        mergeBitmap: Bitmap,
        @MergeBitmapOrientation orientation: Int,
        scaleBitmap: Boolean,
        scaleWidth: Int,
        scaleHeight: Int
    ): Bitmap {
        val firstW = bitmap.width
        val firstH = bitmap.height
        val secondW = mergeBitmap.width
        val secondH = mergeBitmap.height
        var width = 0
        var height = 0
        when (orientation) {
            Constants.BitmapMergeType.HORIZONTAL -> {
                if (scaleBitmap && scaleHeight != 0) {
                    // 需要缩放， 且缩放目标不为0
                    val fScale = scaleHeight * 1F / firstH
                    val newFirstBitmap = resizeBitmap(bitmap, fScale, false)
                    val sScale = scaleHeight * 1F / secondH
                    val newSecondBitmap = resizeBitmap(mergeBitmap, sScale, false)
                    width = newFirstBitmap.width + newSecondBitmap.width
                    height = scaleHeight
                    return mergeBitmap(newFirstBitmap, newSecondBitmap, width, height, true)
                } else {
                    width = firstW + secondW
                    height = Math.max(firstH, secondH)
                    return mergeBitmap(bitmap, mergeBitmap, width, height, true)
                }
            }
            Constants.BitmapMergeType.VERTICAL -> {
                if (scaleBitmap && scaleWidth != 0) {
                    // 需要缩放， 且缩放目标不为0
                    val fScale = scaleWidth * 1F / firstW
                    // 缩放后图片
                    val newFirstBitmap = resizeBitmap(bitmap, fScale, false)
                    val sScale = scaleWidth * 1F / secondW
                    // 缩放后图片
                    val newSecondBitmap = resizeBitmap(mergeBitmap, sScale, false)
                    width = scaleWidth
                    height = newFirstBitmap.height + newSecondBitmap.height
                    return mergeBitmap(newFirstBitmap, newSecondBitmap, width, height, false)
                } else {
                    width = Math.max(firstW, secondW)
                    height = firstH + secondH
                    return mergeBitmap(bitmap, mergeBitmap, width, height, false)
                }
            }
            else -> {
                width = firstW + secondW
                height = Math.max(firstH, secondH)
                return mergeBitmap(bitmap, mergeBitmap, width, height, true)
            }
        }
    }

    /**
     * 合并图片
     * @param bitmap 第一个图
     * @param mergeBitmap 第二个图
     * @param width 目标宽
     * @param height 目标高
     * @param horizontal 是否水平合并
     */
    private fun mergeBitmap(
        bitmap: Bitmap,
        mergeBitmap: Bitmap,
        width: Int,
        height: Int,
        horizontal: Boolean
    ): Bitmap {
        val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(newBitmap)
        canvas.drawColor(Color.TRANSPARENT)
        canvas.drawBitmap(bitmap, 0F, 0F, null)
        if (horizontal) {
            canvas.drawBitmap(mergeBitmap, bitmap.width.toFloat(), 0F, null)
        } else {
            canvas.drawBitmap(mergeBitmap, 0F, bitmap.height.toFloat(), null)
        }
        return newBitmap
    }
}