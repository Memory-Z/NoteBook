package com.inz.z.note_book.view.activity

import android.content.Intent
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.inz.z.base.entity.BaseChooseFileBean
import com.inz.z.base.entity.Constants
import com.inz.z.base.util.BaseTools
import com.inz.z.base.util.L
import com.inz.z.base.view.AbsBaseActivity
import com.inz.z.note_book.R
import com.inz.z.note_book.database.bean.NoteInfo
import com.inz.z.note_book.database.controller.NoteInfoController
import com.inz.z.note_book.view.BaseNoteActivity
import com.inz.z.note_book.view.dialog.ChooseImageDialog
import com.inz.z.note_book.view.fragment.BaseDialogFragment
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import kotlinx.android.synthetic.main.note_info_add_layout.*
import java.io.File
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * 新笔记
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2019/10/25 16:36.
 */
class NewNoteActivity : BaseNoteActivity() {

    companion object {
        const val TAG = "NewNoteActivity"

        // Reqeust code 0~65535
        private const val IMAGE_REQUEST_CODE = 0x0FE0
    }

    /**
     * 笔记ID
     */
    private var noteInfoId = ""

    /**
     * 笔记
     */
    private var noteInfo: NoteInfo? = null

    /**
     * 已保存笔记内容
     */
    private var oldNoteContent = ""
    private var executorService: ScheduledExecutorService

    init {
        executorService = Executors.newScheduledThreadPool(3)
    }

    override fun initWindow() {

    }

    override fun getLayoutId(): Int {
        return R.layout.note_info_add_layout
    }

    override fun setNavigationBar() {

    }

    override fun initView() {
        QMUIStatusBarHelper.setStatusBarLightMode(this)
        window.statusBarColor = ContextCompat.getColor(mContext, R.color.card_second_color)
        note_info_add_top_finish_tv.setOnClickListener {
            saveNoteInfo()
        }
        note_info_add_content_brl?.setOnClickListener {
            note_info_add_content_content_ll?.let {
                val count = it.childCount;
                val lastView = it.getChildAt(count - 1)
                lastView.performClick()
            }
        }
        note_info_add_top_back_iv?.setOnClickListener {
            if (!checkHaveChange()) {
                this@NewNoteActivity.finish()
            }
        }
        note_iab_image_ll?.setOnClickListener {
            showChooseImageDialog()
        }
    }

