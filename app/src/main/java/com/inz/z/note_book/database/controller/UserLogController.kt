package com.inz.z.note_book.database.controller

import com.inz.z.note_book.database.bean.UserLogInfo
import com.inz.z.note_book.database.util.GreenDaoHelper
import java.util.*

/**
 *
 * 用户日志信息
 * ====================================================
 * Create by 11654 in 2021/1/1 1:10
 */
object UserLogController {

    private fun getDao() = GreenDaoHelper.getInstance().getDaoSession()?.userLogInfoDao

    fun insertUserLog(
        userId: Long,
        logType: String,
        describe: String,
        oldValue: String,
        newValue: String
    ) {
        val date = Calendar.getInstance(Locale.getDefault()).time
        val logInfo = UserLogInfo()
            .apply {
                this.userId = userId
                this.logDescribe = describe
                this.logType = logType
                this.oldValue = oldValue
                this.newValue = newValue
                this.createDate = date
            }
        getDao()?.insert(logInfo)
    }
}