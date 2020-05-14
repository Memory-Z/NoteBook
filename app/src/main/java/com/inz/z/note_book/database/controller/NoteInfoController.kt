package com.inz.z.note_book.database.controller

import com.inz.z.note_book.database.bean.NoteInfo
import com.inz.z.note_book.database.NoteInfoDao
import com.inz.z.note_book.database.util.GreenDaoHelper

/**
 * NoteInfo 数据库控制
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2019/10/31 16:53.
 */
object NoteInfoController {
    /**
     * 获取 NoteInfoDao
     */
    private fun getNoteInfoDao(): NoteInfoDao? {
        return GreenDaoHelper.getInstance().getDaoSession()?.noteInfoDao
    }

    fun findById(id: String): NoteInfo? {
        return getNoteInfoDao()?.load(id)
    }

    /**
     * 查询 对应的全部 列表信息
     * @param groupId 组ID
     */
    fun findAllNoteInfoListWithLimit(limit: Int): List<NoteInfo> {
        val noteInfoDao = getNoteInfoDao()
        if (noteInfoDao != null) {
            return noteInfoDao.queryBuilder()
                .orderDesc(NoteInfoDao.Properties.UpdateDate)
                .limit(limit)
                .list()
        }
        return emptyList()
    }

    fun insert(noteInfo: NoteInfo) {
        val dao = getNoteInfoDao()
        dao?.insert(noteInfo)
        LogController.log("insert", noteInfo, "添加笔记信息", dao?.tablename ?: "note_info")
    }

    /**
     * 更新 笔记
     * @param noteInfo 笔记信息
     */
    fun updateNoteInfo(noteInfo: NoteInfo) {
        val dao = getNoteInfoDao()
        dao?.update(noteInfo)
        LogController.log("update", noteInfo, "更新笔记信息", dao?.tablename ?: "note_info")
    }

    /**
     * 删除笔记
     * @param noteInfoId 笔记ID
     */
    fun delNoteInfoById(noteInfoId: String) {
        val dao = getNoteInfoDao()
        dao?.deleteByKey(noteInfoId)
        LogController.log("delete", noteInfoId, "删除笔记信息", dao?.tablename ?: "note_info")
    }

}