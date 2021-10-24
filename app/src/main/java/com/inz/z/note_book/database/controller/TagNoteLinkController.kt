package com.inz.z.note_book.database.controller

import com.inz.z.note_book.database.TagNoteLinkDao
import com.inz.z.note_book.database.bean.TagNoteLink
import com.inz.z.note_book.database.util.GreenDaoHelper

/**
 * 标签 - 笔记 关联
 *
 * ====================================================
 * Create by 11654 in 2021/10/24 20:17
 */
object TagNoteLinkController {

    private fun getTagNoteLinkDao(): TagNoteLinkDao? =
        GreenDaoHelper.getInstance().getDaoSession()?.tagNoteLinkDao

    /**
     * 添加
     */
    fun insertTagNoteLink(link: TagNoteLink) {
        getTagNoteLinkDao()?.let {
            // 获取 最后一个 数据Index.
            val lastIndex = findLastTagNoteIndex(link.tagId, link.noteId)
            link.index = lastIndex + 1
            it.insert(link)
            LogController.log(LogController.TYPE_INSERT, link, "添加笔记-标签链接", it.tablename)
        }
    }

    /**
     * 通过 笔记 ID 获取 关联列表
     * @param noteId 笔记 ID
     */
    fun findTagNoteLinkListByNoteId(noteId: String): List<TagNoteLink>? {
        getTagNoteLinkDao()?.let {
            val query = it.queryBuilder()
                .where(TagNoteLinkDao.Properties.NoteId.eq(noteId))
                .orderAsc(TagNoteLinkDao.Properties.Index, TagNoteLinkDao.Properties.CreateDate)
                .build()
            return query.list()
        }
        return null
    }

    /**
     * 删除
     */
    fun deleteTagNoteLink(tagNoteLink: TagNoteLink) {
        getTagNoteLinkDao()?.let {
            it.delete(tagNoteLink)
            LogController.log(LogController.TYPE_DELETE, tagNoteLink, "删除标签-笔记关联", it.tablename)
        }
    }

    /**
     * 通过标签ID 获取 标签
     * @param tagId 标签id
     */
    fun findTagNoteInfoByTagId(tagId: Long): List<TagNoteLink>? {
        getTagNoteLinkDao()?.let {
            val query = it.queryBuilder()
                .where(TagNoteLinkDao.Properties.TagId.eq(tagId))
                .build()
            return query.list()
        }
        return null
    }

    /**
     * 获取最后一个 序列
     */
    fun findLastTagNoteIndex(tagId: Long?, noteId: String?): Int {
        getTagNoteLinkDao()?.let {
            val query = it.queryBuilder()
            val list = query
                .where(
                    query.or(
                        TagNoteLinkDao.Properties.TagId.eq(tagId),
                        TagNoteLinkDao.Properties.NoteId.eq(noteId)
                    )
                )
                .orderDesc(TagNoteLinkDao.Properties.Index)
                .limit(1)
                .build()
                .list()
            return if (list.isNullOrEmpty()) 0 else list[0].index

        }
        return 0
    }
}