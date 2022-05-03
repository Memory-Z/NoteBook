package com.inz.z.note_book.view.activity

import android.net.Uri
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.inz.z.base.util.BaseTools
import com.inz.z.base.util.KeyBoardUtils
import com.inz.z.base.util.L
import com.inz.z.note_book.R
import com.inz.z.note_book.base.NoteStatus
import com.inz.z.note_book.base.PickImageActivityResultContracts
import com.inz.z.note_book.database.bean.NoteInfo
import com.inz.z.note_book.database.controller.NoteInfoController
import com.inz.z.note_book.databinding.NoteInfoAddLayoutBinding
import com.inz.z.note_book.util.ClickUtil
import com.inz.z.note_book.view.BaseNoteActivity
import com.inz.z.note_book.view.dialog.BaseDialogFragment
import com.inz.z.note_book.view.dialog.ChooseImageDialog
import com.inz.z.note_book.view.widget.ScheduleLayout
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * 新笔记
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2019/10/25 16:36.
 */
class NewNoteActivity : BaseNoteActivity(), View.OnClickListener {

    companion object {
        const val TAG = "NewNoteActivity"
    }

    /**
     * 修改内容框，
     */
    private var noteInfoScheduleLayout: ScheduleLayout? = null

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

    /**
     * 检测笔记线程
     */
    private var checkNoteRunnable: Runnable? = null

    private var binding: NoteInfoAddLayoutBinding? = null

    /**
     * 获取 选择 图片启动 器
     */
    private var getImageLauncher: ActivityResultLauncher<String>? = null

    /**
     * 拍照 启动器
     */
    private var takePictureLauncher: ActivityResultLauncher<Uri>? = null

    override fun initWindow() {

    }

    override fun getLayoutId(): Int {
        return R.layout.note_info_add_layout
    }


    override fun useViewBinding(): Boolean = true

    override fun setViewBinding() {
        super.setViewBinding()
        binding = NoteInfoAddLayoutBinding.inflate(layoutInflater)
            .apply {
                setContentView(root)
            }
    }

    override fun setNavigationBar() {

    }

    override fun initView() {
//        QMUIStatusBarHelper.setStatusBarLightMode(this)
//        window.statusBarColor = ContextCompat.getColor(mContext, R.color.card_second_color)

        noteInfoScheduleLayout = findViewById(R.id.note_info_add_content_schedule_layout)
        binding?.let {
            setSupportActionBar(it.noteInfoAddTopToolbar)
            it.noteInfoAddContentBrl.setOnClickListener(this)
            it.noteIabImageLl.setOnClickListener(this)
        }

        // 注册启动器
        registerLauncher()
    }

    /**
     * 注册启动器
     */
    private fun registerLauncher() {
        // 注册 请求 图片 启动器
        getImageLauncher = registerForActivityResult(PickImageActivityResultContracts()) { result ->
            L.i(TAG, "onActivityResult: uri - $result")
            // TODO: 2022/5/3 处理选中文件
        }

        // 注册 拍照 启动器
        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {

        }
    }

    /**
     * 注销启动器
     */
    private fun unregisterLauncher() {
        getImageLauncher?.unregister()
        getImageLauncher = null
        takePictureLauncher?.unregister()
        takePictureLauncher = null
    }


