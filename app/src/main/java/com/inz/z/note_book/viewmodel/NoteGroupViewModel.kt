package com.inz.z.note_book.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.inz.z.note_book.database.bean.NoteGroup
import com.inz.z.note_book.database.controller.NoteController
import com.inz.z.note_book.database.controller.NoteGroupService

/**
 * 笔记 分组 ViewModel
 *
 * ====================================================
 * Create by 11654 in 2021/12/13 22:59
 */
class NoteGroupViewModel : ViewModel() {

    private var noteGroupLiveData: MutableLiveData<NoteGroup?>? = null

    /**
     * 获取 笔记分组
     */
    fun getNoteGroup(): MutableLiveData<NoteGroup?>? {
        if (noteGroupLiveData == null) {
            noteGroupLiveData = MutableLiveData()
        }
        return noteGroupLiveData
    }

    /**
     * 通过笔记分组 ID 查询
     */
    fun findNoteGroupById(noteGroupId: String?) {
        if (noteGroupId.isNullOrEmpty()) {
            noteGroupLiveData?.postValue(null)
        } else {
            val noteGroup = NoteGroupService.findNoteGroupById(noteGroupId)
            noteGroupLiveData?.postValue(noteGroup)
        }
    }
}