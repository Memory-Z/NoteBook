package com.inz.z.note_book.view.app_widget.service

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.inz.z.base.util.L
import com.inz.z.note_book.R
import com.inz.z.note_book.database.bean.NoteInfo
import com.inz.z.note_book.database.controller.NoteController
import com.inz.z.note_book.util.Constants
import com.inz.z.note_book.util.SPHelper
import com.inz.z.note_book.util.ViewUtil

/**
 *
 * 微件 --笔记 信息 列表 Factory
 * ====================================================
 * Create by 11654 in 2022/5/28 22:46
 */
class WidgetNoteInfoListRemoteViewsServiceFactory(
    val mContext: Context,
    val appWidgetId: Int?,
) : RemoteViewsService.RemoteViewsFactory {

    companion object {
        private const val TAG = "WidgetNoteInfoRemoteFactory"
    }

    private var noteInfoList: MutableList<NoteInfo>? = mutableListOf()
    private var noteGroupId = ""

    override fun onCreate() {
        appWidgetId?.let {
            noteGroupId = SPHelper.getAppWidgetNoteGroupId(it)
        }
        L.i(TAG, "onCreate. appWidgetId = $appWidgetId ,  noteGroupId = $noteGroupId --< ")
    }

    override fun getLoadingView(): RemoteViews {
        L.i(TAG, "getLoadingView. ")
        return ViewUtil.createLoadingRemoteView(mContext)
    }

    override fun getItemId(position: Int): Long {
        L.i(TAG, "getItemId: $position")
        return noteInfoList?.get(position).hashCode().toLong()
    }

    override fun onDataSetChanged() {
        L.i(TAG, "onDataSetChanged >>>>>>>>>>>>>> .")
        updateNoteInfoData()
    }

    override fun hasStableIds(): Boolean {
        L.i(TAG, "hasStableIds .")
        return false
    }

    override fun getViewAt(position: Int): RemoteViews {
        val noteInfo = noteInfoList?.get(position)
        val remoteViews = RemoteViews(mContext.packageName, R.layout.app_widget_item_note_info)

        remoteViews.setTextViewText(R.id.app_widget_item_note_title_tv, noteInfo?.noteTitle)

        //设置 Intent
        val clickIntent = Intent()
            .apply {
                val bundle = Bundle()
                    .also {
                        it.putInt(Constants.WidgetParams.WIDGET_NOTE_INFO_CLICK_POSITION, position)
                        it.putString(
                            Constants.WidgetParams.WIDGET_NOTE_INFO_APP_WIDGET_ITEM_CLICK_NOTE_INFO_ID,
                            noteInfo?.noteInfoId
                        )
                        it.putString(
                            Constants.WidgetParams.WIDGET_NOTE_INFO_APP_WIDGET_NOTE_GROUP_ID,
                            noteGroupId
                        )
                    }
                putExtras(bundle)
            }
        remoteViews.setOnClickFillInIntent(R.id.app_widget_item_note_content_ll, clickIntent)

        return remoteViews
    }

    override fun getCount(): Int {
        L.i(TAG, "getCount . ${noteInfoList?.size} ")
        return noteInfoList?.size ?: 0
    }

    override fun getViewTypeCount(): Int {
        L.i(TAG, "getViewTypeCount, ")
        return 1
    }

    override fun onDestroy() {
        L.i(TAG, "onDestroy. ")
    }

    /**
     * 更新
     */
    private fun updateNoteInfoData() {
        L.i(TAG, "updateNoteInfoData: noteGroupId = $noteGroupId")
        if (appWidgetId == null || noteGroupId.isEmpty()) {
            L.w(TAG, "updateNoteInfoData: noteGroupId is null . ---> $this")
            return
        }
        val list =
            NoteController.findAllNoteInfoByGroupId(noteGroupId)
        if (list.isNotEmpty()) {
            noteInfoList = list as MutableList<NoteInfo>
            L.d(TAG, "updateNoteInfoData: --->> List[Size] = ${noteInfoList?.size} ")
            AppWidgetManager.getInstance(mContext)
                .notifyAppWidgetViewDataChanged(appWidgetId, R.id.item_note_sample_ll)
        }
    }
}