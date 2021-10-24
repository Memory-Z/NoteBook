package com.inz.z.note_book.database.controller

import com.inz.z.note_book.database.TagScheduleLinkDao
import com.inz.z.note_book.database.bean.TagScheduleLink
import com.inz.z.note_book.database.util.GreenDaoHelper

/**
 * 标签 - 计划链接
 *
 * ====================================================
 * Create by 11654 in 2021/10/24 21:11
 */
object TagScheduleLinkController {

    private fun getTagScheduleLinkDao(): TagScheduleLinkDao? =
        GreenDaoHelper.getInstance().getDaoSession()?.tagScheduleLinkDao

    /**
     * 插入 标签-计划 链接
     */
    fun insertTagScheduleLink(tagScheduleLink: TagScheduleLink) {
        getTagScheduleLinkDao()?.let {
            // 获取 最后一个 index
            val lastIndex =
                findLastTagScheduleLinkIndex(tagScheduleLink.tagId, tagScheduleLink.scheduleId)
            tagScheduleLink.index = lastIndex + 1
            it.insert(tagScheduleLink)
            LogController.log(LogController.TYPE_INSERT, tagScheduleLink, "添加标签-计划链接", it.tablename)
        }
    }

    /**
     * 删除标签-计划 链接
     */
    fun deleteTagScheduleLink(tagScheduleLink: TagScheduleLink) {
        getTagScheduleLinkDao()?.let {
            it.delete(tagScheduleLink)
            LogController.log(LogController.TYPE_DELETE, tagScheduleLink, "删除标签-计划链接", it.tablename)
        }
    }

    /**
     * 获取 最后一个 Index 值
     */
    fun findLastTagScheduleLinkIndex(tagId: Long?, scheduleId: String?): Int {
        getTagScheduleLinkDao()?.let {
            val query = it.queryBuilder()
            val list = query
                .where(
                    query.or(
                        TagScheduleLinkDao.Properties.TagId.eq(tagId),
                        TagScheduleLinkDao.Properties.ScheduleId.eq(scheduleId)
                    )
                )
                .orderDesc(TagScheduleLinkDao.Properties.Index)
                .limit(1)
                .build()
                .list()
            return if (list.isNullOrEmpty()) 0 else list[0].index
        }
        return 0
    }

    /**
     * 通过 计划 id 查询 关联列表
     */
    fun findTagScheduleLinkListByScheduleId(scheduleId: String): List<TagScheduleLink>? {
        getTagScheduleLinkDao()?.let {
            return it.queryBuilder()
                .where(TagScheduleLinkDao.Properties.ScheduleId.eq(scheduleId))
                .orderAsc(
                    TagScheduleLinkDao.Properties.Index,
                    TagScheduleLinkDao.Properties.CreateDate
                )
                .build()
                .list()
        }
        return null
    }

    /**
     * 通过 标签ID 获取 关联列表
     */
    fun findTagScheduleLinkByTagId(tagId: Long): List<TagScheduleLink>? {
        getTagScheduleLinkDao()?.let {
            return it.queryBuilder()
                .where(TagScheduleLinkDao.Properties.TagId.eq(tagId))
                .orderAsc(
                    TagScheduleLinkDao.Properties.Index,
                    TagScheduleLinkDao.Properties.CreateDate
                )
                .build()
                .list()
        }
        return null
    }
}