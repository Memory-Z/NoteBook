package com.inz.z.note_book.util

import androidx.annotation.UiThread
import java.util.concurrent.atomic.AtomicLong

/**
 *
 * 点击工具类
 * ====================================================
 * Create by 11654 in 2021/4/10 21:21
 */
object ClickUtil {

    private val firstClickTime = AtomicLong()

    /**
     * 快速点击间隔： .5s
     */
    private const val FAST_CLICK_TIME = 500L

    @UiThread
    fun isFastClick(): Boolean {
        return isFastClick(FAST_CLICK_TIME)
    }

    @UiThread
    fun isFastClick(interval: Long): Boolean {
        val currentTime = System.currentTimeMillis()
        // 如果两次点击小于 interval 表明快速点击
        val isFast = currentTime - firstClickTime.get() < interval
        firstClickTime.set(currentTime)
        return isFast
    }
}