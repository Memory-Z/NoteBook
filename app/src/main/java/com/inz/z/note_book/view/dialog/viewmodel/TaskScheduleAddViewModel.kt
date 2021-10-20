package com.inz.z.note_book.view.dialog.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.inz.z.note_book.database.bean.RepeatInfo
import com.inz.z.note_book.database.bean.TaskSchedule
import com.inz.z.note_book.database.controller.RepeatController

/**
 * 添加  任务 计划  ViewModel
 *
 * ====================================================
 * Create by 11654 in 2021/10/20 21:48
 */
class TaskScheduleAddViewModel : ViewModel() {

    /**
     * 计划任务 信息
     */
    private var taskScheduleLiveData: MutableLiveData<TaskSchedule>? = null

    /**
     * 计划 重复信息
     */
    private var repeatInfoListLiveData: MutableLiveData<MutableList<RepeatInfo>>? = null

    /**
     * 获取 重复信息
     * @param taskScheduleId 任务计划 ID
     */
    fun getRepeatInfoList(): MutableLiveData<MutableList<RepeatInfo>>? {
        if (repeatInfoListLiveData == null) {
            repeatInfoListLiveData = MutableLiveData()
        }
        return repeatInfoListLiveData
    }

    /**
     * 获取 任务 计划
     */
    fun getTaskSchedule(): MutableLiveData<TaskSchedule>? {
        if (taskScheduleLiveData == null) {
            taskScheduleLiveData = MutableLiveData()
        }
        return taskScheduleLiveData
    }

    /**
     * 通过 任务-计划 id 查询 重复信息 列表
     */
    fun findTaskScheduleRepeatList(taskScheduleId: String) {
        val list = RepeatController.findRepeatListByTaskScheduleId(taskScheduleId)
        repeatInfoListLiveData?.postValue(list.toMutableList())
    }

    fun destroy() {
        repeatInfoListLiveData = null
        taskScheduleLiveData = null
    }

}