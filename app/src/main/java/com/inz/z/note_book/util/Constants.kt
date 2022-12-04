package com.inz.z.note_book.util

import android.content.Context
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
         * 笔记文件 地址
         */
        const val BASE_NOTE_FILE_PATH = "note_file"

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


        /**
         * 创建 LovePanel work
         */
        const val CREATE_LOVE_PANEL_WORK_NAME = "create_love_panel_work"
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

        /**
         * 创建 LovePanel 时钟 广播
         */
        const val ALARM_BROADCAST_CLOCK_CREATE_LOVE_PANEL_ACTION =
            "${APPLICATION_ID}.action.ALARM_BROADCAST_CLOCK_CREATE_LOVE_PANEL_ACTION"
    }

    /**
     * application 请求码
     */
    const val APPLICATION_LIST_REQUEST_CODE_FLAG = "0x000A01"
    const val APPLICATION_LIST_REQUEST_CODE = 0x000A01
    const val APPLICATION_LIST_RESPONSE_CODE = 0x000A02

    /**
     * 笔记信息 参数
     */
    object NoteBookParams {
        /**
         * 笔记分组ID
         */
        const val NOTE_GROUP_ID_TAG = "groupId"

        /**
         * 笔记 ID
         */
        const val NOTE_ID_TAG = "noteInfoId"

        /**
         * 笔记信息界面启动界面
         */
        const val NOTE_INFO_LAUNCH_TYPE_TAG = "launchType"
        const val NOTE_INFP_LAUNCH_TYPE_NORMAL = 0
        const val NOTE_INFO_LAUNCH_TYPE_OPEN = 0x0001
        const val NOTE_INFO_LAUNCH_TYPE_CREATE = 0x0002
    }

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

        /**
         * 创建 LovePanel 广播
         */
        const val NOTIFICATION_CREATE_LOVE_PANEL_ACTION = "NotificationCreatedLovePanelAction"

        const val NOTIFICATION_TAG_TITLE = "title"
        const val NOTIFICATION_TAG_FILE_PATH = "filePath"
    }

    /**
     * 基础广播参数
     */
    object BaseBroadcastParams {
        /**
         * 应用启动
         */
        const val APPLICATION_CREATE_ACTION = "${APPLICATION_ID}.action.APPLICATION_CREATE_ACTION"

        /**
         * 应用暂停
         */
        const val APPLICATION_PAUSE_ACTION = "${APPLICATION_ID}.action.APPLICATION_PAUSE_ACTION"

        /**
         * 备份 服务启动
         */
        const val BACKUP_SERVICE_CREATE_ACTION =
            "${APPLICATION_ID}.action.BACKUP_SERVICE_CREATE_ACTION"

        /**
         * 备份服务销毁
         */
        const val BACKUP_SERVICE_DESTROY_ACTION =
            "${APPLICATION_ID}.action.BACKUP_SERVICE_DESTROY_ACTION"

        /**
         * 生成 LovePanel 失败
         */
        const val CREATE_LOVE_PANEL_FAILURE_ACTION =
            "${APPLICATION_ID}.action.CREATE_LOVE_PANEL_FAILURE_ACTION"

        const val CREATE_LOVE_PANEL_FAILURE_MESSAGE_TAG = "messageTag"
    }

    /**
     * 任务参数
     */
    object TaskParams {
        /**
         * 自定义重复时间
         */
        const val CUSTOM_DATE_REQUEST_CODE = 0x000A02

        /**
         * 请求重复类型
         */
        const val REQUEST_REPEAT_TYPE_CODE = 0x000A03
        const val RESULT_REPEAT_TYPE_CODE = 0x000A04

        /**
         * 请求 标签
         */
        const val REQUEST_TAG_CODE = 0x000A05
        const val RESULT_TAG_CODE = 0x000A06

        /**
         * 重复类型
         */
        const val PARAMS_REPEAT_TYPE = "RepeatType"

        /**
         * 重复日期
         */
        const val PARAMS_REPEAT_DATE = "RepeatDate"

        /**
         * 计划类型
         */
        const val PARAMS_SCHEDULE_TYPE = "ScheduleType"

    }

    /**
     * 碎片 参数
     */
    object FragmentParams {
        /**
         * 内容 类型
         */
        const val PARAMS_CONTENT_TYPE = "Content"

        /**
         * 请求 添加 内容 Code
         */
        const val REQUEST_ADD_CONTENT_CODE = 0x0B01
        const val RESULT_ADD_CONTENT_CODE = 0x0B02

        /**
         * 标签 数据
         */
        const val PARAMS_TAG_ARRAYS = "TagArrays"

        /**
         * 标签 关联 ID
         */
        const val PARAMS_TAG_LINK_ID = "TagLinkId"

        /**
         * 碎片 前缀
         */
        const val FRAGMENT_TAG_PREFIX = "FragmentTag_"

        /**
         * 每日 事件弹窗
         */
        const val EVENT_DAY_DIALOG_TAG = "${FRAGMENT_TAG_PREFIX}EVENT_DAY_DIALOG_TAG"
        const val EVENT_DAY_CALENDAR_TAG = "EventDayCalendarTag"

    }

    /**
     * 壁纸 参数
     */
    object WallpaperParams {
        /**
         * 壁纸 Rect
         */
        const val PARAMS_TAG_WALLPAPER_RECT_ARRAY = "WallpaperRectArray"

        /**
         * 壁纸 文件地址
         */
        const val PARAMS_TAG_WALLPAPER_FILE_PATH = "WallpaperFilePath"

        /**
         * 壁纸ID
         */
        const val PARAMS_TAG_WALLPAPER_ID = "WallpaperId"

        /**
         * 选择图片 请求Code
         */
        const val CHOOSE_IMAGE_REQUEST_CODE = 0x00A0

        /**
         * 请求设置壁纸权限
         */
        const val SET_WALLPAPER_REQUEST_CODE = 0x00A1

        /**
         * 读取 文件 权限
         */
        const val READ_FILE_REQUEST_CODE = 0x00A2

    }

    /**
     * 桌面小插件 参数
     */
    object WidgetParams {

        /**
         * noteInfo 单项点击事件 ..
         */
        const val WIDGET_NOTE_INFO_APP_WIDGET_ITEM_CLICK_ACTION =
            "${APPLICATION_ID}.action.NOTE_INFO_ITEM_CLICK"

        /**
         * 切换显示的笔记分组
         */
        const val WIDGET_NOTE_INFO_APP_WIDGET_CHANGE_NOTE_GROUP_ACTION =
            "${APPLICATION_ID}.action.NOTE_INFO_CHANGE_NOTE_GROUP"

        /**
         * 更新 笔记信息
         */
        const val WIDGET_NOTE_INFO_APP_WIDGET_UPDATE_NOTE_INFO_ACTION =
            "${APPLICATION_ID}.action.NOTE_INFO_UPDATE_NOTE_INFO"

        const val WIDGET_NOTE_INFO_APP_WIDGET_NOTE_GROUP_ID =
            "widget_note_info_click_note_group_id"

        const val WIDGET_NOTE_INFO_APP_WIDGET_ITEM_CLICK_NOTE_INFO_ID =
            "widget_note_info_click_note_info_id"

        const val WIDGET_NOTE_INFO_CLICK_POSITION = "widget_note_info_click_position"

    }


}