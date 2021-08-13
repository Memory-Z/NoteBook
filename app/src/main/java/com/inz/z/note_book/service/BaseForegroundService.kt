package com.inz.z.note_book.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.inz.z.base.util.BaseTools
import com.inz.z.base.util.L

/**
 *
 * 标准/基础 前台服务 - 带广播
 * ====================================================
 * Create by 11654 in 2021/7/6 21:50
 */
abstract class BaseForegroundService : Service() {

    companion object {
        private const val TAG = "BaseForegroundService"

        public const val BASE_NOTIFICATION_ID = 0xAA0000
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    /**
     * 获取通道名。
     */
    abstract fun getChannelName(): String

    /**
     * 获取通道ID.
     */
    abstract fun getChannelIdStr(): String

    /**
     * 获取 通知栏ID。
     */
    abstract fun getNotificationId(): Int

    /**
     * 获取 通知栏重要性
     */
    abstract fun getNotificationImportance(): Int

    /**
     * 获取默认 通知内容。
     * @param channelId 通道Id
     */
    abstract fun getInitNotification(channelId: String): Notification?

    private var manager: NotificationManager? = null

    private var mImportance = NotificationManager.IMPORTANCE_LOW
    private var mChannelName = ""
    private var mChannelId = ""
    private var mNotificationId: Int = 0
    private var mNotification: Notification? = null


    override fun onCreate() {
        super.onCreate()
        initData()
        loadBaseData()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 移除通知栏信息。
        stopForeground(true)
        manager = null
    }

    private fun initData() {
        manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
    }

    /**
     * 加载默认数据
     */
    private fun loadBaseData() {
        mChannelName = getChannelName()
        mChannelId = getChannelIdStr()
        mNotificationId = getNotificationId()
        mImportance = getNotificationImportance()
        mNotification = getInitNotification(mChannelId)
        initNotificationChannel()
        setNotification(mNotification)
    }

    /**
     * 初始化通知栏 通道。
     */
    private fun initNotificationChannel() {
        L.d(TAG, "initNotificationChannel: ")

        if (manager == null) {
            L.e(TAG, "initNotificationChannel: ")
            return
        }
        val channel = NotificationChannel(
            getChannelIdStr(),
            getChannelName(),
            mImportance
        )
        manager?.createNotificationChannel(channel)
    }

    /**
     * 设置显示通知。
     */
    fun setNotification(notification: Notification?) {
        if (mNotification != notification && mNotification != null) {
            stopForeground(mNotificationId)
        }
        this.mNotification = notification
        this.mNotification?.let {
            startForeground(mNotificationId, it)
        }

    }

}