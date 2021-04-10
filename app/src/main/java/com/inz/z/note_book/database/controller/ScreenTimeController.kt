package com.inz.z.note_book.database.controller

import com.inz.z.note_book.database.ScreenInfoDao
import com.inz.z.note_book.database.bean.ScreenInfo
import com.inz.z.note_book.database.util.GreenDaoHelper
import com.inz.z.note_book.database.util.GreenDaoOpenHelper
import org.greenrobot.greendao.database.DatabaseOpenHelper
import java.util.*

/**
 * 屏幕显示 数据 控制
 *
 * ====================================================
 * Create by 11654 in 2021/4/3 20:45
 */
object ScreenTimeController {

    private fun getScreenTimeDao(): ScreenInfoDao? =
        GreenDaoHelper.getInstance().getDaoSession()?.screenInfoDao

    /**
     * 创建屏幕显示
     */
    fun createScreenInfo(startTime: Date): ScreenInfo? {
        var screenInfo: ScreenInfo? = null
        getScreenTimeDao()?.let {
            screenInfo = ScreenInfo()
                .apply {
                    this.startTime = startTime
                }
            it.insert(screenInfo)
            LogController.log(
                LogController.TYPE_INSERT,
                screenInfo!!,
                "insert screen info",
                it.tablename
            )
        }
        return screenInfo
    }

    /**
     * 保存屏幕信息
     */
    fun saveScreenInfo(info: ScreenInfo) {
        getScreenTimeDao()?.let {
            it.update(info)
            LogController.log(
                LogController.TYPE_UPDATE,
                info,
                "save screen info",
                it.tablename
            )
        }
    }

    /**
     * 保存屏幕解锁时间
     */
    fun saveScreenUnlock(info: ScreenInfo): ScreenInfo {
        getScreenTimeDao()?.let {
            it.update(info)
            LogController.log(
                LogController.TYPE_UPDATE,
                info,
                "update unlock info",
                it.tablename
            )
        }
        return info
    }

    /**
     * 通过时间获取显示记录信息
     */
    fun screenInfoListWithDay(time: Date): MutableList<ScreenInfo>? {
        val beforeCalendar = Calendar.getInstance(Locale.getDefault())
        beforeCalendar.apply {
            this.time = time
            this.set(Calendar.HOUR_OF_DAY, 0)
            this.set(Calendar.MINUTE, 0)
            this.set(Calendar.SECOND, 0)
        }
        val afterCalendar = Calendar.getInstance(Locale.getDefault())
        afterCalendar.apply {
            val date = this.get(Calendar.DATE)
            this.set(Calendar.DATE, date + 1)
            this.set(Calendar.HOUR_OF_DAY, 0)
            this.set(Calendar.MINUTE, 0)
            this.set(Calendar.SECOND, 0)
        }

        getScreenTimeDao()?.let {
            val query = it.queryBuilder()
            return query
                .where(
                    query.and(
                        ScreenInfoDao.Properties.StartTime.between(beforeCalendar, afterCalendar),
                        ScreenInfoDao.Properties.EndTime.isNotNull
                    )
                )
                .orderAsc(ScreenInfoDao.Properties.StartTime)
                .list()
        }
        return null
    }

    /**
     * 获取最后一次屏幕显示信息
     */
    fun findLastScreenInfo(): ScreenInfo? {
        getScreenTimeDao()?.let {
            val query = it.queryBuilder()
            val list = query
                .where(ScreenInfoDao.Properties.EndTime.isNotNull)
                .orderDesc(ScreenInfoDao.Properties.StartTime)
                .list()
            if (list != null && list.size > 0) {
                return list[0]
            }
        }
        return null
    }
}