package com.inz.z.note_book.database.controller

import com.inz.z.note_book.database.TagInfoDao
import com.inz.z.note_book.database.bean.TagInfo
import com.inz.z.note_book.database.util.GreenDaoHelper

/**
 * 标签 信息 操作 。
 *
 * ====================================================
 * Create by 11654 in 2021/10/24 19:54
 */
object TagInfoController {

    /**
     * 获取  TagInfoDao
     */
    private fun getTagInfoDao(): TagInfoDao? =
        GreenDaoHelper.getInstance().getDaoSession()?.tagInfoDao

    /**
     * 添加 标签 信息
     * @param tagInfo tag
     */
    fun insertTagInfo(tagInfo: TagInfo) {
        getTagInfoDao()?.let {
            it.insert(tagInfo)
            LogController.log(LogController.TYPE_INSERT, tagInfo, "添加标签", it.tablename)
        }
    }

    /**
     * 更新 标签
     * @param tagInfo 标签 信息
     */
    fun updateTagInfo(tagInfo: TagInfo) {
        getTagInfoDao()?.let {
            val tagId = tagInfo.tagId
            val oldTagInfo = findTagByTagId(tagId)
            if (oldTagInfo == null) {
                insertTagInfo(tagInfo)
            } else {
                it.update(tagInfo)
                LogController.log(LogController.TYPE_UPDATE, tagInfo, "更新标签内容", it.tablename)
            }
        }
    }

    /**
     * 删除 TagInfo
     */
    fun deleteTagInfo(tagInfo: TagInfo) {
        getTagInfoDao()?.let {
            it.delete(tagInfo)
            LogController.log(LogController.TYPE_DELETE, tagInfo, "删除标签", it.tablename)
        }
    }

    /**
     * 通过 tagId 查询 Tag
     * @param tagId 标签 ID
     */
    fun findTagByTagId(tagId: Long): TagInfo? {
        getTagInfoDao()?.let {
            val query = it.queryBuilder()
                .where(TagInfoDao.Properties.TagId.eq(tagId))
                .build()
            return query.unique()
        }
        return null
    }

    /**
     * 通过 内容 查询 标签
     * @param content 标签内容
     */
    fun findTagByContent(content: String?): List<TagInfo>? {
        getTagInfoDao()?.let {
            val query = it.queryBuilder()
            return query
                .where(TagInfoDao.Properties.TagContent.like("%$content%"))
                .orderAsc(TagInfoDao.Properties.CreateDate)
                .build()
                .list()
        }
        return null
    }
}