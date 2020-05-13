package com.inz.z.note_book.util

import com.inz.z.note_book.BuildConfig

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/01/15 15:53.
 */
object Constants {

    /**
     * 时钟开始提醒
     */
    val CLOCK_ALARM_START_ACTION = BuildConfig.APPLICATION_ID + ".CLOCK_ALARM_START_ACTION"

    /**
     * 时钟广播
     */
    object AlarmAction {
        /**
         * 默认请求码
         */
        val REQUEST_CODE_BASE = 0x002E01

        /**
         * 运行码
         */
        val REQUEST_CODE_LAUNCHER = 0x002E02

        /**
         * 默认时钟广播
         */
        val ALARM_BROADCAST_BASE_ACTION =
            BuildConfig.APPLICATION_ID + ".ALARM_BROADCAST_BASE_ACTION"

        /**
         * 程序启动时钟广播
         */
        val ALARM_BROADCAST_LAUNCHER_ACTION =
            BuildConfig.APPLICATION_ID + ".ALARM_BROADCAST_LAUNCHER_ACTION"

        /**
         * 包名
         */
        val ALARM_BROADCAST_LAUNCHER_FLAG_PACKAGE_NAME = "laubcherPackageName"

        /**
         * 名称
         */
        val ALARM_BROADCAST_LAUNCHER_FLAG_LABEL = "launcherLabel"

        /**
         * 事件计划广播
         */
        val ALARM_BROADCAST_SCHEDULE_ACTION =
            BuildConfig.APPLICATION_ID + ".ALARM_BROADCAST_SCHEDULE_ACTION"

    }
}