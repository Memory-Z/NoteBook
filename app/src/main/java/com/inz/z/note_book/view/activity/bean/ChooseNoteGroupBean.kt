package com.inz.z.note_book.view.activity.bean

import com.inz.z.note_book.database.bean.NoteGroup

/**
 * 选择文件包装 类
 *
 * ====================================================
 * Create by 11654 in 2022/6/2 17:03
 */
data class ChooseNoteGroupBean(var group: NoteGroup, var checked: Boolean = false)