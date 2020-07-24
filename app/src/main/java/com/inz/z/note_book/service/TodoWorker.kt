package com.inz.z.note_book.service

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.inz.z.base.util.LauncherHelper
import com.inz.z.note_book.database.controller.TaskInfoController
import com.inz.z.note_book.database.controller.TaskScheduleController
import com.inz.z.note_book.util.Constants

/**
 * 任务执行
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/07/23 13:48.
 */
class TodoWorker(val context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        val taskScheduleId = inputData.getString(Constants.WorkConstraint.TASK_SCHEDULED_ID) ?: ""
        if (!TextUtils.isEmpty(taskScheduleId)) {
            val taskSchedule = TaskScheduleController.findTaskScheduleById(taskScheduleId)
                ?: return Result.failure()

            val taskInfo =
                TaskInfoController.queryTaskInfoById(taskSchedule.taskId) ?: return Result.failure()
            val action = taskInfo.taskPackageName
            LauncherHelper.launcherPackageName(context, action)
            return Result.failure()
        } else {
            return Result.failure()
        }
    }
}