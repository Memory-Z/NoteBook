package com.inz.z.note_book.util

import android.content.Context
import com.inz.z.base.entity.Constants
import com.inz.z.base.util.FileUtils
import com.inz.z.note_book.BuildConfig
import java.io.File

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/01/15 15:53.
 */
object Constants {

    private const val APPLICATION_ID = BuildConfig.APPLICATION_ID

    object Base {
        /**
         * 默认数据库备份地址
         */
        private const val BASE_BACKUP_DATABASE_PATH = "backup"

        /**
         * 获取备份地址
         */
        fun getBaseBackupPath(context: Context): String {
            val path =
                FileUtils.getCacheFilePath(context) + File.separator + BASE_BACKUP_DATABASE_PATH

            val file = File(path)
            if (!file.exists()) {
                file.mkdirs()
            }
            return file.absolutePath
        }
    }

    /**
     * 时钟开始提醒
     */
    const val CLOCK_ALARM_START_ACTION = "${APPLICATION_ID}.action.CLOCK_ALARM_START_ACTION"

    const val BASE_URL = "http://www.baidu.com/"

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

    /**
     * 工作： 变量
     */
    object WorkConstraint {

        const val TASK_SCHEDULED_ID = "taskScheduledId"

        /**
         * 任务计划 标签
         */
        const val TASK_SCHEDULED_TAG = "taskScheduledTag"
    }

    /**
     * Http 请求参数
     */
    object HttpRequestParams {
        const val TIME_STAMP = "timeStamp"
        const val VERSION_CODE = "versionCode"
        const val VERSION_NAME = "versionName"
    }


    /**
     * 通知服务参数
     */
    object NotificationServiceParams {
        /**
         * 屏幕点亮
         */
        const val NOTIFICATION_SCREEN_ON_ACTION = "NotificationScreenOnAction"

        /**
         * 屏幕熄灭
         */
        const val NOTIFICATION_SCREEN_OFF_ACTION = "NotificationScreenOffAction"

        /**
         * 屏幕解锁
         */
        const val NOTIFICATION_UNLOCK_ACTION = "NotificationUnlockAction"

        /**
         * 未知状态
         */
        const val NOTIFICATION_UNKNOWN = "NotificationUnknown"
    }


}