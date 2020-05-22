package com.inz.z.note_book.bean

import android.content.Context
import com.inz.z.note_book.R

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/05/22 08:59.
 */
enum class NoteInfoStatus(val value: Int) {

    /**
     * 未完成
     */
    UNFINISHED(0),

    /**
     * 已完成
     */
    FINISHED(1),

    /**
     * 已取消
     */
    CANCELED(-1),

    /**
     * 已超时
     */
    TIMEOUT(-2),

    /**
     * 未知
     */
    UNKNOWN(-99);

    /**
     * 获取状态值
     */
    fun getStatusStr(context: Context): String {
        return when (this) {
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
            else -> {
                context.getString(R.string._nothing)
            }
        }
    }

    fun getStatus(value: Int): NoteInfoStatus {
        return when (value) {
            UNFINISHED.value -> {
                UNFINISHED
            }
            FINISHED.value -> {
                FINISHED
            }
            CANCELED.value -> {
                CANCELED
            }
            TIMEOUT.value -> {
                TIMEOUT
            }
            else -> {
                UNKNOWN
            }
        }
    }
}