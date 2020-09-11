package com.inz.z.note_book.database.controller

import com.inz.z.note_book.database.RecordInfoDao
import com.inz.z.note_book.database.bean.RecordInfo
import com.inz.z.note_book.database.util.GreenDaoHelper

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/09/10 13:10.
 */
object RecordInfoController {
    /**
     * 获取 RecordInfoDao
     */
    private fun getRecordInfoDao(): RecordInfoDao? {
        return GreenDaoHelper.getInstance().getDaoSession()?.recordInfoDao
    }

    /**
     * 添加记录 信息
     */
    fun insertRecord(recordInfo: RecordInfo) {
        val recordInfoInDb = findById(recordInfo.id)
        if (recordInfoInDb == null) {
            getRecordInfoDao()?.apply {
                this.insert(recordInfo)
                LogController.log("insert", recordInfo, "insert Record info ", this.tablename)
            }
        } else {
            updateRecord(recordInfo)
        }
    }

    /**
     * 更新记录
     */
    fun updateRecord(recordInfo: RecordInfo) {
        val recordInfoInDb = findById(recordInfo.id)
        if (recordInfoInDb != null) {
            getRecordInfoDao()?.apply {
                this.update(recordInfo)
                LogController.log("update", recordInfo, "update Record info", this.tablename)
            }
        } else {
            insertRecord(recordInfo)
        }
    }

    /**
     * 通过ID 查询
     */
    fun findById(id: String): RecordInfo? {
        getRecordInfoDao()?.apply {
            val list = this.queryBuilder()
                .where(RecordInfoDao.Properties.Id.eq(id))
                .orderDesc(RecordInfoDao.Properties.RecordDate)
                .build()
                .list()
            if (list != null && list.size > 0) {
                return list.get(0)
            }
        }
        return null
    }

    /**
     * 查询 全部
     */
    fun findAll(): MutableList<RecordInfo>? {
        getRecordInfoDao()?.apply {
            return this.queryBuilder()
                .orderDesc(RecordInfoDao.Properties.RecordDate)
                .list()
        }
        return null
    }

    /**
     * 通过时间串查询
     */
    fun queryByTime(timeStr: String): MutableList<RecordInfo>? {
        getRecordInfoDao()?.apply {
            return this.queryBuilder()
                .apply {
                    this.and(
                        RecordInfoDao.Properties.Enable.eq(RecordInfo.ENABLE_STATE_USE),
                        RecordInfoDao.Properties.RecordDate.like(timeStr)
                    )
                }
                .orderDesc(RecordInfoDao.Properties.RecordDate)
                .list()
        }
        return null
    }

}