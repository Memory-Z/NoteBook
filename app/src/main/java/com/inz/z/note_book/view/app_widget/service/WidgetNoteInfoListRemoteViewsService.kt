package com.inz.z.note_book.view.app_widget.service

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.widget.RemoteViewsService
import com.inz.z.base.util.L

/**
 * 小部件 笔记列表更新服务
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2019/11/11 10:20.
 */
class WidgetNoteInfoListRemoteViewsService : RemoteViewsService() {
    companion object {
        const val TAG = "WidgetNoteInfoListRVS"
    }

    init {
        L.i(TAG, "init .......... ")
    }

    override fun onCreate() {
        super.onCreate()
        L.i(TAG, "onCreate..")
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {

        val appWidgetId = intent?.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )
        L.i(TAG, "onGetViewFactory -- appWidgetId = $appWidgetId")
        return WidgetNoteInfoListRemoteViewsServiceFactory(applicationContext, appWidgetId)
    }

}