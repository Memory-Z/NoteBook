package com.inz.z.note_book.database.controller

import androidx.annotation.NonNull
import com.alibaba.fastjson.JSON
import com.inz.z.base.util.BaseTools
import com.inz.z.base.util.L
import com.inz.z.note_book.database.OperationLogInfoDao
import com.inz.z.note_book.database.bean.OperationLogInfo
import com.inz.z.note_book.database.util.GreenDaoHelper
import java.util.*

/**
 *
 * 日志 控制 -->
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/05/13 13:59.
 */
object LogController {
    private fun getLogDao(): OperationLogInfoDao? {
        return GreenDaoHelper.getInstance().getDaoSession()?.operationLogInfoDao
    }

    /**
     * 保存日志
     *
     */
    fun log(
        operationType: String,
        data: Any,
        describe: String,
        tableName: String
    ) {
        val log = OperationLogInfo()
        log.apply {
            this.operationLogId = BaseTools.getUUID()
            this.operationData = JSON.toJSONString(data)
            this.tableName = tableName.toUpperCase(Locale.getDefault())
            this.operationType = operationType.toUpperCase(Locale.getDefault())
            this.operationDescribe = describe
            val calendar = Calendar.getInstance(Locale.getDefault())
            this.createTime = calendar.time
            this.updateTime = calendar.time
        }
        val dao = getLogDao()
        dao?.insert(log)
    }

    fun query(): List<OperationLogInfo>? {
        val dao = getLogDao()
        return dao?.queryBuilder()?.list()
    }

    /**
     *  通过时间查询操作日志
     */
    fun queryLogByTime(startTime: Date, endTime: Date): MutableList<OperationLogInfo>? {
        getLogDao()?.apply {
            val queryBuilder = queryBuilder()
            return queryBuilder
                .where(
                    queryBuilder.and(
                        OperationLogInfoDao.Properties.CreateTime.gt(startTime),
                        OperationLogInfoDao.Properties.CreateTime.lt(endTime)
                    )
                )
                .orderAsc(OperationLogInfoDao.Properties.CreateTime)
                .list()
        }
        return null
    }

    /**
     * 通过操作表明 查询操作日志
     */
    fun queryLogByTable(tableName: String): MutableList<OperationLogInfo>? {
        getLogDao()?.apply {
            val queryBuilder = queryBuilder()
            return queryBuilder
                .where(
                    OperationLogInfoDao.Properties.TableName.eq(tableName)
                )
                .orderDesc(OperationLogInfoDao.Properties.CreateTime)
                .list()
        }
        return null
    }

    /**
     * 删除操作记录
     */
    fun deleteLog(@NonNull logInfo: OperationLogInfo) {
        getLogDao()?.apply {
            delete(logInfo)
        }
    }

}