package com.inz.z.note_book.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.inz.z.base.util.L
import com.inz.z.note_book.database.bean.ScreenInfo
import com.inz.z.note_book.database.controller.ScreenTimeController
import com.inz.z.note_book.util.Constants
import java.util.*

/**
 * 屏幕显示广播
 *
 * ====================================================
 * Create by 11654 in 2021/3/28 21:36
 */
class ScreenBroadcast : BroadcastReceiver() {

    companion object {
        private const val TAG = "ScreenBroadcast"
    }

    /**
     * 屏幕显示信息
     */
    private var screenInfo: ScreenInfo? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        L.i(TAG, "onReceive: ---> Action = ${intent?.action}")
        val calendar = Calendar.getInstance(Locale.getDefault())
        // 默认未知状态
        var needSendAction = Constants.NotificationServiceParams.NOTIFICATION_UNKNOWN
        when (intent?.action) {
            Intent.ACTION_SCREEN_ON -> {
                // 屏幕亮
                screenInfo = ScreenTimeController.createScreenInfo(calendar.time)
                // 亮屏状态。
                needSendAction = Constants.NotificationServiceParams.NOTIFICATION_SCREEN_ON_ACTION
            }
            Intent.ACTION_SCREEN_OFF -> {
                // 屏幕熄屏
                screenInfo?.let {
                    it.endTime = calendar.time
                    ScreenTimeController.saveScreenInfo(it)
                }
                screenInfo = null
                // 熄屏状态。
                needSendAction = Constants.NotificationServiceParams.NOTIFICATION_SCREEN_OFF_ACTION
            }
            Intent.ACTION_USER_PRESENT -> {
                // 解锁
                screenInfo?.let {
                    it.unlockTime = calendar.time
                    screenInfo = ScreenTimeController.saveScreenUnlock(it)
                }
                // 解锁状态。
                needSendAction = Constants.NotificationServiceParams.NOTIFICATION_UNLOCK_ACTION
            }
            Intent.ACTION_USER_UNLOCKED -> {
                // 解锁
            }
        }
        L.i(TAG, "onReceive: --->> ScreenInfo = $screenInfo ")
        // 发送状态改变广播。
        context?.sendBroadcast(Intent().setAction(needSendAction))
    }
}