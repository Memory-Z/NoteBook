package com.inz.z.note_book.database.controller

import com.inz.z.base.util.BaseTools
import com.inz.z.calendar_view.Tools
import com.inz.z.note_book.bean.ScheduleStatus
import com.inz.z.note_book.database.bean.TaskInfo
import com.inz.z.note_book.database.bean.TaskSchedule
import java.util.*

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/05/18 13:30.
 */
object ScheduleController {

    /**
     * 添加任务计划
     */
    fun insertScheduleTask(taskInfo: TaskInfo) {
        val taskSchedule = TaskSchedule()
        taskSchedule.apply {
            taskId = taskInfo.taskId
            taskScheduleId = BaseTools.getUUID()
            val calendar = Calendar.getInstance(Locale.getDefault())
            status = ScheduleStatus.NOT_STARTED.value
            scheduleTime = calendar.time
            createTime = calendar.time
            updateTime = calendar.time
        }
        insertScheduleTask(taskInfo, taskSchedule)
    }

    /**
     * 添加任务计划
     */
    fun insertScheduleTask(taskInfo: TaskInfo, taskSchedule: TaskSchedule) {
        TaskInfoController.insertTaskInfo(taskInfo)
        TaskScheduleController.insertTaskSchedule(taskSchedule)
    }

}