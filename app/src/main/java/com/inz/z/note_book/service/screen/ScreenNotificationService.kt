package com.inz.z.note_book.service.screen

import android.app.Notification
import android.app.NotificationManager
import com.inz.z.note_book.service.BaseForegroundService

/**
 *
 *
 * ====================================================
 * Create by 11654 in 2021/7/6 22:23
 */
class ScreenNotificationService : BaseForegroundService() {
    companion object {
        private const val TAG = "ScreenNotificationServi"
        private const val CHANNEL_NAME = "SCREEN_SERVICE_NOTIFICATION"
        private const val CHANNEL_ID = "ScreenService"
        private const val NOTIFICATION_ID = BASE_NOTIFICATION_ID + 1

    }

    override fun getChannelName(): String {
        return CHANNEL_NAME
    }

    override fun getChannelIdStr(): String {
        return CHANNEL_ID
    }

    override fun getNotificationId(): Int {
        return NOTIFICATION_ID
    }

    override fun getNotificationImportance(): Int {
        return NotificationManager.IMPORTANCE_LOW
    }

    override fun getInitNotification(channelId: String): Notification? {
        return null
    }
}