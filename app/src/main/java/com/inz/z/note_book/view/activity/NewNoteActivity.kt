package com.inz.z.note_book.view.activity

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.WorkerThread
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.inz.z.base.util.BaseTools
import com.inz.z.base.util.KeyBoardUtils
import com.inz.z.base.util.L
import com.inz.z.base.util.ProviderUtil
import com.inz.z.note_book.R
import com.inz.z.note_book.base.NoteStatus
import com.inz.z.note_book.base.PickImageActivityResultContracts
import com.inz.z.note_book.common.HorSpanItemDecoration
import com.inz.z.note_book.database.bean.FileInfo
import com.inz.z.note_book.database.bean.NoteFileContent
import com.inz.z.note_book.database.bean.NoteInfo
import com.inz.z.note_book.database.controller.FileInfoController
import com.inz.z.note_book.database.controller.NoteFileController
import com.inz.z.note_book.database.controller.NoteInfoController
import com.inz.z.note_book.databinding.NoteInfoAddLayoutBinding
import com.inz.z.note_book.util.ClickUtil
import com.inz.z.note_book.util.FileUtil
import com.inz.z.note_book.view.BaseNoteActivity
import com.inz.z.note_book.view.activity.adapter.NewNoteInfoImageRvAdapter
import com.inz.z.note_book.view.dialog.BaseDialogFragment
import com.inz.z.note_book.view.dialog.ChooseImageDialog
import com.inz.z.note_book.view.widget.ScheduleLayout
import java.io.File
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.Future
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

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
     * 笔记-文件 信息
     */
    private var noteFileContentList: MutableList<NoteFileContent>? = null

    /**
     * 已保存笔记内容
     */
    private var oldNoteContent = ""

    /**
     * 检测笔记线程
     */
    private var checkNoteRunnable: Runnable? = null
    private var checkNoteFuture: ScheduledFuture<*>? = null
    private var addImageFuture: Future<*>? = null
    private var findNoteFileFuture: Future<*>? = null

    /**
     * 拍照图片链接
     */
    private val takePictureUri: AtomicReference<Uri> = AtomicReference()


    private var binding: NoteInfoAddLayoutBinding? = null


    private var newNoteInfoImageRvAdapter: NewNoteInfoImageRvAdapter? = null
    private var newNoteInfoImageLayoutManager: LinearLayoutManager? = null

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

        newNoteInfoImageLayoutManager = LinearLayoutManager(mContext)
            .apply {
                this.orientation = LinearLayoutManager.HORIZONTAL
                this.offsetChildrenHorizontal(resources.getDimensionPixelOffset(R.dimen.margin_layout))
            }
        newNoteInfoImageRvAdapter = NewNoteInfoImageRvAdapter(mContext)
            .apply {
                listener = NewNoteInfoImageRvAdapterListenerImpl()
            }

        binding?.let {
            setSupportActionBar(it.noteInfoAddTopToolbar)
            it.noteInfoAddContentBrl.setOnClickListener(this)
            it.noteIabImageLl.setOnClickListener(this)
            it.noteInfoAddContentImageCloseIv.setOnClickListener(this)
            it.noteInfoAddContentImageRv.let { rv ->
                rv.layoutManager = newNoteInfoImageLayoutManager
                rv.adapter = newNoteInfoImageRvAdapter
                rv.addItemDecoration(HorSpanItemDecoration(mContext))
            }
        }
        // 切换显示状态
        targetImageLayout(false, expand = true)
        // 注册启动器
        registerLauncher()
    }


    /**
     * 注册启动器
     */
    private fun registerLauncher() {
        // 注册 请求 图片 启动器
        getImageLauncher = registerForActivityResult(PickImageActivityResultContracts()) { result ->
            L.i(TAG, "registerLauncher: onActivityResult: uri - $result")
            addImageFuture = result?.let {
                getWorkThread("_pick_image")?.submit(SaveNoteFileRunnable(it))
            }
        }

        // 注册 拍照 启动器
        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {
            val uri = takePictureUri.get()
            L.i(
                TAG,
                "registerLauncher: onActivityResult: is TakePicture success? $it >>> uri: $uri"
            )
            if (it && uri != null) {
                addImageFuture = getWorkThread("_take_picture")?.submit(SaveNoteFileRunnable(uri))
                takePictureUri.set(null)
            }
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

            // 查询 笔记文件信息
            findNoteFileFuture =
                getWorkThread("_find_note_file")?.submit(FindNoteFileContentRunnable(noteInfoId))
        }

        L.i(TAG, "initData: noteFileContentList = [$noteFileContentList]")
        if (checkNoteRunnable == null) {
            checkNoteRunnable = CheckNoteInfoRunnable(noteInfoId)
        }
        // 每 5 S 检测一次
        checkNoteFuture = getScheduleThread(TAG + "_initData")
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
        L.i(TAG, "onDestroyTask: =================>>>> ")
        checkNoteFuture?.cancel(true)
        checkNoteFuture = null
        checkNoteRunnable = null
        removeNoteFileFuture?.cancel(true)
        removeNoteFileFuture = null
        addImageFuture?.cancel(true)
        addImageFuture = null
        findNoteFileFuture?.cancel(true)
        findNoteFileFuture = null
        unregisterLauncher()
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
                    bindingTemp.noteInfoAddContentCenterRl.let {
                        val count = it.childCount
                        val lastView = it.getChildAt(count - 1)
                        lastView.performClick()
                    }
                }
                bindingTemp.noteIabImageLl.id -> {
                    // 选择图片弹窗
                    showChooseImageDialog()
                }
                bindingTemp.noteInfoAddContentImageCloseIv.id -> {
                    // 切换显示图片
                    targetImageLayoutVisibility(true)
                }
                else -> {

                }
            }
        }

    }

    /**
     * 切换显示状态
     * @param needChangeExpandStatus 是否需要切换显示状态, 默认不切换，仅点击时切换
     */
    private fun targetImageLayoutVisibility(needChangeExpandStatus: Boolean = false) {
        // 切换显示图片
        val haveImage = (newNoteInfoImageRvAdapter?.itemCount ?: 0) > 0
        val currentStatus =
            binding?.noteInfoAddContentImageContentRl?.visibility == View.VISIBLE
        val expand = if (needChangeExpandStatus) !currentStatus else currentStatus
        targetImageLayout(haveImage, expand)
    }

    /**
     * 切换显示图片栏状态
     * @param haveImage 是否有相关图片
     * @param expand 是否展开
     */
    private fun targetImageLayout(haveImage: Boolean, expand: Boolean) {
        binding?.let {
            it.noteInfoAddContentImageRl.visibility = if (haveImage) View.VISIBLE else View.GONE
            if (haveImage) {
                val closeDrawable: Drawable?
                val imageLayoutHeight: Int
                if (expand) {
                    it.noteInfoAddContentImageContentRl.visibility = View.VISIBLE
                    closeDrawable =
                        ContextCompat.getDrawable(mContext, R.drawable.ic_expand_more_black_24dp)
                    imageLayoutHeight =
                        resources.getDimensionPixelOffset(R.dimen.new_note_image_layout_height_expand_less)
                } else {
                    it.noteInfoAddContentImageContentRl.visibility = View.GONE
                    closeDrawable =
                        ContextCompat.getDrawable(mContext, R.drawable.ic_expand_less_black_24dp)
                    imageLayoutHeight =
                        resources.getDimensionPixelOffset(R.dimen.new_note_image_layout_height_expand_more)
                }
                it.noteInfoAddContentImageCloseIv.setImageDrawable(closeDrawable)
                val lp = it.noteInfoAddContentImageRl.layoutParams
                lp.height = imageLayoutHeight
                it.noteInfoAddContentImageRl.layoutParams = lp
            }
        }
    }

    /**
     * 查询 笔记 文件 信息 线程
     */
    private inner class FindNoteFileContentRunnable(val noteInfoId: String) : Runnable {

        override fun run() {
            val noteFileList = NoteFileController.findNoteFileContentByNoteId(noteInfoId)
            L.i(TAG, "FindNoteFileContentRunnable: run: noteFileList = $noteFileList")
            noteFileContentList = noteFileList
            val fileInfoList = mutableListOf<FileInfo>()
            noteFileList.forEach {
                fileInfoList.add(it.fileInfo)
            }
            if (fileInfoList.isNotEmpty()) {
                getUiThread("_load_note_file")?.execute {
                    newNoteInfoImageRvAdapter?.refreshData(fileInfoList)
                    // 切换图片显示状态
                    targetImageLayoutVisibility()
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
        // 获取笔记内容。
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

        var editText: WeakReference<EditText>? = null
        var changed = false

        init {
            binding?.let {
                editText = WeakReference(
                    it.noteInfoAddContentScheduleLayout.getEditTextView()
                )
            }
        }

        override fun run() {
            val noteInfo = NoteInfoController.findById(noteInfoId)
            if (noteInfo == null) {
                checkNoteRunnable = null
                L.w(TAG, "run: note info is null. ")
                return
            }
            val content = editText?.get()?.text.toString()
            // 内容是否有修改。
            val haveChange = noteInfo.noteContent != content
            if (changed != haveChange) {
                L.d(TAG, "run: haveChanged: form [$changed] --> to [$haveChange]")
                getUiThread("_check_noteInfo")?.execute {
                    val title = noteInfo.noteTitle
                    val t = if (haveChange) "${title}*" else title
                    supportActionBar?.title = t
                }
            }
            changed = haveChange
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
            if (mContext == null) {
                L.e(TAG, "takePicture: mContent is null ")
                return
            }
            val fileParentPath = FileUtil.getNoteFilePath(mContext)
            val fileName = FileUtil.createFileNameWithDate("image", ".jpg")
            val filePath = fileParentPath + File.separatorChar + fileName
            L.i(TAG, "takePicture: ")
            val uri = ProviderUtil.getUriFromFile(mContext, File(filePath), mContext.packageName)
            L.i(TAG, "takePicture: filePath: $filePath  -> uri: $uri ")
            takePictureUri.set(uri)
            takePictureLauncher?.launch(uri)
        }
    }

    /* ------------------------ 添加笔记图片内容 ---------------------- */

    /**
     * 移除笔记 文件 Future
     */
    private var removeNoteFileFuture: Future<*>? = null

    /**
     * 图片监听
     */
    private inner class NewNoteInfoImageRvAdapterListenerImpl :
        NewNoteInfoImageRvAdapter.NewNoteInfoImageRvAdapterListener {
        override fun clickClose(v: View?, position: Int) {
            L.i(TAG, "clickClose: --- > remove . $position ")
            removeNoteFileFuture = getWorkThread("_click_close")?.submit {
                val fileInfo = newNoteInfoImageRvAdapter?.getItemByPosition(position)
                L.i(TAG, "clickClose: fileInfo = $fileInfo ")
                fileInfo?.let {
                    removeNoteFileContent(it)
                }
                getUiThread("_click_close")?.execute {
                    newNoteInfoImageRvAdapter?.removeItemData(position)
                    // 切换显示状态
                    targetImageLayoutVisibility()
                }
            }
        }
    }

    /**
     * 保存笔记文件线程
     */
    private inner class SaveNoteFileRunnable(val uri: Uri) : Runnable {

        override fun run() {
            L.i(TAG, "SaveNoteFileRunnable run: ---->>> uri - [$uri]")
            val fileInfo = findFileInfo(uri)
            L.i(TAG, "SaveNoteFileRunnable run: fileInfo: $fileInfo")
            val noteFileContent = addNoteFileContent(fileInfo)
            L.i(TAG, "SaveNoteFileRunnable run: noteFileContent: $noteFileContent")
            noteFileContent?.let {
                noteFileContentList?.add(it)
            }
            getUiThread("_save_note_file_content")?.execute {
                L.i(TAG, "SaveNoteFileRunnable run: addItemData")
                newNoteInfoImageRvAdapter?.addItemData(fileInfo)
                // 切换显示状态
                targetImageLayoutVisibility()
            }
        }
    }

    /**
     * 查询文件信息
     * @param uri 文件链接
     */
    private fun findFileInfo(uri: Uri): FileInfo {
        L.i(TAG, "findFileInfo: uri - [$uri]")
        val path = ProviderUtil.queryFilePathByUri(mContext, uri)
        L.i(TAG, "findFileInfo: uri = $uri --> path = $path")
        var fileInfo = FileInfoController.findFileInfoByUri(uri)
        if (fileInfo == null) {
            val date = BaseTools.getLocalDate()
            // 添加新文件信息
            fileInfo = FileInfo()
                .apply {
                    this.filePath = path
                    this.uri = uri.toString()
                    path?.let {
                        val file = File(path)
                        if (file.exists()) {
                            this.fileName = file.name
                        }
                    }
                    this.fileType = "image/*"
                    this.createDate = date
                    this.updateDate = date
                }
            L.i(TAG, "findFileInfo: [INSERT NEW] FileInfo = $fileInfo")
            FileInfoController.insertFileInfo(fileInfo)
        }
        return fileInfo
    }

    /**
     * 添加 笔记 文件 信息
     * @param fileInfo 文件 信息
     * @return noteFileContent?
     *
     */
    @WorkerThread
    private fun addNoteFileContent(fileInfo: FileInfo): NoteFileContent? {
        if (noteInfo == null) {
            L.i(TAG, "addNoteFileContent: note info is null. ")
            return null
        }
        var noteFileContent =
            NoteFileController.findNoteFileContentByContent(noteInfo?.noteInfoId, fileInfo.fileId)
        // 未查询到相关 笔记 文件  信息
        if (noteFileContent == null) {
            val date = BaseTools.getLocalDate()
            noteFileContent = NoteFileContent()
                .apply {
                    this.noteId = noteInfoId
                    this.fileInfo = fileInfo
                    this.fileId = fileInfo.fileId
                    this.index =
                        NoteFileController.findLastNoteFileContentIndexByNoteId(noteInfo!!.noteInfoId) + 1
                    this.createDate = date
                    this.updateDate = date
                }
            NoteFileController.insertNoteFileContent(noteFileContent)
            L.i(TAG, "addNoteFileContent: [INSERT NEW] noteFileContent: $noteFileContent")
        }
        return noteFileContent
    }

    /**
     * 移除笔记文件 内容
     */
    @WorkerThread
    private fun removeNoteFileContent(fileInfo: FileInfo) {
        if (noteInfo == null) {
            L.w(TAG, "removeNoteFileContent: note info is null. ")
            return
        }
        val noteFileContent =
            NoteFileController.findNoteFileContentByContent(noteInfoId, fileInfo.fileId)
        L.i(
            TAG,
            "removeNoteFileContent: noteFileContent = $noteFileContent , noteInfoId = $noteInfoId , fileId = ${fileInfo.fileId} ."
        )
        noteFileContent?.let {
            NoteFileController.deleteNoteFileContent(it)
            L.i(TAG, "removeNoteFileContent: remove note file content .")
        }
    }

    /* ------------------------ 添加笔记图片内容 ---------------------- */
}