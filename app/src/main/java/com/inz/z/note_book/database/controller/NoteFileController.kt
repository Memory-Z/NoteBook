package com.inz.z.note_book.database.controller

import com.inz.z.note_book.database.NoteFileContentDao
import com.inz.z.note_book.database.bean.NoteFileContent
import com.inz.z.note_book.database.util.GreenDaoHelper

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/10/22 15:09.
 */
object NoteFileController {

    private fun getNoteFileContentDao(): NoteFileContentDao? =
        GreenDaoHelper.getInstance().getDaoSession()?.noteFileContentDao


    /**
     * 添加 笔记文件内容
     */
    fun insertNoteFileContent(noteFileContent: NoteFileContent) {
        getNoteFileContentDao()?.apply {
            insert(noteFileContent)
            LogController.log("INSERT", noteFileContent, "添加笔记文件内容 ", this.tablename)
        }
    }

    /**
     * 更新笔记文件内内容
     */
    fun updateNoteFileContent(noteFileContent: NoteFileContent) {
        getNoteFileContentDao()?.apply {
            val contentId = noteFileContent.fileId
            val oldNoteFileContent = findNoteFileContentById(contentId)
            if (oldNoteFileContent != null) {
                this.update(noteFileContent)
                LogController.log("UPDATE", noteFileContent, "更新笔记文件内容", this.tablename)
            } else {
                insertNoteFileContent(noteFileContent)
            }

        }

    }

    /**
     * 通过Id  查询
     */
    fun findNoteFileContentById(contentId: Long): NoteFileContent? {
        getNoteFileContentDao()?.apply {
            val list = this.queryBuilder()
                .where(
                    NoteFileContentDao.Properties.NoteFileId.eq(contentId)
                )
                .list()
            if (!list.isNullOrEmpty()) {
                return list.get(0)
            }
        }
        return null
    }
}