package com.inz.z.note_book.util

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.RectF
import android.os.Build
import android.view.Window
import android.view.WindowInsets
import android.widget.RemoteViews
import com.inz.z.note_book.R

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

    /**
     * 判断当前是否为深色模式 。
     *
     */
    fun getIsNightMode(mContext: Context): Boolean {
        val uiModeManager = mContext.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager?
        var useNightMode = false
        uiModeManager?.let {
            val mode = it.nightMode
            useNightMode = (mode == UiModeManager.MODE_NIGHT_YES)
                .or(mode == UiModeManager.MODE_NIGHT_AUTO)
        }
        val uiMode = mContext.resources.configuration.uiMode
        return ((uiMode.and(Configuration.UI_MODE_TYPE_MASK)) == Configuration.UI_MODE_NIGHT_YES)
            .or(useNightMode)
    }

    fun showSystemUI(window: Window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.decorView.windowInsetsController?.hide(
                WindowInsets.Type.statusBars()
                    .or(WindowInsets.Type.systemBars())
                    .or(WindowInsets.Type.ime())
            )
        }

    }

    /**
     * 创建 LovePanel 通知栏界面
     */
    fun createNotificationLovePanelView(
        context: Context,
        title: String,
        minBitmap: Bitmap?
    ): RemoteViews {
        val remoteView = RemoteViews(context.packageName, R.layout.notification_create_love_panel)
        remoteView.setImageViewBitmap(R.id.notification_create_love_panel_left_iv, minBitmap)
        remoteView.setTextViewText(R.id.notification_create_love_panel_title_tv, title)
        return remoteView
    }
}