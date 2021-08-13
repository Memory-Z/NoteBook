package com.inz.z.note_book.base

import android.content.Context
import androidx.annotation.IntDef
import androidx.annotation.NonNull
import com.inz.z.note_book.R

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
    @Target(AnnotationTarget.TYPE_PARAMETER, AnnotationTarget.FIELD,
        AnnotationTarget.PROPERTY,
        AnnotationTarget.VALUE_PARAMETER)
    annotation class NoteInfoStatus {}

    /**
     * 获取状态描述.
     * @param status 状态 {@link NoteInfoStatus}
     */
    fun getStatusStr(@NoteInfoStatus status: Int, @NonNull context: Context): String {
        return when (status) {
            UNFINISHED -> {
                context.getString(R.string._unfinished)
            }
            FINISHED -> {
                context.getString(R.string._finished)
            }
            CANCELED -> {
                context.getString(R.string._canceled)
            }
            TIMEOUT -> {
                context.getString(R.string._timed_out)
            }
            UNKNOWN -> {
                context.getString(R.string._unknown)
            }
            else -> {
                context.getString(R.string._unknown)
            }
        }

    }
}