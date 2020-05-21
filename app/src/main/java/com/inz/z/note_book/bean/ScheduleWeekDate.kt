package com.inz.z.note_book.bean

import android.content.Context
import com.inz.z.note_book.R

/**
 * 计划周
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/05/18 13:46.
 */
enum class ScheduleWeekDate(val value: Int) {
    /**
     * 星期一
     */
    MONDAY(1),

    /**
     * 星期贰
     */
    TUESDAY(2),

    /**
     * 星期三
     */
    WEDNESDAY(3),

    /**
     * 星期四
     */
    THURSDAY(4),

    /**
     * 星期五
     */
    FRIDAY(5),

    /**
     * 星期六
     */
    SATURDAY(6),

    /**
     * 星期日
     */
    SUNDAY(7),

    /**
     * 永不
     */
    NEVER(0);


    /**
     * 获取资源文字
     */
    fun getString(context: Context): String {
        return when (this.value) {
            2 -> {
                context.getString(R.string._date_week_2)
            }
            3 -> {
                context.getString(R.string._date_week_3)
            }
            4 -> {
                context.getString(R.string._date_week_4)
            }
            5 -> {
                context.getString(R.string._date_week_5)
            }
            6 -> {
                context.getString(R.string._date_week_6)
            }
            7 -> {
                context.getString(R.string._date_week_7)
            }
            else -> {
                context.getString(R.string._date_week_1)
            }
        }
    }

    fun getScheduleWeekDate(value: Int): ScheduleWeekDate {
        return when (value) {
            MONDAY.value -> {
                MONDAY
            }
            TUESDAY.value -> {
                TUESDAY
            }
            WEDNESDAY.value -> {
                WEDNESDAY
            }
            THURSDAY.value -> {
                THURSDAY
            }
            FRIDAY.value -> {
                FRIDAY
            }
            SATURDAY.value -> {
                SATURDAY
            }
            SUNDAY.value -> {
                SUNDAY
            }
            else -> {
                NEVER
            }
        }
    }

}