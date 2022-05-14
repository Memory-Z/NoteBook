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
            LogController.log(
                LogController.TYPE_INSERT,
                noteFileContent,
                "添加笔记文件内容 ",
                this.tablename
            )
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
                LogController.log(
                    LogController.TYPE_UPDATE,
                    noteFileContent,
                    "更新笔记文件内容",
                    this.tablename
                )
            } else {
                insertNoteFileContent(noteFileContent)
            }

        }

    }

    /**
     * 删除笔记文件内容
     * @param noteFileContent 笔记文件信息
     */
    fun deleteNoteFileContent(noteFileContent: NoteFileContent) {
        getNoteFileContentDao()?.delete(noteFileContent)
        LogController.log(
            LogController.TYPE_DELETE,
            noteFileContent,
            "删除笔记文件内容",
            NoteFileContentDao.TABLENAME
        )
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
                return list[0]
            }
        }
        return null
    }

    /**
     * 根据 笔记信息ID 查询相关内容
     * @param noteInfoId 笔记信息 ID
     */
    fun findNoteFileContentByNoteId(noteInfoId: String): MutableList<NoteFileContent> {
        getNoteFileContentDao()?.apply {
            val list = queryBuilder()
                .where(NoteFileContentDao.Properties.NoteId.eq(noteInfoId))
                .orderAsc(NoteFileContentDao.Properties.Index)
                .list()
            return list
        }
        return mutableListOf()
    }

    /**
     * 通过 笔记 信息  ID 获取最后一个Index 值
     * @param noteInfoId 笔记信息 ID
     */
    fun findLastNoteFileContentIndexByNoteId(noteInfoId: String): Int {
        getNoteFileContentDao()?.apply {
            val list = this.queryBuilder()
                .where(
                    NoteFileContentDao.Properties.NoteId.eq(noteInfoId)
                )
                .orderDesc(NoteFileContentDao.Properties.Index)
                .list()
            if (!list.isNullOrEmpty()) {
                return list[0].index
            }
        }
        return -1
    }

    /**
     * 通过内容ID 查询 NoteFileContent
     * @param noteInfoId 笔记信息 ID
     * @param fileInfoId 文件信息 ID
     */
    fun findNoteFileContentByContent(noteInfoId: String?, fileInfoId: Long?): NoteFileContent? {
        getNoteFileContentDao()?.apply {
            val query = this.queryBuilder()
            val list = query
                .where(
                    query.and(
                        NoteFileContentDao.Properties.NoteId.eq(noteInfoId),
                        NoteFileContentDao.Properties.FileId.eq(fileInfoId)
                    )
                )
                .orderAsc(NoteFileContentDao.Properties.Index)
                .list()
            if (!list.isNullOrEmpty()) {
                return list[0]
            }
        }
        return null
    }
}