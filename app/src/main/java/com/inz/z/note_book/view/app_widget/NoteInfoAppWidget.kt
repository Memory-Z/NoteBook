package com.inz.z.note_book.view.app_widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.RemoteViews
import com.inz.z.base.util.L
import com.inz.z.note_book.R
import com.inz.z.note_book.database.bean.NoteGroup
import com.inz.z.note_book.database.controller.NoteGroupService
import com.inz.z.note_book.util.BaseUtil
import com.inz.z.note_book.util.Constants
import com.inz.z.note_book.util.SPHelper
import com.inz.z.note_book.view.activity.NewNoteInfoSampleActivity
import com.inz.z.note_book.view.app_widget.bean.WidgetNoteInfo.NOTE_INFO_APP_WIDGET_CHANGE_NOTE_GROUP_ACTION
import com.inz.z.note_book.view.app_widget.service.WidgetNoteInfoListRemoteViewsService


/**
 * Implementation of App Widget functionality.
 */
class NoteInfoAppWidget : AppWidgetProvider() {
    companion object {
        const val TAG = "NoteInfoAppWidget"
    }

    private var saveAppWidgetIds: IntArray = intArrayOf()

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        L.i(TAG, "onUpdate. ")
        saveAppWidgetIds = appWidgetIds
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        L.i(TAG, "onEnabled . ")
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        L.i(TAG, "onDisabled . ")
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        L.i(TAG, "onReceive. ---> ${intent?.action}")
        super.onReceive(context, intent)
        intent?.let {
            when (it.action) {
                // ListView 中点击 项
                Constants.WidgetParams.WIDGET_NOTE_INFO_APP_WIDGET_ITEM_CLICK_ACTION -> {
                    L.i(TAG, "-item on click .ing .")
                    // 获取 微件 ID
                    val widgetId = it.getIntExtra(
                        AppWidgetManager.EXTRA_APPWIDGET_ID,
                        AppWidgetManager.INVALID_APPWIDGET_ID
                    )
                    L.i(TAG, "onReceive: intent -- $intent  ")
                    val bundle = it.extras
                    L.d(TAG, "onReceive: widgetId = $widgetId ---> bundle = $bundle ")
                    bundle?.let { bu ->
                        // 获取 笔记 ID
                        val noteInfoId =
                            bu.getString(
                                Constants.WidgetParams.WIDGET_NOTE_INFO_APP_WIDGET_ITEM_CLICK_NOTE_INFO_ID,
                                ""
                            )
                        // 获取 笔记 组ID
                        val noteGroupId = bu.getString(
                            Constants.WidgetParams.WIDGET_NOTE_INFO_APP_WIDGET_NOTE_GROUP_ID,
                            ""
                        )

                        L.i(
                            TAG,
                            "onReceive: on item click noteInfoId = $noteInfoId ; noteGroupId = $noteGroupId . "
                        )
                    }
                }
                NOTE_INFO_APP_WIDGET_CHANGE_NOTE_GROUP_ACTION -> {
                    L.i(TAG, " change note group is click !! ")
                    val manager = AppWidgetManager.getInstance(context)
                    val views = RemoteViews(context?.packageName, R.layout.note_info_app_widget)
                    manager.updateAppWidget(saveAppWidgetIds, views)
                }
                else -> {
                    L.i(TAG, " other .do ")
                }
            }
        }

    }

    /**
     * 通过 AppWidgetId 获取对应数据
     * @param appWidgetId WidgetId
     */
    private fun getNoteGroupId(appWidgetId: Int): String {
        var noteGroupId = SPHelper.getAppWidgetNoteGroupId(appWidgetId)
        if (noteGroupId.isEmpty()) {
            noteGroupId = NoteGroupService.getNearUpdateNoteGroup()?.noteGroupId ?: ""
        }
        L.i(TAG, "getNoteGroupId: ---->>>> noteGroupId = $noteGroupId ")
        if (noteGroupId.isNotEmpty()) {
            SPHelper.saveAppWidgetNoteGroupId(appWidgetId, noteGroupId)
        }
        return noteGroupId
    }

    /**
     * 获取笔记组
     */
    private fun getNoteGroup(groupId: String): NoteGroup? {
        return if (groupId.isNotEmpty()) {
            NoteGroupService.findNoteGroupById(groupId)
        } else {
            L.w(TAG, "_getNoteGroup: noteGroupId is empty ! can check this --> $this")
            null
        }
    }

    /**
     * 更新 AppWidget 内容
     */
    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        L.i(TAG, "updateAppWidget: $appWidgetId . ")
        // 笔记分组ID
        val noteGroupId = getNoteGroupId(appWidgetId)
        // 笔记组
        val noteGroup = getNoteGroup(noteGroupId)

        L.d(TAG, "updateAppWidget: noteGroupId = $noteGroupId , noteGroup = $noteGroup  ")
        val views: RemoteViews
        if (noteGroup == null) {
            views = RemoteViews(context.packageName, R.layout.note_info_app_widget_empty)
        } else {
            views = RemoteViews(context.packageName, R.layout.note_info_app_widget)

            setListViewAdapter(context, appWidgetId, views)
        }

        views.setTextViewText(
            R.id.app_widget_top_title_tv,
            noteGroup?.groupName ?: context.getString(R.string._nothing)
        )

        // 切换分组
        val changeGroupIntent = Intent(context, NewNoteInfoSampleActivity::class.java)
            .apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    .or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                val bundle = Bundle()
                bundle.putInt("launchType", 2)
                putExtras(bundle)
            }
        // +bug, 11654, 2022/4/25 , modify, PendingIntent flag Type with S+.
        val flag = BaseUtil.getPendingIntentFlag()
        val changePendingIntent = PendingIntent.getActivity(
            context,
            9,
            changeGroupIntent,
            flag
        )
        // -bug, 11654, 2022/4/25 , modify, PendingIntent flag Type with S+.
        views.setOnClickPendingIntent(R.id.app_widget_top_title_tv, changePendingIntent)

        // 添加笔记
        val addNoteIntent = Intent(context, NewNoteInfoSampleActivity::class.java)
            .apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    .or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                val bundle = Bundle()
                bundle.putInt("launchType", 1)
                bundle.putString("groupId", noteGroupId)
                putExtras(bundle)
            }
        val addNotePendingIntent =
            PendingIntent.getActivity(context, 8, addNoteIntent, flag)
        views.setOnClickPendingIntent(R.id.app_widget_top_add_iv, addNotePendingIntent)


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    /**
     * 设置 ListView Adapter
     */
    private fun setListViewAdapter(context: Context, appWidgetId: Int, views: RemoteViews) {
        // 设置 ListView  Adapter  设置传入 数据
        val noteIntentService = Intent(context, WidgetNoteInfoListRemoteViewsService::class.java)
            .apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
        views.setRemoteAdapter(R.id.app_widget_content_lv, noteIntentService)

        // 设置  list view 中 点击事件
        // 设置 intent 模板
        // <GridView/ListView/StackView> 存在很多子元素，不能通过 setOnClickPendingIntent 设置点击事件
        // 1. 通过 setPendingIntentTemplate 设置 Intent 模板
        // 2. 在 RemoteViewsFactory 的 getViewAt 中，通过 setOnClickFillInIntent 设置 item 点击事件
        val listIntent = Intent(context, NoteInfoAppWidget::class.java)
            .apply {
                action = Constants.WidgetParams.WIDGET_NOTE_INFO_APP_WIDGET_ITEM_CLICK_ACTION
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                // 接受 fillIntent 返回值
                data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
            }
        val flag = BaseUtil.getMutablePendingIntentFlag()
        val pendingIntent =
            PendingIntent.getBroadcast(context, 7, listIntent, flag)
        views.setPendingIntentTemplate(R.id.app_widget_content_lv, pendingIntent)
    }

}


