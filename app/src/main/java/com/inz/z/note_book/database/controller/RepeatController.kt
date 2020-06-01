package com.inz.z.note_book.database.controller

import com.inz.z.base.util.BaseTools
import com.inz.z.note_book.database.RepeatInfoDao
import com.inz.z.note_book.database.bean.RepeatInfo
import com.inz.z.note_book.database.util.GreenDaoHelper
import java.util.*

/**
 * 重复信息
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/05/27 14:06.
 */
object RepeatController {

    private fun getRepeatDao(): RepeatInfoDao? =
        GreenDaoHelper.getInstance().getDaoSession()?.repeatInfoDao

    /**
     * 通过任务计划ID 查询重复时间
     * @param taskScheduleId 任务计划ID
     */
    fun findRepeatListByTaskScheduleId(taskScheduleId: String): List<RepeatInfo> {
        val dao = getRepeatDao()
        if (dao != null) {
            val queryBuilder = dao.queryBuilder()
            return queryBuilder
                .where(
                    queryBuilder.and(
                        RepeatInfoDao.Properties.Enable.eq(1),
                        RepeatInfoDao.Properties.TaskScheduleId.eq(taskScheduleId)
                    )
                )
                .orderDesc(RepeatInfoDao.Properties.UpdateDate)
                .list()
        }
        return emptyList()
    }

    /**
     * 通过周 查询 重复列表
     * @param weekDate 周目
     */
    fun findRepeatListByWeekDate(weekDate: Int): List<RepeatInfo> {
        val dao = getRepeatDao()
        if (dao != null) {
            val queryBuilder = dao.queryBuilder()
            return queryBuilder
                .where(
                    queryBuilder.and(
                        RepeatInfoDao.Properties.Enable.eq(1),
                        RepeatInfoDao.Properties.RepeatWeek.eq(weekDate)
                    )
                )
                .orderDesc(RepeatInfoDao.Properties.UpdateDate)
                .list()
        }
        return emptyList()
    }

    /**
     * 通过月份日  查询重复日期
     * @param monthDate 日
     */
    fun findRepeatListByMonthDate(monthDate: Int): List<RepeatInfo> {
        val dao = getRepeatDao()
        if (dao != null) {
            val queryBuilder = dao.queryBuilder()
            return queryBuilder
                .where(
                    queryBuilder.and(
                        RepeatInfoDao.Properties.Enable.eq(1),
                        RepeatInfoDao.Properties.RepeatMonth.eq(monthDate)
                    )
                )
                .orderDesc(RepeatInfoDao.Properties.UpdateDate)
                .list()
        }
        return emptyList()
    }

    /**
     * 通过月份日期 组 查询重复日期
     * @param monthDays 日期（月）
     */
    fun findRepeatListByMonthDate(monthDays: IntArray): List<RepeatInfo> {
        val dao = getRepeatDao()
        if (dao != null) {
            val queryBuilder = dao.queryBuilder()
            return queryBuilder
                .where(
                    queryBuilder.and(
                        RepeatInfoDao.Properties.Enable.eq(1),
                        RepeatInfoDao.Properties.RepeatMonth.`in`(monthDays)
                    )
                )
                .orderDesc(RepeatInfoDao.Properties.UpdateDate)
                .list()
        }
        return emptyList()
    }

    /**
     * 通过年份天数 查询 重复日期
     * @param yearDate 日期（年）
     */
    fun findRepeatListByYearDate(yearDate: Int): List<RepeatInfo> {
        val dao = getRepeatDao()
        if (dao != null) {
            val queryBuilder = dao.queryBuilder()
            return queryBuilder
                .where(
                    queryBuilder.and(
                        RepeatInfoDao.Properties.Enable.eq(1),
                        RepeatInfoDao.Properties.RepeatYear.eq(yearDate)
                    )
                )
                .orderDesc(RepeatInfoDao.Properties.UpdateDate)
                .list()
        }
        return emptyList()
    }

    /**
     * 添加重复日期
     * @param repeatInfo 重复信息
     */
    fun insertRepeatInfo(repeatInfo: RepeatInfo) {
        val dao = getRepeatDao()
        if (dao != null) {
            dao.insert(repeatInfo)
            LogController.log("INSERT", repeatInfo, "添加重复日期", RepeatInfoDao.TABLENAME)
        }
    }