    override fun initData() {
        val bundle = intent.extras
        if (bundle != null) {
            noteInfoId = bundle.getString("noteInfoId", "")
        }
        if (noteInfoId.isNotEmpty()) {
            noteInfo = NoteInfoController.findById(noteInfoId)
            noteInfo?.apply {
                note_info_add_top_title_tv.text = noteTitle
                note_info_add_content_schedule_layout.setContent(noteContent)
                note_info_add_content_top_time_tv.text =
                    BaseTools.getBaseDateFormat().format(updateDate)
                oldNoteContent = noteContent
            }
        }
        // 每 5 S 检测一次
        executorService.scheduleAtFixedRate(checkNoteRunnable, 5, 5, TimeUnit.SECONDS)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            val nslHeight = note_info_add_content_content_nsv.height
            val topHeight = note_info_add_content_top_time_tv.height
            note_info_add_content_schedule_layout.minimumHeight = nslHeight - topHeight
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            if (checkHaveChange()) {
                return true
            }
        }
        return super.onKeyUp(keyCode, event)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE) {
            when (resultCode) {
                Constants.ChooseFileConstants.CHOOSE_FILE_RESULT_CODE -> {
                    data?.extras?.apply {
                        val list =
                            this.getParcelableArrayList<BaseChooseFileBean>(Constants.ChooseFileConstants.CHOOSE_FILE_RESULT_LIST_TAG)
                        val listSize = this.getInt(
                            Constants.ChooseFileConstants.CHOOSE_FILE_RESULT_SIZE_TAG,
                            0
                        )
                        L.i(TAG, "onActivityResult: $listSize --- $list ")
                    }
                }
            }

        }
    }

    /**
     * 检测是否有内容更改
     */
    private fun checkHaveChange(): Boolean {
        val newContent = note_info_add_content_schedule_layout.getContent()
        noteInfo?.let {
            if (!newContent.equals(noteInfo?.noteContent)) {
                showExitHintDialog()
                return true
            }
        }
        return false
    }

    /**
     * 保存笔录信息
     */
    private fun saveNoteInfo() {
        val newContent = note_info_add_content_schedule_layout.getContent()
        if (!oldNoteContent.equals(newContent)) {
            if (noteInfo != null) {
                noteInfo!!.apply {
                    noteContent = newContent
                    updateDate = Date()
                }
                NoteInfoController.updateNoteInfo(noteInfo!!)
                if (mContext != null) {
                    Toast.makeText(mContext, getString(R.string._save), Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                L.w(TAG, "note_info is null. ")
            }
        }
    }

    /**
     * 检测笔记线程
     */
    private var checkNoteRunnable = object : Runnable {
        override fun run() {
            val noteInfo = NoteInfoController.findById(noteInfoId)
//            L.i(
//                TAG,
//                "noteInfo ${System.currentTimeMillis()} -- ${noteInfo?.noteContent} + {${Thread.currentThread().name}}"
//            )
        }
    }

    /**
     * 显示退出 提示 弹窗
     */
    private fun showExitHintDialog() {
        if (mContext == null) {
            L.w(TAG, "showExitHintDialog: mContext is null .")
            return
        }
        val manager = supportFragmentManager
        var hintDialog = manager.findFragmentByTag("ExitDialogFragment") as BaseDialogFragment?
        if (hintDialog == null) {
            hintDialog = BaseDialogFragment.Builder()
                .setCenterMessage(getString(R.string.note_content_have_change_is_saveable))
                .setLeftButton(
                    getString(R.string._quit),
                    View.OnClickListener {
                        hideExitHintDialog()
                        this@NewNoteActivity.finish()
                    }
                )
                .setRightButton(
                    getString(R.string._save),
                    View.OnClickListener {
                        saveNoteInfo()
                        this@NewNoteActivity.finish()
                    }
                )
                .build()
        }
        if (!hintDialog.isAdded && !hintDialog.isVisible) {
            hintDialog.show(manager, "ExitDialogFragment")
        }
    }

    /**
     * 隐藏退出提示
     */
    private fun hideExitHintDialog() {
        if (mContext == null) {
            L.w(TAG, "showExitHintDialog: mContext is null .")
            return
        }
        val manager = supportFragmentManager
        val hintDialog = manager.findFragmentByTag("ExitDialogFragment") as BaseDialogFragment?
        hintDialog?.dismissAllowingStateLoss()
    }

    /**
     * 显示选择图片弹窗
     */
    private fun showChooseImageDialog() {
        if (mContext == null) {
            L.w(TAG, "showAddImageDialog: mContext is null.")
            return
        }
        val manager = supportFragmentManager
        var chooseImageDialog = manager.findFragmentByTag("ChooseImageDialog") as ChooseImageDialog?
        if (chooseImageDialog == null) {
            chooseImageDialog = ChooseImageDialog.getInstance(IMAGE_REQUEST_CODE)
        }
        if (!chooseImageDialog.isAdded && !chooseImageDialog.isVisible) {
            chooseImageDialog.show(manager, "ChooseImageDialog")
        }
    }

    /* ------------------------ 添加笔记土图片内容 ---------------------- */

    private class TransferImageThread : Thread() {

        override fun run() {
            super.run()
            val file = File("")
        }
    }

    private fun contentAddImageViewContent() {

    }

    /* ------------------------ 添加笔记土图片内容 ---------------------- */
}