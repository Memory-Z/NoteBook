package com.inz.z.note_book.bean.inside

/**
 * 任务动作
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/05/18 15:32.
 */
enum class TaskAction(val value: String) {
    /**
     * 提示
     */
    HINT("hint"),

    /**
     * 运行
     */
    LAUNCHER("launcher"),

    /**
     * 闹钟
     */
    CLOCK("clock"),

    /**
     * 不执行
     */
    NONE("none");

    fun getTaskAction(action: String): TaskAction {
        return when (action) {
            HINT.value -> {
                HINT
            }
            LAUNCHER.value -> {
                LAUNCHER
            }
            CLOCK.value -> {
                CLOCK
            }
            else -> {
                NONE
            }
        }
    }

}