package com.inz.z.note_book.database.controller

import com.inz.z.base.util.BaseTools
import com.inz.z.note_book.database.DesktopWallpaperInfoDao
import com.inz.z.note_book.database.bean.DesktopWallpaperInfo
import com.inz.z.note_book.database.util.GreenDaoHelper
import java.util.*

/**
 * 桌面 壁纸设置 控制
 *
 * ====================================================
 * Create by 11654 in 2021/11/9 21:07
 */
object DesktopWallpaperController {

    private fun getWallpaperDao(): DesktopWallpaperInfoDao? =
        GreenDaoHelper.getInstance().getDaoSession()?.desktopWallpaperInfoDao

    /**
     * 添加 桌面壁纸信息
     */
    fun insertWallpaperInfo(info: DesktopWallpaperInfo) {
        getWallpaperDao()?.let {
            it.insert(info)
            LogController.log(
                LogController.TYPE_INSERT,
                info,
                "添加桌面壁纸信息",
                it.tablename
            )
        }
    }

    /**
     * 更新桌面壁纸信息
     */
    fun updateWallpaperInfo(info: DesktopWallpaperInfo) {
        getWallpaperDao()?.let {
            val id = info.wallpaperId
            val wallpaperInfo = findWallpaperInfoById(id)
            if (wallpaperInfo != null) {
                it.update(info)
                LogController.log(
                    LogController.TYPE_UPDATE,
                    info,
                    "更新桌面壁纸信息",
                    it.tablename
                )
            } else {
                insertWallpaperInfo(info)
            }
        }
    }

    /**
     * 通过 壁纸 ID 查询 壁纸信息
     */
    fun findWallpaperInfoById(id: Long): DesktopWallpaperInfo? {
        getWallpaperDao()?.let {
            return it.queryBuilder()
                .where(DesktopWallpaperInfoDao.Properties.WallpaperId.eq(id))
                .unique()
        }
        return null
    }

    /**
     * 通过 时间 查询 相关壁纸
     */
    fun findWallpaperInfoByTime(date: Date): List<DesktopWallpaperInfo>? {
        getWallpaperDao()?.let {
            val startDate = BaseTools.getLocalDate()
            val endDate = BaseTools.getLocalDate()

            val query = it.queryBuilder()
            return query
                .where(
                    DesktopWallpaperInfoDao.Properties.StartTime.between(startDate, endDate)
                )
                .orderAsc(DesktopWallpaperInfoDao.Properties.StartTime)
                .build()
                .list()
        }
        return null
    }

    /**
     * 获取 全部 壁纸 信息
     */
    fun findAllWallpaper(): List<DesktopWallpaperInfo>? {
        getWallpaperDao()?.let {
            return it.queryBuilder()
                .orderAsc(DesktopWallpaperInfoDao.Properties.StartTime)
                .build()
                .list()
        }
        return null
    }
}