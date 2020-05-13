package com.inz.z.note_book.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.inz.z.note_book.util.Constants
import java.util.*

/**
 *
 * 启动应用服务
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/05/12 17:28.
 */
class LauncherApplicationService : Service() {
    companion object {
        private const val TAG = "LauncherApplicationService"
    }

    private var binder: LauncherAppServiceBinder? = null

    override fun onBind(intent: Intent?): IBinder? {
        if (binder == null) {
            binder = LauncherAppServiceBinder()
        }
        return binder
    }

    inner class LauncherAppServiceBinder : Binder() {
        fun getService(): LauncherApplicationService {
            return this@LauncherApplicationService
        }
    }

    private fun setAlarm() {
        val alarmManager =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.set(Calendar.HOUR_OF_DAY, 18)

        val intent = PendingIntent.getBroadcast(
            applicationContext,
            45,
            null,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager?.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            intent
        )
    }

    private fun setAlarm(time: Long) {
        this.setAlarm(time, false)
    }

    private fun setAlarm(time: Long, repeat: Boolean) {
        val intent = Intent(Constants.AlarmAction.ALARM_BROADCAST_BASE_ACTION)
        val pi = PendingIntent.getBroadcast(
            applicationContext,
            Constants.AlarmAction.REQUEST_CODE_BASE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        this.setAlarm(time, repeat, pi)
    }

    private fun setAlarm(time: Long, repeat: Boolean, pendingIntent: PendingIntent) {
        val alarmManager =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
        if (repeat) {
            alarmManager?.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                time,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        } else {
            alarmManager?.set(AlarmManager.RTC_WAKEUP, time, pendingIntent)
        }
    }



}