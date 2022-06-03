package com.inz.z.note_book.view.activity

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.inz.z.base.util.L
import com.inz.z.note_book.R
import com.inz.z.note_book.database.controller.NoteGroupService
import com.inz.z.note_book.databinding.ActivityChooseNoteGroupBinding
import com.inz.z.note_book.util.Constants
import com.inz.z.note_book.view.BaseNoteActivity
import com.inz.z.note_book.view.activity.adapter.ChooseNoteGroupAdapter
import com.inz.z.note_book.view.activity.bean.ChooseNoteGroupBean

/**
 * 选择 桌面 微件 笔记组
 *
 * ====================================================
 * Create by 11654 in 2022/6/2 16:36
 */
class ChooseAppWidgetNoteGroupActivity : BaseNoteActivity() {

    companion object {
        private const val TAG = "ChooseAppWidgetNoteGrou"
    }

    private var binding: ActivityChooseNoteGroupBinding? = null
    private var loadNoteGroupRunnable: LoadNoteGroupRunnable? = null
    private var noteGroupId = ""

    /**
     * 微件 ID
     */
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    private var chooseNoteGroupAdapter: ChooseNoteGroupAdapter? = null

    override fun initWindow() {

    }

    override fun getLayoutId(): Int = R.layout.activity_choose_note_group

    override fun useViewBinding(): Boolean = true
    override fun setViewBinding() {
        super.setViewBinding()
        binding = ActivityChooseNoteGroupBinding.inflate(layoutInflater)
            .also {
                setContentView(it.root)
            }
    }

    override fun initView() {
        binding?.let {
            setSupportActionBar(it.chooseNoteGroupTopToolbar)
            chooseNoteGroupAdapter = ChooseNoteGroupAdapter(mContext)
            it.chooseNoteGroupContentRv.also { rv ->
                rv.layoutManager = LinearLayoutManager(mContext)
                rv.adapter = chooseNoteGroupAdapter
            }
        }

    }

    override fun initData() {
        val bundle = intent.extras
        bundle?.let {
            L.d(TAG, "initData: -->> bundle = $it")
            noteGroupId = it.getString(Constants.NoteBookParams.NOTE_GROUP_ID_TAG, "")
            appWidgetId = it.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }

        loadNoteGroupRunnable = LoadNoteGroupRunnable(noteGroupId)

        getWorkThread("_init_data")?.execute(loadNoteGroupRunnable)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_finish, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // 点击 返回
            android.R.id.home -> {
                this.finish()
            }
            // 点击完成
            R.id.menu_finish_item -> {
                val noteGroupId = chooseNoteGroupAdapter?.let {
                    val position = it.getSelectedPosition()
                    val bean = it.getItemByPosition(position)
                    bean?.group?.noteGroupId ?: ""
                }
                sendUpdateWidgetBroadcast(noteGroupId)
                binding?.root?.postDelayed({
                    val intent = Intent()
                        .apply {
                            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                        }
                    setResult(RESULT_OK, intent)
                    finish()
                }, 1000L)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyTask() {
        intent.putExtras(Bundle.EMPTY)
        binding = null
        val removed = getWorkThread("_on_destroy_task")?.remove(loadNoteGroupRunnable)
        L.d(TAG, "onDestroyTask: remove load runnable = $removed")
        loadNoteGroupRunnable = null
        super.onDestroyTask()
    }


    /**
     * 切换显示内容
     * @param haveDate 是否存在数据
     */
    private fun targetContentView(haveDate: Boolean = false) {
        binding?.let {
            it.chooseNoteGroupContentNoDataView.visibility =
                if (haveDate) View.GONE else View.VISIBLE
            it.chooseNoteGroupContentRv.visibility = if (haveDate) View.VISIBLE else View.GONE
        }
    }

    /**
     * 发送广播
     * @param noteGroupId 笔记组ID
     */
    private fun sendUpdateWidgetBroadcast(noteGroupId: String?) {
        val broadcast = Intent()
            .also {
                it.action =
                    Constants.WidgetParams.WIDGET_NOTE_INFO_APP_WIDGET_CHANGE_NOTE_GROUP_ACTION
                it.`package` = packageName
                it.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                val bundle = Bundle()
                    .apply {
                        putString(Constants.NoteBookParams.NOTE_GROUP_ID_TAG, noteGroupId)
                    }
                it.putExtras(bundle)
            }
        sendBroadcast(broadcast)

    }


    /**
     * 加载笔记目录线程
     */
    private inner class LoadNoteGroupRunnable(val noteGroupId: String?) : Runnable {

        override fun run() {
            val list = NoteGroupService.findAll()
            val beanList: MutableList<ChooseNoteGroupBean> = mutableListOf()
            var haveChecked = 0
            list.forEach {
                var checked = it.noteGroupId.equals(noteGroupId)
                L.d(TAG, "run: --- nId = ${it.noteGroupId} --->> noteId = $noteGroupId")
                if (checked) {
                    if (haveChecked == 1) {
                        checked = false
                    } else {
                        haveChecked++
                    }
                }
                val bean = ChooseNoteGroupBean(it, checked)

                beanList.add(bean)
            }
            if (beanList.isNotEmpty() && haveChecked == 0) {
                beanList[0].checked = true
            }
            // 转主线程
            getUiThread("_load_note_group")?.execute {
                // 切换显示内容
                targetContentView(beanList.isNotEmpty())
                // 刷新数据
                chooseNoteGroupAdapter?.refreshData(beanList)
                binding?.chooseNoteGroupContentSrl?.isRefreshing = false
            }
        }
    }
}