package com.inz.z.note_book.database.controller

import com.inz.z.base.util.BaseTools
import com.inz.z.note_book.bean.inside.ScheduleStatus
import com.inz.z.note_book.database.bean.RepeatInfo
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
        insertScheduleTask(taskInfo, taskSchedule, null)
    }

    /**
     * 添加计划 任务
     * @param taskInfo 任务信息
     * @param taskSchedule 任务计划 信息
     * @param repeatInfoList 重复信息
     */
    fun insertScheduleTask(
        taskInfo: TaskInfo,
        taskSchedule: TaskSchedule,
        repeatInfoList: MutableList<RepeatInfo>?
    ) {
        TaskInfoController.updateTaskInfo(taskInfo)
        TaskScheduleController.updateTaskSchedule(taskSchedule)
        repeatInfoList?.forEach {
            RepeatController.updateRepeatInfo(it)
        }

    }

    /**
     * 通过时间查询计划
     * @param weekDate 周时间
     */
    fun findTaskScheduleListByDate(weekDate: Int): List<TaskSchedule> {
        val repeatInfoList = RepeatController.findRepeatListByWeekDate(weekDate)
        val taskScheduleList = mutableListOf<TaskSchedule>()
        for (repeatInfo in repeatInfoList) {
            val taskSchedule =
                TaskScheduleController.findTaskScheduleById(repeatInfo.taskScheduleId)
            if (taskSchedule != null) {
                taskScheduleList.add(taskSchedule)
            }
        }
        return taskScheduleList
    }

}