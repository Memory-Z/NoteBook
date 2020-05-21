package com.inz.z.note_book.database.controller

import com.inz.z.note_book.bean.ScheduleStatus
import com.inz.z.note_book.database.TaskScheduleDao
import com.inz.z.note_book.database.bean.TaskSchedule
import com.inz.z.note_book.database.util.GreenDaoHelper
import com.inz.z.note_book.database.util.GreenDaoOpenHelper
import org.greenrobot.greendao.query.WhereCondition
import java.util.*
import kotlin.math.min

/**
 * 任务 计划 控制
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/05/18 13:32.
 */
object TaskScheduleController {
    private fun getTaskScheduleDao(): TaskScheduleDao? =
        GreenDaoHelper.getInstance().getDaoSession()?.taskScheduleDao

    fun insertTaskSchedule(taskSchedule: TaskSchedule) {
        val dao = getTaskScheduleDao()
        if (dao != null) {
            val taskScheduleId = taskSchedule.taskScheduleId
            val schedule = findTaskScheduleById(taskScheduleId)
            if (schedule == null) {
                dao.insert(taskSchedule)
                LogController.log("Insert", taskSchedule, "添加任务计划", dao.tablename)
            } else {
                dao.update(taskSchedule)
                LogController.log("Upload", taskSchedule, "更新任务计划", dao.tablename)
            }
        }
    }

    fun findTaskScheduleByTag(tag: String): MutableList<TaskSchedule>? {
        val dao = getTaskScheduleDao()
        if (dao != null) {
            val query = dao.queryBuilder()
            return query
                .where(
                    TaskScheduleDao.Properties.ScheduleTag.eq(tag)
                )
                .orderDesc(TaskScheduleDao.Properties.ScheduleTime)
                .list()
        }
        return null
    }

    fun findTaskScheduleById(scheduleId: String): TaskSchedule? {
        return getTaskScheduleDao()
            ?.queryBuilder()
            ?.where(TaskScheduleDao.Properties.TaskScheduleId.eq(scheduleId))
            ?.orderDesc(TaskScheduleDao.Properties.ScheduleTime)
            ?.list()?.get(0)
    }

    fun updateTaskSchedule(taskSchedule: TaskSchedule) {
        val dao = getTaskScheduleDao()
        if (dao != null) {
            dao.update(taskSchedule)
            LogController.log("update", taskSchedule, "更新任务计划", dao.tablename)
        }
    }

    /**
     * 通过日期查询当天任务
     */
    fun findTaskScheduleByDate(date: Date): MutableList<TaskSchedule> {
        val dao = getTaskScheduleDao()
        val minCalendar = Calendar.getInstance(Locale.getDefault())
        minCalendar.time = date
        minCalendar.set(Calendar.HOUR_OF_DAY, 0)
        minCalendar.set(Calendar.MINUTE, 0)
        minCalendar.set(Calendar.MILLISECOND, 0)
        val maxCalendar = Calendar.getInstance(Locale.getDefault())
        maxCalendar.time = minCalendar.time
        maxCalendar.set(Calendar.DATE, minCalendar.get(Calendar.DATE) + 1)
        val queryBuilder = dao?.queryBuilder()
        return queryBuilder
            ?.where(
//                TaskScheduleDao.Properties.ScheduleTime.between(minCalendar.time, maxCalendar.time)
                queryBuilder.and(
                    TaskScheduleDao.Properties.ScheduleTime.gt(minCalendar.time),
                    TaskScheduleDao.Properties.ScheduleTime.lt(maxCalendar.time)
                )

            )
            ?.list()
            ?: mutableListOf()
    }


}