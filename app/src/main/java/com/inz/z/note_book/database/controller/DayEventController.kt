package com.inz.z.note_book.database.controller

import com.inz.z.note_book.database.DayEventInfoDao
import com.inz.z.note_book.database.bean.DayEventInfo
import com.inz.z.note_book.database.util.GreenDaoHelper
import java.util.*

/**
 * 日事件 数据库 控制
 *
 * ====================================================
 * Create by 11654 in 2022/6/9 10:58
 */
object DayEventController {

    /**
     * 获取DayEventInfo Dao 信息
     */
    private fun getDayEventInfoDao(): DayEventInfoDao? =
        GreenDaoHelper.getInstance().getDaoSession()?.dayEventInfoDao

    /**
     * 添加日事件信息
     */
    fun insertDayEventInfo(info: DayEventInfo) {
        getDayEventInfoDao()?.let {
            val queryInfo = queryById(info.dayEventId)
            if (queryInfo == null) {
                it.insert(info)
                LogController.log(
                    LogController.TYPE_INSERT,
                    info,
                    "insert day event info .",
                    DayEventInfoDao.TABLENAME
                )
            } else {
                updateDayEventInfo(info)
            }
        }
    }

    /**
     * 更新 日事件信息
     */
    fun updateDayEventInfo(info: DayEventInfo) {
        getDayEventInfoDao()?.let {
            val queryInfo = queryById(info.dayEventId)
            if (queryInfo != null) {
                it.update(info)
                LogController.log(
                    LogController.TYPE_UPDATE,
                    info,
                    "update day event info.",
                    DayEventInfoDao.TABLENAME
                )
            } else {
                insertDayEventInfo(info)
            }
        }

    }

    /**
     * 通过 ID 查询相关信息
     * @param infoId DayEventInfo id
     * @return DayEventInfo
     */
    fun queryById(infoId: Long): DayEventInfo? {
        return getDayEventInfoDao()?.let {
            val list = it.queryBuilder()
                .where(DayEventInfoDao.Properties.DayEventId.eq(infoId))
                .list()
            if (list.isNullOrEmpty()) {
                null
            } else {
                list[0]
            }
        }
    }

    /**
     * 根据日期查询 相关信息
     * @param date 日期
     */
    fun queryByDate(date: Date): List<DayEventInfo> {
        val currentDate = Calendar.getInstance(Locale.getDefault())
        currentDate.time = date
        currentDate.set(Calendar.SECOND, 0)
        currentDate.set(Calendar.MINUTE, 0)
        currentDate.set(Calendar.HOUR_OF_DAY, 0)
        currentDate.set(Calendar.MILLISECOND, 0)
        getDayEventInfoDao()?.let {
            val query = it.queryBuilder()
            val list = query
                .where(
                    query.or(
                        DayEventInfoDao.Properties.EventDate.eq(date),
                        DayEventInfoDao.Properties.EventShowDayCycle.eq(true)
                    )
                )
                .orderDesc(DayEventInfoDao.Properties.UpdateDate)
                .list()
                .filter { info ->
                    if (!info.eventShowCycle) {
                        true
                    } else {
                        val eventCalendar = Calendar.getInstance(Locale.getDefault())
                            .apply {
                                time = info.eventDate
                            }

                        val difDay =
                            currentDate.get(Calendar.DAY_OF_YEAR) - eventCalendar.get(Calendar.DAY_OF_YEAR)
                        val difMonth =
                            currentDate.get(Calendar.MONTH) - eventCalendar.get(Calendar.MONTH)
                        val difYear =
                            currentDate.get(Calendar.YEAR) - eventCalendar.get(Calendar.YEAR)
                        // 计算日周期
                        val cycleDay = info.eventShowDayCycle
                        // 计算月周期
                        val cycleMonth = info.eventShowMonthCycle
                        // 计算年周期
                        val cycleYear = info.eventShowYearCycle
                        var isCycleDate = true
                        if (cycleYear != -1) {
                            isCycleDate = difYear % cycleYear == 0
                        }
                        if (cycleMonth != -1) {
                            isCycleDate = difMonth % cycleMonth == 0
                        }
                        if (cycleDay != -1) {
                            isCycleDate = difDay % cycleDay == 0
                        }
                        isCycleDate
                    }
                }
            if (!list.isEmpty()) {
                return list
            } else {
                return mutableListOf()
            }
        }
        return mutableListOf()
    }

    fun queryByDateRound(startDate: Date, endDate: Date): List<DayEventInfo> {
        val startCalendar = Calendar.getInstance(Locale.getDefault())
            .apply {
                time = startDate
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
        val endCalendar = Calendar.getInstance(Locale.getDefault())
            .apply {
                time = endDate
                set(Calendar.DATE, get(Calendar.DATE) + 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

        return mutableListOf()
    }
}