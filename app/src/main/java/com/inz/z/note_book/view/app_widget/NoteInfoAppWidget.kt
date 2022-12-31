package com.inz.z.note_book.view.app_widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import com.inz.z.base.util.L
import com.inz.z.note_book.R
import com.inz.z.note_book.database.bean.NoteGroup
import com.inz.z.note_book.database.controller.NoteController
import com.inz.z.note_book.database.controller.NoteGroupService
import com.inz.z.note_book.util.BaseUtil
import com.inz.z.note_book.util.Constants
import com.inz.z.note_book.util.NoteSPHelper
import com.inz.z.note_book.view.activity.ChooseAppWidgetNoteGroupActivity
import com.inz.z.note_book.view.activity.NewNoteActivity
import com.inz.z.note_book.view.activity.NoteGroupActivity
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
                            bu.getString(Constants.NoteBookParams.NOTE_ID_TAG, "")
                        // 获取 笔记 组ID
                        val noteGroupId =
                            bu.getString(Constants.NoteBookParams.NOTE_GROUP_ID_TAG, "")

                        L.i(
                            TAG,
                            "onReceive: on item click noteInfoId = $noteInfoId ; noteGroupId = $noteGroupId . "
                        )
                    }
                }
                // 切换 分组
                Constants.WidgetParams.WIDGET_NOTE_INFO_APP_WIDGET_CHANGE_NOTE_GROUP_ACTION -> {
                    L.i(TAG, "onReceive: change note group ")
                    // 获取微件 ID
                    val appWidgetId = it.getIntExtra(
                        AppWidgetManager.EXTRA_APPWIDGET_ID,
                        AppWidgetManager.INVALID_APPWIDGET_ID
                    )
                    val bundle = it.extras
                    bundle?.let { bu ->
                        val currentGroupId =
                            bu.getString(Constants.NoteBookParams.NOTE_GROUP_ID_TAG, "")
                        L.d(TAG, "onReceive: -currentGroupId = $currentGroupId")
                        if (!currentGroupId.isNullOrEmpty()) {
                            // 保存数据
                            NoteSPHelper.saveAppWidgetNoteGroupId(appWidgetId, currentGroupId)
                            // 更新界面
                            context?.let { co ->
                                val appWidgetManager = AppWidgetManager.getInstance(co)
                                updateAppWidget(co, appWidgetManager, appWidgetId)
                                appWidgetManager.notifyAppWidgetViewDataChanged(
                                    appWidgetId,
                                    R.id.app_widget_note_info_content_lv
                                )
                            }
                        }
                    }

                }
                // 更新 分组内容
                Constants.WidgetParams.WIDGET_NOTE_INFO_APP_WIDGET_UPDATE_NOTE_INFO_ACTION -> {
                    L.d(TAG, "onReceive: update widget note info . ")
                    val appWidgetId = it.getIntExtra(
                        AppWidgetManager.EXTRA_APPWIDGET_ID,
                        AppWidgetManager.INVALID_APPWIDGET_ID
                    )
                    it.extras?.let { bu ->
                        val noteGroupId =
                            bu.getString(Constants.NoteBookParams.NOTE_GROUP_ID_TAG, "")
                        if (noteGroupId.isNullOrEmpty()) return
                        context?.let { co ->
                            val appWidgetManager = AppWidgetManager.getInstance(co)
                            appWidgetManager.notifyAppWidgetViewDataChanged(
                                appWidgetId,
                                R.id.app_widget_note_info_content_lv
                            )
//                            updateAppWidget(co, appWidgetManager, appWidgetId)
                        }
                    }
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
        var noteGroupId = NoteSPHelper.getAppWidgetNoteGroupId(appWidgetId)
        if (noteGroupId.isEmpty()) {
            noteGroupId = NoteGroupService.getNearUpdateNoteGroup()?.noteGroupId ?: ""
            NoteSPHelper.saveAppWidgetNoteGroupId(appWidgetId, noteGroupId)
        }
        L.i(TAG, "getNoteGroupId: ---->>>> noteGroupId = $noteGroupId ")
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
     * 检测是否存在数据
     */
    private fun checkNoteGroupHaveNote(groupId: String): Boolean {
        return NoteController.findAllNoteInfoByGroupId(groupId).isNotEmpty()
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
        // 是否存在内容
        val haveNote = checkNoteGroupHaveNote(noteGroupId)

        L.d(TAG, "updateAppWidget: noteGroupId = $noteGroupId  ")
        val views = RemoteViews(context.packageName, R.layout.note_info_app_widget)
        if (haveNote) {
            setListViewAdapter(context, appWidgetId, views)
            views.setViewVisibility(R.id.app_widget_note_info_empty_ll, View.GONE)
            views.setViewVisibility(R.id.app_widget_note_info_content_lv, View.VISIBLE)
        } else {
            views.setViewVisibility(R.id.app_widget_note_info_empty_ll, View.VISIBLE)
            views.setViewVisibility(R.id.app_widget_note_info_content_lv, View.GONE)
        }

        views.setTextViewText(
            R.id.app_widget_top_title_tv,
            noteGroup?.groupName ?: context.getString(R.string._nothing)
        )

        // 选择笔记分组
        val changeGroupBundle = Bundle()
            .apply {
                L.d(TAG, "updateAppWidget: ----->>> noteGroupId = $noteGroupId ")
                putString(Constants.NoteBookParams.NOTE_GROUP_ID_TAG, noteGroupId)
                putInt(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
        val changeGroupIntent = Intent(context, ChooseAppWidgetNoteGroupActivity::class.java)
            .apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    .or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                putExtras(changeGroupBundle)
            }
        // +bug, 11654, 2022/4/25 , modify, PendingIntent flag Type with S+.
        val changeGroupPendingIntent = PendingIntent.getActivity(
            context,
            0,
            changeGroupIntent,
            BaseUtil.getPendingIntentFlag()
        )
        // -bug, 11654, 2022/4/25 , modify, PendingIntent flag Type with S+.
        views.setOnClickPendingIntent(R.id.app_widget_top_title_tv, changeGroupPendingIntent)

        // 添加笔记
        val addNoteIntent = Intent(context, NoteGroupActivity::class.java)
            .apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    .or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                val bundle = Bundle()
                    .apply {
                        putInt(
                            Constants.NoteBookParams.NOTE_INFO_LAUNCH_TYPE_TAG,
                            Constants.NoteBookParams.NOTE_INFO_LAUNCH_TYPE_CREATE
                        )
                        putString(Constants.NoteBookParams.NOTE_GROUP_ID_TAG, noteGroupId)
                    }
                putExtras(bundle)
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
        val addNotePendingIntent =
            PendingIntent.getActivity(
                context,
                8,
                addNoteIntent,
                BaseUtil.getPendingIntentFlag()
            )
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
        views.setRemoteAdapter(R.id.app_widget_note_info_content_lv, noteIntentService)

        // 设置  list view 中 点击事件
        // 设置 intent 模板
        // <GridView/ListView/StackView> 存在很多子元素，不能通过 setOnClickPendingIntent 设置点击事件
        // 1. 通过 setPendingIntentTemplate 设置 Intent 模板
        // 2. 在 RemoteViewsFactory 的 getViewAt 中，通过 setOnClickFillInIntent 设置 item 点击事件
        val listIntent = Intent(context, NewNoteActivity::class.java)
            .apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                action = Constants.WidgetParams.WIDGET_NOTE_INFO_APP_WIDGET_ITEM_CLICK_ACTION
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                // 接受 fillIntent 返回值
                data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
            }
        val flag = BaseUtil.getMutablePendingIntentFlag()
        val pendingIntent =
            PendingIntent.getActivity(context, 0, listIntent, flag)
        views.setPendingIntentTemplate(R.id.app_widget_note_info_content_lv, pendingIntent)
    }

}


