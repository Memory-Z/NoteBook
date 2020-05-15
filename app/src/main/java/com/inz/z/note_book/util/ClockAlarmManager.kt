package com.inz.z.note_book.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*

/**
 *
 * 闹钟管理
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/01/15 16:33.
 */
object ClockAlarmManager {


    private fun getAlarmManager(context: Context): AlarmManager? {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
        if (alarmManager != null) {
            return alarmManager
        }
        return null
    }

//    public fun getNextAlarm(context: Context) {
//        val alarmManager = getAlarmManager(context)
//        val clock = alarmManager?.nextAlarmClock
//        if (clock != null) {
//
//        }
//    }

    fun addClockAlarm(context: Context, triggerAtMillis: Long, operation: PendingIntent) {
        val alarmManager = getAlarmManager(context)
        alarmManager?.setRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            triggerAtMillis,
            AlarmManager.INTERVAL_DAY,
            operation
        )
    }


    /**
     * 设置闹钟
     */
    fun setAlarm(context: Context, time: Long) {
        this.setAlarm(context, time, false)
    }

    /**
     * 设置闹钟
     */
    fun setAlarm(context: Context, time: Long, repeat: Boolean) {
        val intent = Intent()
        intent.action = Constants.AlarmAction.ALARM_BROADCAST_BASE_ACTION
        intent.`package` = context.packageName
        val pi = PendingIntent.getBroadcast(
            context,
            Calendar.getInstance(Locale.getDefault()).get(Calendar.MILLISECOND),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        this.setAlarm(context, time, repeat, pi)
    }

    /**
     * 设置闹钟
     */
    fun setAlarm(context: Context, time: Long, repeat: Boolean, pendingIntent: PendingIntent) {
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
        if (repeat) {
            alarmManager?.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                time,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        } else {
            alarmManager?.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent)
        }
    }
}