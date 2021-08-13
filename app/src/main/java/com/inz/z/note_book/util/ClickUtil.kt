package com.inz.z.note_book.util

import android.view.View
import androidx.annotation.UiThread
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.abs

/**
 *
 * 点击工具类
 * ====================================================
 * Create by 11654 in 2021/4/10 21:21
 */
object ClickUtil {
    /**
     * 快速点击间隔： .5s
     */
    private const val FAST_CLICK_TIME = 500L

    private const val MAX_MAP_SIZE = 32

    private val firstClickTime = AtomicLong()

    /**
     * 点击内容
     */
    private val clickViewMap = LinkedHashMap<Int, Long>(16)

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

    @UiThread
    fun isFastClick(view: View?): Boolean {
        return isFastClick(view, FAST_CLICK_TIME)
    }

    @UiThread
    fun isFastClick(view: View?, interval: Long): Boolean {
        if (view == null) {
            return false
        }
        val currentTime = System.currentTimeMillis()
        val vId = view.id
        // 默认为：false , 非快速点击，
        var isFast = false
        if (clickViewMap.containsKey(vId)) {
            val lastTime = clickViewMap[vId] ?: 0L
            // 判断两次点击 事件间隔。
            isFast = abs(currentTime - lastTime) > interval
            // 存在点击 ID , 判断 上次执行时间，
            clickViewMap.remove(vId)
        }
        if (clickViewMap.size > MAX_MAP_SIZE) {
            // 获取第一个数据并移除，第一个数据时间最长久，
            val firstKey = clickViewMap.keys.iterator().next()
            clickViewMap.remove(firstKey)
        }
        // 更新 最新时间
        clickViewMap[vId] = currentTime
        return isFast
    }
}