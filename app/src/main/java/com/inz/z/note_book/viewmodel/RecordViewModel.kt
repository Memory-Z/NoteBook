package com.inz.z.note_book.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.inz.z.base.util.BaseTools
import com.inz.z.note_book.bean.record.RecordInfoStatus
import com.inz.z.note_book.database.bean.RecordInfo
import com.inz.z.note_book.database.controller.RecordInfoController
import freemarker.core.StringArraySequence
import java.util.*
import kotlin.Comparator

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/09/10 14:14.
 */
class RecordViewModel : ViewModel() {

    private var recordInfoList: MutableLiveData<MutableList<RecordInfoStatus>>? = null

    /**
     * 获取记录列表
     */
    fun getRecordInfoList(): MutableLiveData<MutableList<RecordInfoStatus>> {
        if (recordInfoList == null) {
            recordInfoList = MutableLiveData()
            queryRecordList()
        }
        return recordInfoList!!
    }

    /**
     * 设置记录列表
     */
    fun setRecordInfoList(recordInfoList: MutableList<RecordInfo>) {
        recordInfoList.sortWith(RecordListComparator())
        val orderList = mutableListOf<Array<String>>()
        val recordInfoStatusList = mutableListOf<RecordInfoStatus>()
        var lastTime = "0"
        for (position in 0..(recordInfoList.size - 1)) {
            val info = recordInfoList.get(position)

            val recordInfoStatus = RecordInfoStatus()
            recordInfoStatus.setRecordInfo(info)
            recordInfoStatus.isTitle = false
            recordInfoStatusList.add(recordInfoStatus)

            val timeStr =
                BaseTools.getDateFormat("yyyy-MM", Locale.getDefault()).format(info.recordDate)
            if (lastTime != timeStr) {
                orderList.add(arrayOf(position.toString(), timeStr))
                lastTime = timeStr
            }
        }
        for (position in (orderList.size - 1)..0) {
            val recordInfoStatus = RecordInfoStatus()
            recordInfoStatus.isTitle = true
            recordInfoStatus.titleName = orderList.get(position)[1].toString()
            recordInfoStatusList.add(orderList.get(position)[0].toInt(), recordInfoStatus)
        }
        this.recordInfoList?.postValue(recordInfoStatusList)

    }

    /**
     * 刷新
     */
    fun refreshRecordList() {
        queryRecordList()
    }

    /**
     * 查询记录列表
     */
    private fun queryRecordList() {
        val list = RecordInfoController.findAll()
        if (list != null && list.size > 0) {
            setRecordInfoList(list)
        }
    }

    private class RecordListComparator : Comparator<RecordInfo> {
        override fun compare(o1: RecordInfo?, o2: RecordInfo?): Int {
            if (o1 == null || o2 == null) {
                return 1
            }
            return if (o1.recordDate.time > o2.recordDate.time) {
                1
            } else if (o1.recordDate.time < o2.createDate.time) {
                -1
            } else {
                0
            }
        }
    }

}