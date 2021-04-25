package com.inz.z.note_book.base

import androidx.annotation.IntDef

/**
 *
 * 基本 状态 类
 * ====================================================
 * Create by 11654 in 2021/4/25 20:38
 */

/**
 * 笔记状态
 */
object NoteStatus {

    /**
     * 未完成
     */
    const val UNFINISHED = 0

    /**
     * 已完成
     */
    const val FINISHED = 1

    /**
     * 已取消
     */
    const val CANCELED = -1

    /**
     * 已超时
     */
    const val TIMEOUT = -2

    /**
     * 未知
     */
    const val UNKNOWN = -99

    @IntDef(UNFINISHED, FINISHED, CANCELED, TIMEOUT, UNKNOWN)
    @Target(AnnotationTarget.TYPE_PARAMETER, AnnotationTarget.FIELD)
    annotation class NoteInfoStatus {}
}