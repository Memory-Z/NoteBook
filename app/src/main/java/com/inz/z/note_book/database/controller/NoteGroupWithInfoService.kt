package com.inz.z.note_book.database.controller

import com.inz.z.note_book.database.bean.NoteGroupWithInfo
import com.inz.z.note_book.database.NoteGroupWithInfoDao
import com.inz.z.note_book.database.util.GreenDaoHelper

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2019/11/01 09:35.
 */
object NoteGroupWithInfoService {

    /**
     * 获取 NoteGroupWithInfoDao
     */
    private fun getNoteGroupWithInfoDao(): NoteGroupWithInfoDao? {
        return GreenDaoHelper.getInstance().getDaoSession()?.noteGroupWithInfoDao
    }

    fun findById(id: String): NoteGroupWithInfo? {
        return getNoteGroupWithInfoDao()?.load(id)
    }

    fun insert(noteGroupWidthInfo: NoteGroupWithInfo) {
        val dao = getNoteGroupWithInfoDao()
        dao?.insert(noteGroupWidthInfo)
        LogController.log(
            "insert",
            noteGroupWidthInfo,
            "添加笔录组与信息关联",
            dao?.tablename ?: "note_group_with_info"
        )

    }

    fun delete(groupWidthInfoId: String) {
        val dao = getNoteGroupWithInfoDao()
        dao?.deleteByKey(groupWidthInfoId)
        LogController.log(
            "delete",
            groupWidthInfoId,
            "删除关联信息",
            dao?.tablename ?: "note_group_with_info"
        )
    }

    fun update(noteGroupWithInfo: NoteGroupWithInfo) {
        val dao = getNoteGroupWithInfoDao()
        dao?.update(noteGroupWithInfo)
        LogController.log(
            "update",
            noteGroupWithInfo,
            "更新关联信息",
            dao?.tablename ?: "note_group_with_info"
        )
    }

    /**
     * 通过 noteInfo Id 查询 noteGroup
     */
    fun findNoteGroupIdByNoteId(noteInfoId: String): List<NoteGroupWithInfo> {
        val noteGroupWithInfoDao = getNoteGroupWithInfoDao()
        if (noteGroupWithInfoDao != null) {
            return noteGroupWithInfoDao.queryBuilder()
                .where(NoteGroupWithInfoDao.Properties.InfoId.eq(noteInfoId))
                .list()
        }
        return emptyList()
    }

    /**
     * 通过 groupId 查询相关组
     */
    fun findNoteInfoIdByGroupId(groupId: String): List<NoteGroupWithInfo> {
        return getNoteGroupWithInfoDao()
            ?.queryBuilder()
            ?.where(NoteGroupWithInfoDao.Properties.GroupId.eq(groupId))
            ?.list() ?: emptyList()
    }

    fun getGroupChildCountByGroupId(groupId: String): Long {
        return getNoteGroupWithInfoDao()?.queryBuilder()
            ?.where(NoteGroupWithInfoDao.Properties.GroupId.eq(groupId))
            ?.count() ?: 0L
    }

}