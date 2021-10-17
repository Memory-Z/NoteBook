package com.inz.z.note_book.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleService
import androidx.work.*
import com.inz.z.base.util.L
import com.inz.z.note_book.bean.inside.ScheduleStatus
import com.inz.z.note_book.bean.inside.ScheduleWeekDate
import com.inz.z.note_book.bean.inside.TaskAction
import com.inz.z.note_book.database.bean.TaskInfo
import com.inz.z.note_book.database.bean.TaskSchedule
import com.inz.z.note_book.database.controller.RepeatController
import com.inz.z.note_book.database.controller.TaskInfoController
import com.inz.z.note_book.database.controller.TaskScheduleController
import com.inz.z.note_book.util.ClockAlarmManager
import com.inz.z.note_book.util.Constants
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

/**
 * 计划执行服务
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/05/25 14:13.
 */
class ScheduleService : LifecycleService() {
    companion object {
        private const val TAG = "ScheduleService"

        /**
         * 延迟时间间隔
         */
        private const val DELAY_TIME = 1000 * 60 * 3
    }

    private var binder: ScheduleServiceBinder? = null
    private var executors: ScheduledExecutorService? = null


    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        if (binder == null) {
            binder = ScheduleServiceBinder()
        }
        return binder
    }

    public inner class ScheduleServiceBinder : Binder() {

        /**
         * 获取服务
         */
        fun getService(): ScheduleService {
            return this@ScheduleService
        }
    }

    override fun onCreate() {
        super.onCreate()
        L.i(TAG, "onCreate: ------------ ")
        executors = Executors.newScheduledThreadPool(8)

    }

    override fun onDestroy() {
        super.onDestroy()
        L.i(TAG, "onDestroy: ----------------- ")
        if (executors != null) {
            executors!!.shutdown()
            executors = null
        }
        binder = null
    }

    /**
     * 检测计划线程
     */
    private inner class CheckScheduleThread() : Thread() {


        override fun run() {
            super.run()
            val calendar = Calendar.getInstance(Locale.getDefault())
            val currentDate = calendar.time
            val weekDay = calendar.get(Calendar.DAY_OF_WEEK);
            val monthDay = calendar.get(Calendar.DAY_OF_MONTH)
            val yearDate = calendar.get(Calendar.MONTH)
            // 通过当前 周目 查找对应的计划
//            val taskScheduleList = ScheduleController.findTaskScheduleListByDate(weekDay)
            val taskScheduleList = TaskScheduleController.findTaskScheduleByDate(currentDate)
            for (taskSchedule in taskScheduleList) {
                L.i(TAG, "CheckScheduleThread: ----- > $taskSchedule ")
                val scheduleDate = taskSchedule.scheduleTime
                // 计划执行时间
                val scheduleCalendar = Calendar.getInstance(Locale.getDefault())
                scheduleCalendar.apply {
                    time = scheduleDate
                    set(Calendar.YEAR, calendar.get(Calendar.YEAR))
                    set(Calendar.MONTH, calendar.get(Calendar.MONTH))
                    set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH))
                    set(Calendar.MILLISECOND, 0)
                    firstDayOfWeek = Calendar.MONDAY
                }
                // 计划状态
                val scheduleStatus = taskSchedule.scheduleStatus
                // 是否重复
                val scheduleRepeat = taskSchedule.scheduleRepeat
                var needDoing = false
                if (scheduleRepeat) {
                    val repeatInfoList =
                        RepeatController.findRepeatListByTaskScheduleId(taskSchedule.taskScheduleId)
                    for (repeatInfo in repeatInfoList) {
                        // 每日重复
                        needDoing = repeatInfo.repeatDate != -1
                        if (needDoing) {
                            L.i(TAG, "CheckScheduleThread: --> this repeat day have doing ,")
                            break
                        }
                        // 重复周
                        needDoing = repeatInfo.repeatWeek == weekDay
                        if (needDoing) {
                            L.i(TAG, "CheckScheduleThread: --> this repeat week have doing ,")
                            break
                        }
                        // 按年重复
                        needDoing = repeatInfo.repeatYear == yearDate && needDoing
                        if (needDoing) {
                            L.i(TAG, "CheckScheduleThread: --> this repeat year have doing ,")
                            break
                        }
                        // 重复月
                        needDoing = repeatInfo.repeatMonth == monthDay + 1
                        if (needDoing) {
                            L.i(TAG, "CheckScheduleThread: --> this repeat month have doing ,")
                            break
                        }
                    }
                    val diffTime = scheduleCalendar.timeInMillis - System.currentTimeMillis()
                    if (diffTime < DELAY_TIME) {
                        // 计划执行时间 与 当前时间 小于 延迟时间 ，不需要进行
                        needDoing = false
                        L.w(TAG, "CheckScheduleThread: -- difference time: $diffTime.")
                    }
                } else {
                    val scheduleDay = scheduleCalendar.get(Calendar.DAY_OF_MONTH)
//                    val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
                    L.i(TAG, "------- $scheduleDay --- $monthDay")
                    needDoing = (scheduleStatus == ScheduleStatus.DOING)
                            && (scheduleDay == monthDay)
                }
//                if (needDoing) {
//                    needDoing = scheduleCalendar.timeInMillis > System.currentTimeMillis()
//                    L.i(TAG, "${this.name}: this task schedule is old ")
//                }
                if (needDoing) {
                    L.i(TAG, "${this.name}: this task's schedule need doing. ")
                    val isDone = executeScheduleTask(scheduleCalendar.timeInMillis, taskSchedule)
                    if (isDone) {
                        taskSchedule.updateTime = Calendar.getInstance(Locale.getDefault()).time
                        if (!scheduleRepeat) {
                            taskSchedule.setScheduleStatus(ScheduleStatus.FINISHED)
                        }
                        TaskScheduleController.updateTaskSchedule(taskSchedule)
                    }
                } else {
                    L.w(TAG, "${this.name}: this task's schedule needn't do. ")
                    if (currentDate.time > scheduleDate.time) {
                        L.w(TAG, "${this.name}: this task's schedule is old schedule. ")
                        cancelScheduleTask(taskSchedule)
                    } else {
                        L.w(TAG, "${this.name}: this task's schedule have time to do it . ")
                    }
                }
            }

        }
    }

    /**
     * 检测计划是否需要继续执行
     */
    private fun checkScheduleStatus(
        status: ScheduleStatus,
        calendar: Calendar,
        scheduleWeekDate: ScheduleWeekDate
    ): Boolean {
        return when (status) {
            ScheduleStatus.DOING -> {
                val scheduleDate = scheduleWeekDate.value
                val calendarDate = calendar.get(Calendar.DAY_OF_WEEK)
                L.i(TAG, "checkScheduleStatus: ---------- $scheduleDate ---- $calendarDate ")
                scheduleDate == calendarDate
            }
            else -> {
                false
            }
        }
    }

    /**
     * 执行计划任务
     */
    private fun executeScheduleTask(
        executeTime: Long,
        taskSchedule: TaskSchedule
    ): Boolean {
        val taskInfo = TaskInfoController.queryTaskInfoById(taskSchedule.taskId)
        if (taskInfo != null) {
            L.i(
                TAG,
                "executeScheduleTask: exectue task info $taskInfo. ----> TIME :::: $executeTime ----------- ${System.currentTimeMillis()}"
            )
            val pendingIntent = getTaskContentIntent(taskInfo)
            ClockAlarmManager.setAlarm(applicationContext, executeTime, false, pendingIntent)
            return true
        }
        return false
    }

    /**
     * 取消执行任务
     */
    private fun cancelScheduleTask(taskSchedule: TaskSchedule) {
        val taskInfo = TaskInfoController.queryTaskInfoById(taskSchedule.taskId)
        if (taskInfo != null) {
            L.w(TAG, "cancelScheduleTask: cancel task info $taskInfo. ")
            val pendingIntent = getTaskContentIntent(taskInfo)
            ClockAlarmManager.cancelAlarm(applicationContext, pendingIntent)
        }
    }

    /**
     * 获取任务 Intent
     */
    private fun getTaskContentIntent(taskInfo: TaskInfo): PendingIntent {
        val taskAction = TaskAction.NONE.getTaskAction(taskInfo.taskAction)
        val intent = Intent()
        intent.`package` = applicationContext.packageName
        when (taskAction) {
            TaskAction.LAUNCHER -> {
                intent.action = Constants.AlarmAction.ALARM_BROADCAST_LAUNCHER_ACTION
                val bundle = Bundle()
                bundle.putString(
                    Constants.AlarmAction.ALARM_BROADCAST_LAUNCHER_FLAG_PACKAGE_NAME,
                    taskInfo.taskPackageName
                )
                bundle.putString(
                    Constants.AlarmAction.ALARM_BROADCAST_LAUNCHER_FLAG_LABEL,
                    taskInfo.taskDescribe
                )
                intent.putExtras(bundle)
            }
            TaskAction.HINT -> {
                intent.action = Constants.AlarmAction.ALARM_BROADCAST_HINT_ACTION
                val bundle = Bundle()
                bundle.putString(
                    Constants.AlarmAction.ALARM_BROADCAST_HINT_FLAG_MESSAGE,
                    taskInfo.taskDescribe
                )
                intent.putExtras(bundle)
            }
            TaskAction.CLOCK -> {
                intent.action = Constants.AlarmAction.ALARM_BROADCAST_CLOCK_ACTION

            }
            else -> {
                intent.action = Constants.AlarmAction.ALARM_BROADCAST_SCHEDULE_ACTION
            }
        }
        return PendingIntent.getBroadcast(
            applicationContext,
            taskInfo.taskRequestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }


    ////////////////////////////////////////////////////////////////////
    // 对外接口
    ////////////////////////////////////////////////////////////////////

    /**
     * 开始检测计划线程
     */
    fun startCheckScheduleThread() {
        L.i(TAG, "startCheckScheduleThread: ")
        executors?.execute(CheckScheduleThread())
    }

    /**
     * 开始执行任务
     */
    public fun startTodoWork(taskId: String) {
        val workManager = WorkManager.getInstance(applicationContext)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresStorageNotLow(false)
            .setRequiresBatteryNotLow(false)
            .setRequiresCharging(false)
            .build()

        val data = Data.Builder()
            .putString(Constants.WorkConstraint.TASK_SCHEDULED_ID, taskId)
            .build()

        val request = OneTimeWorkRequestBuilder<TodoWorker>()
            .setConstraints(constraints)
            .setInputData(data)
            .addTag(Constants.WorkConstraint.TASK_SCHEDULED_TAG)
            .build()

        workManager.enqueue(request)

        WorkManager.getInstance(applicationContext).getWorkInfoByIdLiveData(request.id)
            .observe(
                this,
                androidx.lifecycle.Observer { workInfo ->
                    if (workInfo != null) {
                        when (workInfo.state) {
                            WorkInfo.State.SUCCEEDED -> {
                                L.i(TAG, "Successed. ")
                            }
                            WorkInfo.State.FAILED -> {
                                L.i(TAG, "failed ")
                            }
                            else -> {
                                L.i(TAG, "not deal this work info state : ${workInfo.state}")
                            }
                        }
                    }
                }
            )


    }
}