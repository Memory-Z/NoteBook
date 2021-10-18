package com.inz.z.note_book.base

import android.content.Context
import androidx.annotation.IntDef
import androidx.annotation.NonNull
import androidx.annotation.StringDef
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

/**
 * 计划类型 ： 提示，震动， 闹钟， 启动， 无
 */
object ScheduleTypeValue {
    /**
     * 无
     */
    const val NONE = 0x0020

    /**
     * 提示
     */
    const val HINT = 0x0021

    /**
     * 震动
     */
    const val SHOCK = 0x0022

    /**
     * 闹钟
     */
    const val ALARM = 0x0023

    /**
     * 启动
     */
    const val LAUNCHER = 0x0024

    /**
     * 类型 值
     */
    private val TYPE_VALUE_MAP = mutableMapOf<Int, String>()
        .apply {
            put(NONE, "none")
            put(HINT, "hint")
            put(SHOCK, "shock")
            put(ALARM, "alarm")
            put(LAUNCHER, "launcher")
        }


    @ScheduleType
    fun getScheduleTypeByContentStr(content: String): Int {
        var type = NONE
        TYPE_VALUE_MAP.forEach {
            if (it.value.equals(content, true)) {
                type = it.key
            }
        }
        return type
    }

    fun getContentStrByType(@ScheduleType type: Int): String {
        return TYPE_VALUE_MAP.getOrDefault(type, "none")
    }
}

@IntDef(ScheduleTypeValue.NONE,
    ScheduleTypeValue.HINT,
    ScheduleTypeValue.SHOCK,
    ScheduleTypeValue.ALARM,
    ScheduleTypeValue.LAUNCHER)
@Target(AnnotationTarget.FUNCTION,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.TYPE_PARAMETER)
annotation class ScheduleType

/**
 * 任务动作
 */
object TaskActionValue {
    /**
     * 计划
     */
    const val TASK_ACTION_SCHEDULE = "Schedule"

    /**
     * 无
     */
    const val TASK_ACTION_NONE = "None"

}

@StringDef(
    TaskActionValue.TASK_ACTION_SCHEDULE,
    TaskActionValue.TASK_ACTION_NONE
)
@Target(
    AnnotationTarget.TYPE_PARAMETER,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.FIELD
)
annotation class TaskAction