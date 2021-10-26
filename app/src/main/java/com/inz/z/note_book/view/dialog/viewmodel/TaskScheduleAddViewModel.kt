package com.inz.z.note_book.view.dialog.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.inz.z.base.util.BaseTools
import com.inz.z.note_book.base.RepeatType
import com.inz.z.note_book.database.bean.RepeatInfo
import com.inz.z.note_book.database.bean.TaskInfo
import com.inz.z.note_book.database.bean.TaskSchedule
import com.inz.z.note_book.database.bean.local.TaskRepeatInfo
import com.inz.z.note_book.database.controller.RepeatController
import com.inz.z.note_book.database.controller.ScheduleController
import com.inz.z.note_book.database.controller.TaskInfoController
import com.inz.z.note_book.database.controller.TaskScheduleController
import java.util.*

/**
 * 添加  任务 计划  ViewModel
 *
 * ====================================================
 * Create by 11654 in 2021/10/20 21:48
 */
class TaskScheduleAddViewModel : ViewModel() {

    /**
     * 任务信息
     */
    private var taskInfoLiveDate: MutableLiveData<TaskInfo>? = null

    /**
     * 计划任务 信息
     */
    private var taskScheduleLiveData: MutableLiveData<TaskSchedule>? = null

    /**
     * 计划 重复信息
     */
    private var repeatInfoListLiveData: MutableLiveData<RepeatInfo>? = null

    /**
     * 获取 重复信息
     * @param taskScheduleId 任务计划 ID
     */
    fun getRepeatInfoList(taskScheduleId: String?): MutableLiveData<RepeatInfo>? {
        if (repeatInfoListLiveData == null) {
            repeatInfoListLiveData = MutableLiveData()
        }
        taskScheduleId?.let {
            findTaskScheduleRepeatList(it)
        }
        return repeatInfoListLiveData
    }

    /**
     * 获取 任务 计划
     */
    fun getTaskSchedule(taskScheduleId: String?): MutableLiveData<TaskSchedule>? {
        if (taskScheduleLiveData == null) {
            taskScheduleLiveData = MutableLiveData()
        }
        taskScheduleId?.let {
            findTaskScheduleInfoById(it)
        }
        return taskScheduleLiveData
    }

    /**
     *  获取 任务信息
     */
    fun getTaskInfo(taskId: String?): MutableLiveData<TaskInfo>? {
        if (taskInfoLiveDate == null) {
            taskInfoLiveDate = MutableLiveData()
        }
        findTaskInfoByTaskId(taskId)
        return taskInfoLiveDate
    }

    /**
     * 获取 任务计划 信息
     * @param taskScheduleId ID
     */
    private fun findTaskScheduleInfoById(taskScheduleId: String) {
        val taskSchedule = TaskScheduleController.findTaskScheduleById(taskScheduleId)
        taskScheduleLiveData?.postValue(taskSchedule)
    }

    /**
     * 通过 任务-计划 id 查询 重复信息 列表
     */
    private fun findTaskScheduleRepeatList(taskScheduleId: String) {
        val list = RepeatController.findRepeatListByTaskScheduleId(taskScheduleId)
        val repeat = if (list.isNotEmpty()) list[0] else null
        repeatInfoListLiveData?.postValue(repeat)
    }

    /**
     * 查询任务信息
     * @param taskId 任务 ID
     */
    fun findTaskInfoByTaskId(taskId: String?) {
        taskId?.let {
            val taskInfo = TaskInfoController.queryTaskInfoById(it)
            taskInfoLiveDate?.postValue(taskInfo)
        }

    }

    fun destroy() {
        repeatInfoListLiveData = null
        taskScheduleLiveData = null
    }

    /**
     * 更新 任务计划 信息
     */
    fun updateTaskScheduleInfo(taskInfo: TaskInfo, schedule: TaskSchedule) {
        val calendar = Calendar.getInstance(Locale.getDefault())
        taskInfo.apply {
            this.updateTime = calendar.time
        }
        schedule.apply {
            this.updateTime = calendar.time
        }
        // 更新数据库
        ScheduleController.insertScheduleTask(taskInfo, schedule)
        taskInfoLiveDate?.postValue(taskInfo)
        taskScheduleLiveData?.postValue(schedule)
    }

    /**
     * 更新重复类型 信息
     * @param taskScheduleId 任务计划id
     * @param repeatType 重复类型
     * @param repeatDataArray 重复日期数据
     */
    fun updateRepeatInfo(
        taskScheduleId: String,
        @RepeatType repeatType: Int,
        repeatDataArray: IntArray?
    ) {
        var repeatInfo = repeatInfoListLiveData?.value
        val calendar = Calendar.getInstance(Locale.getDefault())
        // 如果无重复 日期数据, 或 获取的数据与需要保存的 数据不符 。新建
        if (repeatInfo == null || repeatInfo.taskScheduleId != taskScheduleId) {
            repeatInfo = RepeatInfo()
                .apply {
                    this.taskScheduleId = taskScheduleId
                    repeatId = BaseTools.getUUID()
                    createDate = calendar.time
                }
        }
        repeatInfo.apply {
            this.repeatType = repeatType
            setCustomRepeatDate(repeatDataArray)
            updateDate = calendar.time
        }
        // 更新数据库。
        RepeatController.updateRepeatInfo(repeatInfo)
        repeatInfoListLiveData?.postValue(repeatInfo)
    }

}