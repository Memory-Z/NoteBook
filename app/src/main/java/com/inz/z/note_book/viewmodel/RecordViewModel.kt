package com.inz.z.note_book.viewmodel

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.inz.z.base.util.BaseTools
import com.inz.z.note_book.bean.record.RecordInfoStatus
import com.inz.z.note_book.database.bean.RecordInfo
import com.inz.z.note_book.database.controller.RecordInfoController
import java.util.*

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/09/10 14:14.
 */
class RecordViewModel : ViewModel() {

    private var recordInfoList: MutableLiveData<MutableList<RecordInfoStatus>>? = null

    /**
     * 记录列表数量
     */
    private var recordListSizeLiveData: MutableLiveData<Int>? = null

    /**
     * 获取记录列表
     */
    fun getRecordInfoList(): MutableLiveData<MutableList<RecordInfoStatus>> {
        if (recordInfoList == null) {
            recordInfoList = MutableLiveData()
            queryRecordList("")
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
        queryRecordList("")
    }

    /**
     * 查询记录列表
     */
    fun queryRecordList(searchContent: String) {
        var list: MutableList<RecordInfo>? = null
        if (TextUtils.isEmpty(searchContent)) {
            list = RecordInfoController.findAll()
        } else {
            list = RecordInfoController.querySearchList(searchContent, 10)
        }
        resetRecordList(list)
    }

    private fun resetRecordList(list: MutableList<RecordInfo>?) {
        recordListSizeLiveData?.postValue(if (list == null) 0 else list.size)
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

    /**
     * 获取记录数量
     */
    fun getRecordInfoListSize(): MutableLiveData<Int> {
        if (recordListSizeLiveData == null) {
            recordListSizeLiveData = MutableLiveData(0)
            queryRecordList("")
        }
        return recordListSizeLiveData!!
    }
}