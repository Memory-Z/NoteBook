package com.inz.z.note_book.database.controller

import com.inz.z.note_book.database.bean.TagInfo

/**
 * 标签 链接 操作
 *
 * ====================================================
 * Create by 11654 in 2021/10/24 20:33
 */
object TagLinkController {

    /**
     * 通过 笔记 id 查找TagInfoList
     */
    fun findTagInfoListByNoteId(noteId: String): List<TagInfo>? {
        val tagNoteLinkList = TagNoteLinkController.findTagNoteLinkListByNoteId(noteId)
        // 不为 null 空
        if (!tagNoteLinkList.isNullOrEmpty()) {
            val list = mutableListOf<TagInfo>()
            // 遍历 链接
            tagNoteLinkList.forEach { link ->
                val tagInfo = TagInfoController.findTagByTagId(link.tagId)
                tagInfo?.let {
                    list.add(it)
                }
            }
            return list
        }
        return null
    }

    /**
     * 通过 计划 ID 查询 关联  的 标签 列表
     */
    fun findTagInfoListByScheduleId(scheduleId: String): List<TagInfo>? {
        val tagScheduleLinkList =
            TagScheduleLinkController.findTagScheduleLinkListByScheduleId(scheduleId)
        // 如果不为空，遍历查询
        if (!tagScheduleLinkList.isNullOrEmpty()) {
            val list = mutableListOf<TagInfo>()
            tagScheduleLinkList.forEach { link ->
                val tagInfo = TagInfoController.findTagByTagId(link.tagId)
                tagInfo?.let {
                    list.add(it)
                }
            }
            return list
        }
        return null

    }

}