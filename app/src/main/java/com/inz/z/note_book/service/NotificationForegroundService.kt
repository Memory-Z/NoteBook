package com.inz.z.note_book.service

import android.app.*
import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.inz.z.base.util.BaseTools
import com.inz.z.base.util.L
import com.inz.z.note_book.R
import com.inz.z.note_book.broadcast.ClockAlarmBroadcast
import com.inz.z.note_book.database.bean.ScreenInfo
import com.inz.z.note_book.database.controller.ScreenTimeController
import com.inz.z.note_book.util.BaseUtil
import com.inz.z.note_book.util.ClockAlarmManager
import com.inz.z.note_book.util.Constants
import com.inz.z.note_book.util.ViewUtil
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
        private const val NOTIFICATION_TO_WECHAT_REQUEST_CODE = 0x0011

        private const val NOTIFICATION_LOVE_PANEL_CHANNEL_ID = "LovePanel"
        private const val NOTIFICATION_LOVE_PANEL_CHANNEL_NAME = "Love Panel"
        private const val NOTIFICATION_LOVE_PANEL_CODE = 0x000A

    }

    private var notification: Notification? = null
    private var activityLifeBroadcast: ActivityLifeBroadcast? = null
    private var notificationManager: NotificationManager? = null

    /**
     * 默认 通道
     */
    private var baseChannel: NotificationChannel? = null

    /**
     * LovePanel 通道
     */
    private var lovePanelChannel: NotificationChannel? = null

    /**
     * 时钟广播监听。
     */
    private var receiveListener: ClockAlarmBroadcastReceiveListenerImpl? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        L.i(TAG, "onCreate: --------------- ")
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        registerActivityLifeBroadcast()
        registerNotificationBroadcast()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            initNotificationChannel()
        }
        initForegroundNotification()
        receiveListener = ClockAlarmBroadcastReceiveListenerImpl()
        ClockAlarmBroadcast.addListener(receiveListener!!)
        bindScheduleService()
        setLauncherCheckClock()
    }

    override fun onDestroy() {
        super.onDestroy()
        L.i(TAG, "onDestroy: --------------> ")
        stopForeground(true)
        unregisterActivityLifeBroadcast()
        unbindScheduleService()
        ClockAlarmBroadcast.removeListener(receiveListener)
        receiveListener = null
        baseChannel = null
        lovePanelChannel = null
        notificationManager = null
    }

    /**
     * 初始化通知栏
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initNotificationChannel() {
        // 默认通道
        baseChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            getString(R.string.app_name),
            NotificationManager.IMPORTANCE_LOW
        )
        baseChannel?.let {
            notificationManager?.createNotificationChannel(it)
        }

        // 创建 LovePanel 通道
        lovePanelChannel = NotificationChannel(
            NOTIFICATION_LOVE_PANEL_CHANNEL_ID,
            NOTIFICATION_LOVE_PANEL_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        lovePanelChannel?.let {
            notificationManager?.createNotificationChannel(it)
        }
    }

    /**
     * 初始化前台服务通知
     */
    private fun initForegroundNotification() {
        val time =
            BaseTools.getDateFormatTime().format(Calendar.getInstance(Locale.getDefault()).time)
        notification = NotificationCompat
            .Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(applicationContext.getString(R.string.app_name))
            .setContentText(applicationContext.getString(R.string.base_content) + " $time")
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .build()
            .apply {
                // 设置默认值
                this.contentIntent = getNotificationDefPendingIntent()
            }
        startForeground(NOTIFICATION_CODE, notification)
    }

    /**
     * 是否为屏幕显示
     */
    private fun uploadNotification(isScreen: Boolean) {
        L.i(TAG, "uploadNotification： ")
        val notification = if (isScreen) getScreenNotification() else getBaseNotification()
        notification.flags = Notification.FLAG_FOREGROUND_SERVICE
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        val flag = BaseUtil.getPendingIntentFlag()
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_TO_MAIN_REQUEST_CODE,
            intent,
            flag
        )
        notification.contentIntent = pendingIntent
        notificationManager?.notify(NOTIFICATION_CODE, notification)
    }

    private fun hideNotification() {
        L.i(TAG, "hideNotification： ")
        val notification = getBaseNotification()
        notification.contentIntent = null
        notificationManager?.notify(NOTIFICATION_CODE, notification)
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
     * 获取 屏幕信息 Notification
     */
    private fun getScreenNotification(): Notification {
        return NotificationCompat
            .Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setContent(getScreenMinView())
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setCustomBigContentView(getScreenView())
            .setOngoing(true)
            .build()
    }

    /**
     * 获取 通知默认 PendingIntent
     */
    private fun getNotificationDefPendingIntent(): PendingIntent {
        val intent = Intent(applicationContext, MainActivity::class.java)
            .apply {
                this.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    .or(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    .or(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP)
                    .or(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
        // +bug, 11654, 2022/5/2 , modify , update pendingIntent FLAG .
        val flag = BaseUtil.getPendingIntentFlag()
        // -bug, 11654, 2022/5/2 , modify , update pendingIntent FLAG .
        return PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_TO_MAIN_REQUEST_CODE,
            intent,
            flag
        )
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
    private fun unregisterActivityLifeBroadcast() {
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
                    return if (process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        L.i(TAG, "checkApplicationIsVisible：this is foreground. ")
                        true
                    } else {
                        L.w(TAG, "checkApplicationIsVisible: this is background. ")
                        false
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
            when (intent.action) {
                Constants.LifeAction.LIFE_CHANGE_ACTION -> {
                    // 判断进程是否显示
                    val visible = checkApplicationIsVisible()
                    if (!visible) {
                        uploadNotification(false)
                        L.i(TAG, "ActivityLifeBroadcast-onReceive: show notification, ")
                    } else {
                        hideNotification()
//                        uploadNotification(true)
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

    /* ------------------------- Get Notification Screen View -------------------------- */

    /**
     * 获取使用时间小显示
     */
    private fun getScreenMinView(): RemoteViews {
        val view = RemoteViews(packageName, R.layout.notification_screen_min_layout)
        val screenInfo = getScreenInfoInLast()
        screenInfo?.let {
            val duration = getScreenInfoDuration(it)
            val durationStr = BaseTools.getTimeDurationStr(duration)
            view.setTextViewText(R.id.notification_screen_duration_tv, durationStr)
        }
        return view
    }

    /**
     * 获取显示提示
     */
    private fun getScreenView(): RemoteViews {
        val view = RemoteViews(packageName, R.layout.notification_screen_layout)
        val screenInfo = getScreenInfoInLast()
        screenInfo?.let {
            val duration = getScreenInfoDuration(it)
            val durationStr = BaseTools.getTimeDurationStr(duration)
            view.setTextViewText(R.id.notification_screen_duration_tv, durationStr)
            var timeStr = ""
            if (it.startTime != null) {
                timeStr = BaseTools.getBaseDateFormat().format(it.startTime!!)
            }
            view.setTextViewText(R.id.notification_screen_time_tv, timeStr)
        }
        return view
    }

    /* ------------------------- Get Notification Screen View -------------------------- */

    /* ***************************** LovePanel ********************************* */

    /**
     * 更新 LovePanel 通知
     * @param title 标题
     * @param bitmap 图片
     */
    private fun updateLovePanelNotification(title: String, bitmap: Bitmap) {
        L.d(TAG, "updateLovePanelNotification: title = $title ")
        val remoteViews = ViewUtil.createNotificationLovePanelView(baseContext, title, bitmap)
        val pendingIntent = getWeChatPendingIntent()
        val notification = NotificationCompat
            .Builder(baseContext, NOTIFICATION_LOVE_PANEL_CHANNEL_ID)
            .setCustomContentView(remoteViews)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setDefaults(NotificationCompat.DEFAULT_LIGHTS)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setFullScreenIntent(pendingIntent, true)
            .setContentIntent(pendingIntent)
            .build()
        notification.flags = Notification.FLAG_AUTO_CANCEL

        notificationManager?.notify(NOTIFICATION_LOVE_PANEL_CODE, notification)
    }

    /**
     * 获取跳转至WeChat PendingIntent
     */
    private fun getWeChatPendingIntent(): PendingIntent {
        val componentName = ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI")
        val intent = Intent(Intent.ACTION_MAIN)
        intent.`package` = packageName
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.component = componentName

        val flag = BaseUtil.getPendingIntentFlag()
        return PendingIntent.getActivity(
            baseContext,
            NOTIFICATION_TO_WECHAT_REQUEST_CODE,
            intent,
            flag
        )

    }

    /* ***************************** LovePanel ********************************* */

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

    /**
     * 获取最后一次屏幕显示信息
     */
    private fun getScreenInfoInLast(): ScreenInfo? {
        val screenInfo: ScreenInfo? = ScreenTimeController.findLastScreenInfo()
        L.i(TAG, "getScreenInfoInLast: ----> $screenInfo")
        return screenInfo
    }

    /**
     * 获取上次显示时间
     */
    private fun getScreenInfoDuration(info: ScreenInfo): Long {
        val startTime: Date? = info.startTime
        val endTime: Date? = info.endTime
        return if (startTime != null && endTime != null) {
            endTime.time - startTime.time
        } else {
            0
        }
    }

    /**
     * 注册通知服务广播
     */
    private fun registerNotificationBroadcast() {
        L.i(TAG, "registerNotificationBroadcast: ")
        val broadcast = NotificationBroadcast()
        val intentFilter = IntentFilter()
            .apply {
                this.addAction(Constants.NotificationServiceParams.NOTIFICATION_SCREEN_ON_ACTION)
                this.addAction(Constants.NotificationServiceParams.NOTIFICATION_SCREEN_OFF_ACTION)
                this.addAction(Constants.NotificationServiceParams.NOTIFICATION_UNLOCK_ACTION)
                this.addAction(Constants.NotificationServiceParams.NOTIFICATION_CREATE_LOVE_PANEL_ACTION)
            }
        registerReceiver(broadcast, intentFilter)
    }

    /**
     * 通知广播
     */
    private inner class NotificationBroadcast : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action ?: ""
            L.i(TAG, "NotificationBroadcast: onReceive: $action ")
            when (action) {
                Constants.NotificationServiceParams.NOTIFICATION_SCREEN_ON_ACTION -> {
                    // 屏幕点亮。
                    L.i(TAG, "onReceive: NOTIFICATION_SCREEN_ON_ACTION ")
                    uploadNotification(true)
                    // TODO: 2021/7/5 用户开始时间手机，计时开始
                }
                Constants.NotificationServiceParams.NOTIFICATION_SCREEN_OFF_ACTION -> {
                    // 屏幕关闭
                    L.i(TAG, "onReceive: NOTIFICATION_SCREEN_OFF_ACTION ")
                    // TODO: 2021/7/5 用户熄屏，手机使用结束，
                }
                Constants.NotificationServiceParams.NOTIFICATION_UNLOCK_ACTION -> {
                    // 解锁
                    L.i(TAG, "onReceive: NOTIFICATION_UNLOCK_ACTION ")
                    uploadNotification(true)
                    // TODO: 2021/7/5 解锁， used.
                }
                // 创建 LovePanel 通知
                Constants.NotificationServiceParams.NOTIFICATION_CREATE_LOVE_PANEL_ACTION -> {
                    L.i(TAG, "onReceive: NOTIFICATION_CREATE_LOVE_PANEL_ACTION ")
                    val bundle = intent?.extras
                    bundle?.let {
                        val num =
                            it.getInt(Constants.NotificationServiceParams.NOTIFICATION_TAG_TITLE)
                        val filePath =
                            it.getString(Constants.NotificationServiceParams.NOTIFICATION_TAG_FILE_PATH)
                        val bitmap = BitmapFactory.decodeFile(filePath)
                        context?.let { c ->
                            val title = c.getString(R.string.love_panel_title_template).format(num)
                            updateLovePanelNotification(title, bitmap)
                        }
                    }
                }
                else -> {
                    L.i(TAG, "onReceive: OTHER.  ")
                }
            }
        }
    }
}