package com.inz.z.note_book.util

import android.content.Context
import com.inz.z.base.util.SPHelper

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2019/11/08 15:03.
 */
object NoteSPHelper {
    var instance: SPHelper? = null

    private const val CREATE_DAY_IMAGE_PATH_TAG: String = "createDayImagePath"
    private const val CREATE_DAY_QR_CONTENT_TAG: String = "createDayQrContent"

    /**
     * 初始化
     */
    fun init(context: Context) {
        synchronized(SPHelper::class.java) {
            if (instance == null) {
                synchronized(this) {
                    instance = SPHelper.getInstance()
                }
            }
        }
        instance?.initSharedPreferences(context, "NoteBook")
    }

    /**
     * 保存最后一次开启时间
     */
    fun saveLastOpenTime(lastTime: Long) {
        instance?.setSharedLong("noteLastOpenTime", lastTime)
    }

    /**
     * 获取最后一次开启时间
     */
    fun getLastOpenTime(): Long {
        return instance?.getSharedLong("noteLastOpenTime") ?: 0
    }

    /**
     * 保存对应的笔记分组ID 【桌面插件】
     */
    fun saveAppWidgetNoteGroupId(appWidgetId: Int, noteGroupId: String) {
        instance?.setSharedString("appWidgetNoteGroupId_$appWidgetId", noteGroupId)
    }

    /**
     * 获取笔记分组对应的ID 【桌面插件】
     */
    fun getAppWidgetNoteGroupId(appWidgetId: Int): String {
        return instance?.getSharedString("appWidgetNoteGroupId_$appWidgetId") ?: ""
    }

    fun saveCreateDayImagePath(imagePath: String) {
        instance?.setSharedString(CREATE_DAY_IMAGE_PATH_TAG, imagePath)
    }

    fun getCreateDayImagePath(): String {
        return instance?.getSharedString(CREATE_DAY_IMAGE_PATH_TAG) ?: ""
    }

    fun saveCreateDayQRContent(qrContent: String) {
        instance?.setSharedString(CREATE_DAY_QR_CONTENT_TAG, qrContent)
    }

    fun getCreateDayQRContent(): String {
        return instance?.getSharedString(CREATE_DAY_QR_CONTENT_TAG) ?: ""
    }

}