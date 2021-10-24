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
 * 笔记 状态
 */
@IntDef(NoteStatus.UNFINISHED,
    NoteStatus.FINISHED,
    NoteStatus.CANCELED,
    NoteStatus.TIMEOUT,
    NoteStatus.UNKNOWN)
@Target(AnnotationTarget.TYPE_PARAMETER, AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.VALUE_PARAMETER)
annotation class NoteInfoStatus

/**
 * 任务 值
 * @see com.inz.z.note_book.database.bean.TaskInfo
 * @see com.inz.z.note_book.database.bean.TaskSchedule
 * @see com.inz.z.note_book.database.bean.RepeatInfo
 */
object TaskValue {

    ///////////////////////////////////////////////////////////////////////////
    // 计划类型
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 无
     */
    const val SCHEDULE_NONE = 0x0020

    /**
     * 提示
     */
    const val SCHEDULE_HINT = 0x0021

    /**
     * 震动
     */
    const val SCHEDULE_SHOCK = 0x0022

    /**
     * 闹钟
     */
    const val SCHEDULE_ALARM = 0x0023

    /**
     * 启动
     */
    const val SCHEDULE_LAUNCHER = 0x0024

    /**
     * 类型 值
     */
    private val TYPE_VALUE_MAP = mutableMapOf<Int, String>()
        .apply {
            put(SCHEDULE_NONE, "none")
            put(SCHEDULE_HINT, "hint")
            put(SCHEDULE_SHOCK, "shock")
            put(SCHEDULE_ALARM, "alarm")
            put(SCHEDULE_LAUNCHER, "launcher")
        }

    /**
     * 初始化计划类型
     */
    fun initScheduleTypeValue(context: Context) {
        TYPE_VALUE_MAP.let {
            it.clear()
            it.put(SCHEDULE_NONE, context.getString(R.string._nothing))
            it.put(SCHEDULE_HINT, context.getString(R.string._tips))
            it.put(SCHEDULE_SHOCK, context.getString(R.string._shock))
            it.put(SCHEDULE_ALARM, context.getString(R.string._clock))
            it.put(SCHEDULE_LAUNCHER, context.getString(R.string._launcher))
        }
    }

    @ScheduleType
    fun getScheduleTypeByContentStr(content: String): Int {
        var type = SCHEDULE_NONE
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

    ///////////////////////////////////////////////////////////////////////////
    // 任务动作
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 计划
     */
    const val TASK_ACTION_SCHEDULE = "Schedule"

    /**
     * 启动
     */
    const val TASK_ACTION_LAUNCHER = "Launcher"

    /**
     * 待办事项
     */
    const val TASK_ACTION_TODO = "TODO"

    /**
     * 无
     */
    const val TASK_ACTION_NONE = "None"

    /**
     * 不重复
     */
    const val TASK_REPEAT_TYPE_NONE = 0x0010

    ///////////////////////////////////////////////////////////////////////////
    // 重复类型
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 日
     */
    const val TASK_REPEAT_TYPE_DATE = 0x0011

    /**
     * 周
     */
    const val TASK_REPEAT_TYPE_WEEK = 0x0012

    /**
     * 月
     */
    const val TASK_REPEAT_TYPE_MONTH = 0x0013

    /**
     * 农历-日
     */
    const val TASK_REPEAT_TYPE_LUNAR_DATE = 0x0014

    /**
     * 农历-月
     */
    const val TASK_REPEAT_TYPE_LUNAR_MONTH = 0x0015

    /**
     * 年
     */
    const val TASK_REPEAT_TYPE_YEAR = 0x0016

    /**
     * 自定义
     */
    const val TASK_REPEAT_TYPE_CUSTOM = 0x0017
}

/**
 * 计划类型
 */
@IntDef(TaskValue.SCHEDULE_NONE,
    TaskValue.SCHEDULE_HINT,
    TaskValue.SCHEDULE_SHOCK,
    TaskValue.SCHEDULE_ALARM,
    TaskValue.SCHEDULE_LAUNCHER)
@Target(AnnotationTarget.FUNCTION,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.TYPE_PARAMETER)
annotation class ScheduleType

/**
 * 任务动作
 */
@StringDef(
    TaskValue.TASK_ACTION_SCHEDULE,
    TaskValue.TASK_ACTION_LAUNCHER,
    TaskValue.TASK_ACTION_TODO,
    TaskValue.TASK_ACTION_NONE
)
@Target(
    AnnotationTarget.TYPE_PARAMETER,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.FIELD
)
annotation class TaskAction


/**
 * 重复类型
 */
@IntDef(
    TaskValue.TASK_REPEAT_TYPE_NONE,
    TaskValue.TASK_REPEAT_TYPE_DATE,
    TaskValue.TASK_REPEAT_TYPE_WEEK,
    TaskValue.TASK_REPEAT_TYPE_MONTH,
    TaskValue.TASK_REPEAT_TYPE_LUNAR_DATE,
    TaskValue.TASK_REPEAT_TYPE_LUNAR_MONTH,
    TaskValue.TASK_REPEAT_TYPE_YEAR,
    TaskValue.TASK_REPEAT_TYPE_CUSTOM
)
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.TYPE_PARAMETER,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FUNCTION
)
annotation class RepeatType

/**
 * 碎片 界面 内容类型
 */
object FragmentContentTypeValue {
    /**
     * 笔记 标签
     */
    const val FRAGMENT_CONTENT_TYPE_NOTE_TAG = 0x0A01

    /**
     * 计划 标签
     */
    const val FRAGMENT_CONTENT_TYPE_SCHEDULE_TAG = 0x0A02

    /**
     * 记录 标签
     */
    const val FRAGMENT_CONTENT_TYPE_RECORD_TAG = 0x0A03

    /**
     * 动态 标签
     */
    const val FRAGMENT_CONTENT_TYPE_DYNAMIC_TAG = 0x0A04

    /**
     * 文件 标签
     */
    const val FRAGMENT_CONTENT_TYPE_FILE_TAG = 0x0A05

    /**
     * 其他
     */
    const val FRAGMENT_CONTENT_TYPE_OTHER = 0x0A06
}

/**
 * 碎片 类型
 */
@IntDef(
    FragmentContentTypeValue.FRAGMENT_CONTENT_TYPE_NOTE_TAG,
    FragmentContentTypeValue.FRAGMENT_CONTENT_TYPE_SCHEDULE_TAG,
    FragmentContentTypeValue.FRAGMENT_CONTENT_TYPE_RECORD_TAG,
    FragmentContentTypeValue.FRAGMENT_CONTENT_TYPE_DYNAMIC_TAG,
    FragmentContentTypeValue.FRAGMENT_CONTENT_TYPE_FILE_TAG,
    FragmentContentTypeValue.FRAGMENT_CONTENT_TYPE_OTHER
)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.TYPE_PARAMETER,
    AnnotationTarget.FIELD
)
annotation class FragmentContentType

/**
 * 标签 消息
 */
object TagValue {

}
