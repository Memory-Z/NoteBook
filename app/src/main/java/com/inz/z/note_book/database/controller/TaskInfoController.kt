package com.inz.z.note_book.database.controller

import com.inz.z.note_book.database.TaskInfoDao
import com.inz.z.note_book.database.bean.TaskInfo
import com.inz.z.note_book.database.util.GreenDaoHelper

/**
 * 任务控制
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/05/13 13:57.
 */
object TaskInfoController {

    private fun getTaskInfoDao(): TaskInfoDao? {
        return GreenDaoHelper.getInstance().getDaoSession()?.taskInfoDao
    }

    /**
     * 添加任务
     */
    fun insertTaskInfo(taskInfo: TaskInfo) {
        val dao = getTaskInfoDao()
        if (dao != null) {
            val taskId = taskInfo.taskId
            val t = queryTaskInfoById(taskId)
            if (t == null) {
                dao.insert(taskInfo)
                LogController.log("insert", taskInfo, "添加任务", dao.tablename)
            } else {
                updateTaskInfo(taskInfo)
            }
        }
    }

    fun queryTaskInfoById(taskId: String): TaskInfo? =
        getTaskInfoDao()?.queryBuilder()?.where(TaskInfoDao.Properties.TaskId.eq(taskId))?.unique()


    fun updateTaskInfo(taskInfo: TaskInfo) {
        val dao = getTaskInfoDao()
        if (dao != null) {
            dao.update(taskInfo)
            LogController.log("update", taskInfo, "更新任务", dao.tablename)
        }
    }

}