    /**
     * 添加重复日期
     * @param taskScheduleId 计划ID
     * @param weekDate 重复周
     */
    fun insertRepeatInfo(taskScheduleId: String, weekDate: IntArray) {
        insertRepeatInfo(taskScheduleId, weekDate, intArrayOf(-1))
    }

    /**
     * 添加重复日期
     * @param taskScheduleId 计划ID
     * @param weekDate 重复周
     * @param monthDate 重复月
     */
    fun insertRepeatInfo(taskScheduleId: String, weekDate: IntArray, monthDate: IntArray) {
        insertRepeatInfo(taskScheduleId, weekDate, monthDate, intArrayOf(-1))
    }

    /**
     * 添加重复日期
     * @param taskScheduleId 计划ID
     * @param weekDate 重复周
     * @param monthDate 重复月
     * @param yearDate 重复年
     */
    fun insertRepeatInfo(
        taskScheduleId: String,
        weekDate: IntArray,
        monthDate: IntArray,
        yearDate: IntArray
    ) {
        banRepeatInfoByTaskScheduleId(taskScheduleId)
        for (year in yearDate) {
            for (month in monthDate) {
                for (week in weekDate) {
                    val repeatInfo = RepeatInfo()
                    repeatInfo.apply {
                        repeatId = BaseTools.getUUID()
                        setTaskScheduleId(taskScheduleId)
                        enable = 1
                        repeatDate = -1
                        repeatWeek = week
                        repeatMonth = month
                        repeatYear = year
                        val calendar = Calendar.getInstance(Locale.getDefault())
                        createDate = calendar.time
                        updateDate = calendar.time
                    }
                    insertRepeatInfo(repeatInfo)
                }
            }
        }
        LogController.log(
            "Insert",
            "$taskScheduleId - $weekDate - $monthDate - $yearDate",
            "添加重复日期",
            RepeatInfoDao.TABLENAME
        )
    }

    /**
     * 通过Id 查询
     */
    fun findRepeatInfoById(repeatInfoId: String): RepeatInfo? {
        val dao = getRepeatDao()
        return dao?.queryBuilder()?.where(RepeatInfoDao.Properties.TaskScheduleId.eq(repeatInfoId))
            ?.unique()
    }

    /**
     * 更新重复日期
     */
    fun updateRepeatInfo(repeatInfo: RepeatInfo) {
        val dao = getRepeatDao()
        if (dao != null) {
            val repeatInfoId = repeatInfo.repeatId
            val currentRepeatInfo = findRepeatInfoById(repeatInfoId)
            if (currentRepeatInfo != null) {
                dao.update(repeatInfo)
                LogController.log("Upload", repeatInfo, "更新重复日期", RepeatInfoDao.TABLENAME)
            } else {
                insertRepeatInfo(repeatInfo)
            }
        }
    }

    /**
     * 移除已有的重复日期
     */
    private fun banRepeatInfoByTaskScheduleId(taskScheduleId: String) {
        val dao = getRepeatDao()
        if (dao != null) {
            val repeatInfoList = findRepeatListByTaskScheduleId(taskScheduleId)
            val calendar = Calendar.getInstance(Locale.getDefault())
            for (repeatInfo in repeatInfoList) {
                repeatInfo.enable = 0
                repeatInfo.updateDate = calendar.time
                updateRepeatInfo(repeatInfo)
            }
            LogController.log("upload-list", taskScheduleId, "移除已有的重复日期", RepeatInfoDao.TABLENAME)
        }
    }

    /**
     * 删除重复日期
     * @param repeatInfo 重复日期
     */
    private fun deleteRepeatInfo(repeatInfo: RepeatInfo) {
        val dao = getRepeatDao()
        dao?.delete(repeatInfo)
        LogController.log("delete", repeatInfo, "删除重复日期", RepeatInfoDao.TABLENAME)
    }

    /**
     * 删除重复日期
     * @param taskScheduleId 计划ID
     */
    fun deleteRepeatInfoByTaskSchedule(taskScheduleId: String) {
        val dao = getRepeatDao()
        if (dao != null) {
            val repeatInfoList = findRepeatListByTaskScheduleId(taskScheduleId)
            for (repeatInfo in repeatInfoList) {
                dao.delete(repeatInfo)
            }
        }
    }

}