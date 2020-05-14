package com.inz.z.note_book.database.controller

import com.alibaba.fastjson.JSON
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
            val calendar = Calendar.getInstance(Locale.getDefault())
            this.operationData = JSON.toJSONString(data)
            this.tableName = tableName
            this.operationType = operationType
            this.operationDescribe = describe
            this.operationLogId = UUID.randomUUID().toString()
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

}