    override fun initData() {
        val bundle = intent.extras
        if (bundle != null) {
            noteInfoId = bundle.getString("noteInfoId", "")
        }
        if (noteInfoId.isNotEmpty()) {
            noteInfo = NoteInfoController.findById(noteInfoId)
            noteInfo?.apply {
                binding?.noteInfoAddTopToolbar?.title = noteTitle
                binding?.noteInfoAddContentScheduleLayout?.setContent(noteContent)
                binding?.noteInfoAddContentTopTimeTv?.text =
                    BaseTools.getBaseDateFormat().format(updateDate)
                oldNoteContent = noteContent
            }
        }
        if (checkNoteRunnable == null) {
            checkNoteRunnable = CheckNoteInfoRunnable(noteInfoId)
        }
        // 每 5 S 检测一次
        getScheduleThread(TAG + "_initData")
            ?.scheduleAtFixedRate(checkNoteRunnable, 5, 5, TimeUnit.SECONDS)
    }

//    override fun onWindowFocusChanged(hasFocus: Boolean) {
//        super.onWindowFocusChanged(hasFocus)
////        if (hasFocus) {
////            val nslHeight = note_info_add_content_content_nsv.height
////            val topHeight = note_info_add_content_top_time_tv.height
////            note_info_add_content_schedule_layout.minimumHeight = nslHeight - topHeight * 2
////        }
//    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_note_add, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_note_info_add_finish_item -> {
                // 点击 完成按钮
                saveNoteInfo()
            }
            android.R.id.home -> {
                // 点击 返回按钮。
                // 点击顶部返回按钮。 如果不存在修改内容，关闭界面
                if (!checkHaveChange()) {
                    this@NewNoteActivity.finish()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            if (checkHaveChange()) {
                return true
            }
        }
        return super.onKeyUp(keyCode, event)
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        L.i(TAG, "onActivityResult: -->>> $requestCode == $resultCode")
//        if (requestCode == IMAGE_REQUEST_CODE) {
//            when (resultCode) {
//                // 通过 系统相册选择
//                Activity.RESULT_OK -> {
//                    L.i(TAG, "onActivityResult: ${data?.data}")
//                    data?.let {
//                        L.i(TAG, "onActivityResult: ${data.extras}")
//                    }
//                }
//                ChooseFileConstants.CHOOSE_FILE_RESULT_CODE -> {
//                    data?.extras?.apply {
//                        val list =
//                            this.getParcelableArrayList<BaseChooseFileBean>(ChooseFileConstants.CHOOSE_FILE_RESULT_LIST_TAG)
//                        val listSize = this.getInt(
//                            ChooseFileConstants.CHOOSE_FILE_RESULT_SIZE_TAG,
//                            0
//                        )
//                        L.i(TAG, "onActivityResult: $listSize --- $list ")
//                    }
//                }
//            }
//        }
//    }

    override fun onPause() {
        super.onPause()
        // 隐藏键盘
        noteInfoScheduleLayout?.getEditTextView()?.let {
            it.clearFocus()
            KeyBoardUtils.hidKeyBoard(it)
        }

    }

    override fun onDestroyTask() {
        super.onDestroyTask()
        checkNoteRunnable?.let {
            getScheduleThread("${TAG}_onDestroy")?.shutdown()
        }
        unregisterLauncher()
        checkNoteRunnable = null
        binding = null
    }

    override fun onClick(v: View?) {
        if (ClickUtil.isFastClick(v)) {
            L.w(TAG, "onClick: this is fast click, ignore ! ")
            return
        }
        binding?.let { bindingTemp ->
            when (v?.id) {
                bindingTemp.noteInfoAddContentBrl.id -> {
                    // 笔记内容点击
                    bindingTemp.noteInfoAddContentCenterLl.let {
                        val count = it.childCount
                        val lastView = it.getChildAt(count - 1)
                        lastView.performClick()
                    }
                }
                bindingTemp.noteIabImageLl.id -> {
                    // 选择图片弹窗
                    showChooseImageDialog()
                }
                else -> {

                }
            }
        }

    }

    /**
     * 检测是否有内容更改
     */
    private fun checkHaveChange(): Boolean {
        val newContent = binding?.noteInfoAddContentScheduleLayout?.getContent()
        noteInfo?.let {
            // 判断内容是否存在修改，有修改显示提示框。
            if (newContent != it.noteContent) {
                // 显示退出提示
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
        val newContent = binding?.noteInfoAddContentScheduleLayout?.getContent()
        if (oldNoteContent != newContent) {
            if (noteInfo != null) {
                noteInfo!!.apply {
                    noteContent = newContent
                    updateDate = Date()
                    status = NoteStatus.FINISHED
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
     * 查询笔记信息线程。
     */
    private inner class CheckNoteInfoRunnable(val noteInfoId: String) : Runnable {

        override fun run() {
            val noteInfo = NoteInfoController.findById(noteInfoId)
            L.i(TAG, "run: noteInfo = $noteInfo")
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
                .setLeftButton(getString(R.string._quit)) {
                    hideExitHintDialog()
                    this@NewNoteActivity.finish()
                }
                .setRightButton(getString(R.string._save)) {
                    saveNoteInfo()
                    this@NewNoteActivity.finish()
                }
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
            chooseImageDialog = ChooseImageDialog.getInstance()
                .apply {
                    this.dialogListener = ChooseImageDialogImpl()
                }
        }
        if (!chooseImageDialog.isAdded && !chooseImageDialog.isVisible) {
            chooseImageDialog.show(manager, "ChooseImageDialog")
        }
    }

    /**
     * 选择图片弹窗 监听
     */
    private inner class ChooseImageDialogImpl : ChooseImageDialog.ChooseImageDialogListener {
        override fun chooseImageFromAlbum() {
            L.i(TAG, "chooseImageFromAlbum: ")
            getImageLauncher?.launch("image/*")
        }

        override fun takePicture() {
            L.i(TAG, "takePicture: ")
            // TODO: 2022/5/3 根据当前时间创建 相册 URI ;
//            takePictureLauncher?.launch("")
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