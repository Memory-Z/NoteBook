package com.inz.z.note_book.util

import android.graphics.RectF

/**
 *  View 工具 类
 *
 * ====================================================
 * Create by 11654 in 2022/2/21 21:03
 */
object ViewUtil {

    /**
     * RectF 转 intArray
     */
    fun rectF2Array(rectF: RectF): IntArray {
        return intArrayOf(
            rectF.left.toInt(),
            rectF.top.toInt(),
            rectF.right.toInt(),
            rectF.bottom.toInt()
        )
    }

    /**
     * intArray 转 RectF
     */
    fun array2RectF(array: IntArray): RectF? {
        if (array.size == 4) {
            return RectF(
                array[0].toFloat(),
                array[1].toFloat(),
                array[2].toFloat(),
                array[3].toFloat()
            )
        }
        return null
    }
}