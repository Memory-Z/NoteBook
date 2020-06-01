package com.inz.z.note_book.util

import com.inz.z.note_book.BuildConfig

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/01/15 15:53.
 */
object Constants {

    private const val APPLICATION_ID = BuildConfig.APPLICATION_ID

    /**
     * 时钟开始提醒
     */
    const val CLOCK_ALARM_START_ACTION = "${APPLICATION_ID}.action.CLOCK_ALARM_START_ACTION"

    /**
     * 时钟广播
     */
    object AlarmAction {
        /**
         * 默认请求码
         */
        const val REQUEST_CODE_BASE = 0x002E01

        /**
         * 运行码
         */
        const val REQUEST_CODE_LAUNCHER = 0x002E02

        /**
         * 默认时钟广播
         */
        const val ALARM_BROADCAST_BASE_ACTION =
            "${APPLICATION_ID}.action.ALARM_BROADCAST_BASE_ACTION"

        /**
         * 程序启动时钟广播
         */
        const val ALARM_BROADCAST_LAUNCHER_ACTION =
            "${APPLICATION_ID}.action.ALARM_BROADCAST_LAUNCHER_ACTION"

        /**
         * 包名
         */
        const val ALARM_BROADCAST_LAUNCHER_FLAG_PACKAGE_NAME = "laubcherPackageName"

        /**
         * 名称
         */
        const val ALARM_BROADCAST_LAUNCHER_FLAG_LABEL = "launcherLabel"

        /**
         * 事件计划广播
         */
        const val ALARM_BROADCAST_SCHEDULE_ACTION =
            "${APPLICATION_ID}.action.ALARM_BROADCAST_SCHEDULE_ACTION"

        /**
         * 提示
         */
        const val ALARM_BROADCAST_HINT_ACTION =
            "${APPLICATION_ID}.action.ALARM_BROADCAST_HINT_ACTION"

        /**
         * 提示消息内容
         */
        const val ALARM_BROADCAST_HINT_FLAG_MESSAGE = "hintMessage"

        /**
         * 闹钟
         */
        const val ALARM_BROADCAST_CLOCK_ACTION =
            "${APPLICATION_ID}.action.ALARM_BROADCAST_CLOCK_ACTION"
    }

    /**
     * application 请求码
     */
    const val APPLICATION_LIST_REQUEST_CODE_FLAG = "0x000A01"
    const val APPLICATION_LIST_REQUEST_CODE = 0x000A01
    const val APPLICATION_LIST_RESPONSE_CODE = 0x000A02

    /**
     * 自定义重复时间
     */
    const val CUSTOM_DATE_REQUEST_CODE_FLAG = "0x000A02"
    const val CUSTOM_DATE_REQUEST_CODE = 0x000A02

    /**
     * 计划类型
     */
    const val SCHEDULE_TYPE_REQUEST_CODE_FLAG = "0x000A03"
    const val SCHEDULE_TYPE_REQUEST_CODE = 0x000A03

    /**
     * 生命 周期广播
     */
    object LifeAction {
        const val LIFE_CHANGE_ACTION = "${APPLICATION_ID}.action.LIFE_CHANGE_ACTION"
    }
}