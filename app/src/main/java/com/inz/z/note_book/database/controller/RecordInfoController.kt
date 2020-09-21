package com.inz.z.note_book.database.controller

import com.inz.z.note_book.database.RecordInfoDao
import com.inz.z.note_book.database.bean.RecordInfo
import com.inz.z.note_book.database.bean.SearchContentInfo
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
     * 获取存储 记录数据量
     */
    fun findAllCount(): Int {
        getRecordInfoDao()?.apply {
            return this.queryBuilder().list().size
        }
        return 0
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

    /**
     * 获取搜索 结果列表
     * @param content 内容
     * @param limit 个数
     */
    fun querySearchList(content: String, limit: Int): MutableList<RecordInfo>? {
        getRecordInfoDao()?.apply {
            val searchContentInfo = SearchContentInfoController.getSearchContentReal(
                content,
                SearchContentInfo.SEARCH_TYPE_RECORD
            )
            SearchContentInfoController.insertSearchContent(searchContentInfo)
            val queryBuilder = this.queryBuilder()
            return queryBuilder
                .where(
                    queryBuilder.or(
                        RecordInfoDao.Properties.RecordTitle.like(content),
                        RecordInfoDao.Properties.RecordContent.like(content)
                    )
                )
                .orderDesc(RecordInfoDao.Properties.RecordDate)
                .limit(limit)
                .list()
        }
        return null
    }

}