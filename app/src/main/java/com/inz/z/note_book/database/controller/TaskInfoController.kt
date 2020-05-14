package com.inz.z.note_book.database.controller

import com.alibaba.fastjson.JSON
import com.inz.z.note_book.database.TaskInfoDao
import com.inz.z.note_book.database.bean.TaskInfo
import com.inz.z.note_book.database.util.GreenDaoHelper
import com.inz.z.note_book.database.util.GreenDaoOpenHelper

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
        dao?.insert(taskInfo)
        LogController.log(
            "insert",
            taskInfo,
            "添加任务",
            dao?.tablename ?: "task_info"
        )
    }


}