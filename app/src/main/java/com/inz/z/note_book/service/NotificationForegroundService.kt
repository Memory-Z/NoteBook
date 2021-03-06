package com.inz.z.note_book.service

import android.app.*
import android.content.*
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.inz.z.base.util.L
import com.inz.z.note_book.R
import com.inz.z.note_book.broadcast.ClockAlarmBroadcast
import com.inz.z.note_book.util.ClockAlarmManager
import com.inz.z.note_book.util.Constants
import com.inz.z.note_book.view.activity.MainActivity
import java.util.*

/**
 * 通知 Foreground Service
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2019/11/13 16:40.
 */
class NotificationForegroundService : Service() {

    companion object {
        private const val TAG = "NotificationForegroundService"
        private const val NOTIFICATION_CODE = 0x0009
        private const val NOTIFICATION_CHANNEL_ID = "NoteBook"

        private const val NOTIFICATION_TO_MAIN_REQUEST_CODE = 0x0010

    }

    private var notification: Notification? = null
    private var activityLifeBroadcast: ActivityLifeBroadcast? = null

    private var receiveListener: ClockAlarmBroadcastReceiveListenerImpl? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        L.i(TAG, "onCreate: --------------- ")
        registerActivityLifeBroadcast()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            initNotification()
        }
        receiveListener = ClockAlarmBroadcastReceiveListenerImpl()
        ClockAlarmBroadcast.addListener(receiveListener!!)
        bindScheduleService()
        setLauncherCheckClock()
    }

    override fun onDestroy() {
        super.onDestroy()
        L.i(TAG, "onDestroy: --------------> ")
        stopForeground(true)
        unregisterActivityLifeBroacast()
        unbindScheduleService()
        ClockAlarmBroadcast.removeListener(receiveListener)
        receiveListener = null
    }

    /**
     * 初始化通知栏
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initNotification() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            getString(R.string.app_name),
            NotificationManager.IMPORTANCE_LOW
        )
        val manager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
        notification = NotificationCompat
            .Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(applicationContext.getString(R.string.app_name))
            .setContentText(applicationContext.getString(R.string.base_content))
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .build()
        startForeground(NOTIFICATION_CODE, notification)
    }

    private fun uploadNotification() {
        L.i(TAG, "uploadNotification： ")
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        val notification = getBaseNotification()
        notification.flags = Notification.FLAG_FOREGROUND_SERVICE
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_TO_MAIN_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        notification.contentIntent = pendingIntent
        manager?.notify(NOTIFICATION_CODE, notification)
    }

    private fun hideNotification() {
        L.i(TAG, "hideNotification： ")
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        val notification = getBaseNotification()
        notification.contentIntent = null
        manager?.notify(NOTIFICATION_CODE, notification)
    }

    private fun getBaseNotification(): Notification {
        return NotificationCompat
            .Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(applicationContext.getString(R.string.app_name))
            .setContentText(applicationContext.getString(R.string.base_content))
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .build()
    }

    /**
     * 注册广播
     */
    private fun registerActivityLifeBroadcast() {
        if (activityLifeBroadcast == null) {
            activityLifeBroadcast = ActivityLifeBroadcast()
        }
        val intentFilter = IntentFilter()
        intentFilter.apply {
            addAction(Constants.LifeAction.LIFE_CHANGE_ACTION)
        }
        registerReceiver(activityLifeBroadcast, intentFilter)
    }

    /**
     * 注销广播
     */
    private fun unregisterActivityLifeBroacast() {
        activityLifeBroadcast?.apply {
            unregisterReceiver(this)
        }
        activityLifeBroadcast = null
    }

    /**
     * 检测程序是否显示~
     */
    private fun checkApplicationIsVisible(): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        manager?.apply {
            val appProcess = this.runningAppProcesses
            for (process in appProcess) {
                if (process.processName.equals(packageName)) {
                    if (process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        L.i(TAG, "checkApplicationIsVisible：this is foreground. ")
                        return true
                    } else {
                        L.w(TAG, "checkApplicationIsVisible: this is background. ")
                        return false
                    }
                }
            }
        }
        return false
    }

    /**
     * Activity life broadcast to listener
     */
    private inner class ActivityLifeBroadcast : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context == null || intent == null) {
                L.w(TAG, "ActivityLifeBroadcast-onReceive: context or intent is null. ")
                return
            }
            val bundle = intent.extras
            when (intent.action) {
                Constants.LifeAction.LIFE_CHANGE_ACTION -> {
                    val visible = checkApplicationIsVisible()
                    if (!visible) {
                        uploadNotification()
                        L.i(TAG, "ActivityLifeBroadcast-onReceive: show notification, ")
                    } else {
                        hideNotification()
                        L.i(TAG, "ActivityLifeBroadcast-onReceive: hide notification. ")
                    }
                }
            }
        }
    }

    /**
     * 广播接收监听 实现
     */
    private inner class ClockAlarmBroadcastReceiveListenerImpl :
        ClockAlarmBroadcast.ReceiveListener {
        override fun onDayTwo(t: String) {
            L.i(TAG, "onDayTwo: $t .")
            scheduleService?.startCheckScheduleThread()
        }

    }

    /* -------------------------------- 绑定计划Service  ---------------------------------------- */

    private var scheduleService: ScheduleService? = null
    private var scheduleServiceConnection: ScheduleServiceConnection? = null

    /**
     * 绑定 计划 Service
     */
    private fun bindScheduleService() {
        if (scheduleServiceConnection == null) {
            scheduleServiceConnection = ScheduleServiceConnection()
        }
        val service = Intent(applicationContext, ScheduleService::class.java)
        bindService(service, scheduleServiceConnection!!, Context.BIND_AUTO_CREATE)
    }

    /**
     * 解绑 计划 Service
     */
    private fun unbindScheduleService() {
        if (scheduleServiceConnection != null) {
            unbindService(scheduleServiceConnection!!)
            scheduleServiceConnection = null
        }
    }

    /**
     * 计划 Service 连接
     */
    private inner class ScheduleServiceConnection : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            scheduleService = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val scheduleServiceBinder = service as ScheduleService.ScheduleServiceBinder?
            scheduleServiceBinder?.apply {
                scheduleService = this.getService()
//                scheduleService?.startCheckScheduleThread()
            }
        }
    }

    /**
     * 设置检测线程广播
     */
    private fun setLauncherCheckClock() {
        L.i(TAG, "setLauncherCheckClock: ------ ")
        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        ClockAlarmManager.setAlarm(applicationContext, calendar.timeInMillis, true)
    }
}