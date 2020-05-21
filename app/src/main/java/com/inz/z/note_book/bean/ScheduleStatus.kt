package com.inz.z.note_book.bean

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/05/18 14:12.
 */
enum class ScheduleStatus(val value: Int) {
    NOT_STARTED(0),
    DOING(1),
    FINISHED(2),
    TIMEOUT(3),
    DELETED(4),
    UNABLE(-1);

    fun getScheduleStatue(value: Int): ScheduleStatus {
        return when (value) {
            NOT_STARTED.value -> {
                NOT_STARTED
            }
            DOING.value -> {
                DOING
            }
            FINISHED.value -> {
                FINISHED
            }
            TIMEOUT.value -> {
                TIMEOUT
            }
            DELETED.value -> {
                DELETED
            }
            else -> {
                UNABLE
            }
        }
    }


}