package com.inz.z.base.view.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.IntDef
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.inz.z.base.R
import com.inz.z.base.entity.BaseChooseFileBean
import com.inz.z.base.entity.BaseChooseFileNavBean
import com.inz.z.base.entity.ChooseFileShowType
import com.inz.z.base.entity.Constants
import com.inz.z.base.util.FileTypeHelper
import com.inz.z.base.util.FileUtils
import com.inz.z.base.util.L
import com.inz.z.base.util.ProviderUtil
import com.inz.z.base.view.AbsBaseActivity
import com.inz.z.base.view.activity.adapter.ChooseFileNavRvAdapter
import com.inz.z.base.view.activity.adapter.ChooseFileRvAdapter
import com.inz.z.base.view.dialog.PreviewImageFragmentDialog
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DefaultObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.base_activity_choose_file.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/08/20 11:31.
 */
class ChooseFileActivity : AbsBaseActivity() {

    companion object {
        const val MODE_LIST = 0x000901
        const val MODE_TABLE = 0x000902

        const val CHOOSE_FILE_RESULT_CODE = 0x010001

        const val CHOOSE_FILE_LIST_TAG = "choosedFileList"
        const val CHOOSE_FILE_SIZE_TAG = "choosedFileSize"


        private const val DEFAULT_MAX_CHOOSE_FILE_COUNT = 10
        private const val DEFAULT_TABLE_COLUMNS = 2

        const val TAG = "ChooseFileActivity"

        private const val HANDLER_ADD_CHOOSE_FILE = 0x00FF01
        private const val HANDLER_REMOVE_CHOOSE_FILE = 0x00FF02
        private const val HANDLER_REMOVE_CHOOSE_FILE_LIST = 0x00FF03
        private const val HANDLER_TAG_CHOOSE_FILE = "chooseFile"
        private const val HANDLER_TAG_CHOOSE_FILE_LIST = "chooseFileList"

        /**
         * 跳转至选择文件界面
         */
        fun gotoChooseFileActivity(activity: Activity, requestCode: Int) {
            gotoChooseFileActivity(activity, requestCode, MODE_LIST, DEFAULT_TABLE_COLUMNS)
        }

        /**
         * 跳转至选择文件界面
         */
        fun gotoChooseFileActivity(
            activity: Activity,
            requestCode: Int,
            @ShowMode showMode: Int,
            tableColumn: Int
        ) {
            gotoChooseFileActivity(
                activity,
                requestCode,
                showMode,
                Constants.FileShowType.SHOW_TYPE_DIR,
                tableColumn
            )
        }

        /**
         * 跳转至选择文件界面
         */
        fun gotoChooseFileActivity(
            activity: Activity,
            requestCode: Int,
            @ShowMode showMode: Int,
            @ChooseFileShowType showType: Int,
            tableColumn: Int
        ) {
            gotoChooseFileActivity(
                activity,
                requestCode,
                showMode,
                showType,
                tableColumn,
                DEFAULT_MAX_CHOOSE_FILE_COUNT
            )
        }

        /**
         * 跳转至选择文件界面
         */
        fun gotoChooseFileActivity(
            activity: Activity,
            requestCode: Int,
            @ShowMode showMode: Int,
            @ChooseFileShowType showType: Int,
            tableColumn: Int,
            maxChooseFileSize: Int
        ) {
            val intent = Intent(activity, ChooseFileActivity::class.java)
            val bundle = Bundle()
            bundle.putInt("showMode", showMode)
            bundle.putInt("showType", showType)
            bundle.putInt("tableColumn", tableColumn)
            bundle.putInt("maxChooseFile", maxChooseFileSize)
            intent.putExtras(bundle)
            activity.startActivityForResult(intent, requestCode)
        }
    }

    @IntDef(MODE_LIST, MODE_TABLE)
    @Retention(AnnotationRetention.SOURCE)
    annotation class ShowMode

    private val permissionArray = arrayListOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private val requestPermissionCode = 0x0002

    private var chooseFileListRvAdapter: ChooseFileRvAdapter? = null

    private var chooseFileNavRvAdapter: ChooseFileNavRvAdapter? = null

    /**
     * 根目录地址
     */
    private var mRootPath = ""

    /**
     * max file size  can choose .
     */
    private var maxChooseFileCount = DEFAULT_MAX_CHOOSE_FILE_COUNT

    @ChooseFileShowType
    private var showType = Constants.FileShowType.SHOW_TYPE_DIR

    @ShowMode
    private var showMode = MODE_LIST

    /**
     * 表单 模式下 显示列数
     */
    private var showTableModeColumn = DEFAULT_TABLE_COLUMNS

    /**
     * 选中的文件列表
     */
    private var chooseFileList: ArrayList<BaseChooseFileBean> = arrayListOf()

    private var mLayoutManager: GridLayoutManager? = null
    private var navLayoutManager: LinearLayoutManager? = null

    private var chooseFileHandler: Handler? = null

    override fun initWindow() {

    }

    override fun getLayoutId(): Int {
        return R.layout.base_activity_choose_file
    }

    override fun initView() {
//        base_choose_file_top_r_more_iv?.setOnClickListener { createMorePopupMenu() }
        setSupportActionBar(base_choose_file_top_btal.toolbar)

        navLayoutManager = LinearLayoutManager(mContext)
        navLayoutManager?.orientation = LinearLayoutManager.HORIZONTAL
        chooseFileNavRvAdapter = ChooseFileNavRvAdapter(mContext)
        chooseFileNavRvAdapter?.listener = ChooseFileNavRvAdapterListenerImpl()
        base_choose_file_nav_rv?.apply {
            this.layoutManager = navLayoutManager
            this.adapter = chooseFileNavRvAdapter
        }

        base_choose_file_bl_preview_tv?.setOnClickListener {
            if (!chooseFileList.isNullOrEmpty()) {
                val bean = chooseFileList[0]
                showPreviewDialog(bean, chooseFileList)
            } else {
                mContext?.apply {
                    showToast(mContext.getString(R.string.not_choose_file_to_preview))
                }
            }
        }

    }

    override fun initData() {
        chooseFileHandler = Handler(ChooseFileHandlerCallback())

        val bundle = intent?.extras
        bundle?.let {
            showType = it.getInt("showType", Constants.FileShowType.SHOW_TYPE_DIR)
            showMode = it.getInt("showMode", MODE_LIST)
            showTableModeColumn = it.getInt("tableColumn", DEFAULT_TABLE_COLUMNS)
            maxChooseFileCount = it.getInt("maxChooseFile", DEFAULT_MAX_CHOOSE_FILE_COUNT)
        }

        initContentRv()

        updateFileSizeOnView(0L)

        mRootPath = FileUtils.getSDPath()
        addNavBody(mRootPath, getString(R.string.root_directory))
        base_choose_file_content_rv.postDelayed(
            {
                if (checkHavePermission()) {
                    checkQueryType(mRootPath)
                }
            },
            100
        )

    }

    override fun onDestroy() {
        super.onDestroy()
        chooseFileHandler?.removeCallbacksAndMessages(null)
        chooseFileHandler = null
    }

    private fun initContentRv() {
        mLayoutManager = GridLayoutManager(mContext, 1)
        chooseFileListRvAdapter = ChooseFileRvAdapter(mContext, MODE_LIST)
        chooseFileListRvAdapter?.listener = ChooseFileRvAdapterListenerImpl()
        base_choose_file_content_rv?.apply {
            layoutManager = mLayoutManager
            adapter = chooseFileListRvAdapter
        }
        targetContentType()
    }

    /**
     * 切换中间内容显示类型
     */
    private fun targetContentType() {
        chooseFileListRvAdapter?.changeShowMode(showMode)
        mLayoutManager?.spanCount = when (showMode) {
            MODE_TABLE -> {
                showTableModeColumn
            }
            else -> {
                1
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestPermissionCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkQueryType(mRootPath)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_choose_file, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
        val menuItem = menu.findItem(R.id.menu_choose_file_mode_item)
        var modeShowName = mContext.getString(R.string.list_mode)
        if (showMode == MODE_LIST) {
            modeShowName = mContext.getString(R.string.table_mode)
        }
        menuItem.setTitle(modeShowName)
        return super.onMenuOpened(featureId, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_choose_file_mode_item -> {
                showMode = if (showMode == MODE_LIST) MODE_TABLE else MODE_LIST
                targetContentType()
            }
            android.R.id.home -> {
                chooseFileList.clear()
                closeChooseFileView()
            }
            else -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 关闭选择文件界面
     */
    private fun closeChooseFileView() {
        val intent = Intent()
        val bundle = Bundle()
        bundle.putParcelableArrayList(CHOOSE_FILE_LIST_TAG, chooseFileList)
        bundle.putInt(CHOOSE_FILE_SIZE_TAG, chooseFileList.size)
        intent.putExtras(bundle)
        setResult(CHOOSE_FILE_RESULT_CODE, intent)
        finish()
    }

    /**
     * 导航栏 适配器监听实现
     */
    private inner class ChooseFileNavRvAdapterListenerImpl :
        ChooseFileNavRvAdapter.ChooseFileNavRvAdapterListener {
        override fun onNavClick(position: Int, v: View?) {
            val bean = chooseFileNavRvAdapter?.list?.get(position)
            bean?.let {
                chooseFileNavRvAdapter?.chooseNav(position)
                checkQueryType(it.path)
            }
        }
    }

    /**
     * 选择文件监听实现
     */
    private inner class ChooseFileRvAdapterListenerImpl :
        ChooseFileRvAdapter.ChooseFileRvAdapterListener {
        override fun addChoseFile(position: Int, view: View) {
            L.i(TAG, "addChoseFile: $position")
            if (maxChooseFileCount > 0 && chooseFileList.size >= maxChooseFileCount) {
                showToast(mContext.getString(R.string.selected_file_max))
                return
            }
            val bean = chooseFileListRvAdapter?.getItemByPosition(position)
            chooseFileHandler?.let {
                val message = Message.obtain()
                val bundle = Bundle()
                bundle.putSerializable(HANDLER_TAG_CHOOSE_FILE, bean)
                message.what = HANDLER_ADD_CHOOSE_FILE
                message.data = bundle
                it.sendMessageDelayed(message, 100)
            }
        }

        override fun removeChoseFile(position: Int, view: View) {
            L.i(TAG, "removeChoseFile: $position")
            val bean = chooseFileListRvAdapter?.getItemByPosition(position)
            chooseFileHandler?.let {
                val message = Message.obtain()
                val bundle = Bundle()
                bundle.putSerializable(HANDLER_TAG_CHOOSE_FILE, bean)
                message.what = HANDLER_REMOVE_CHOOSE_FILE
                message.data = bundle
                it.sendMessageDelayed(message, 100)
            }
        }

        override fun showFullImage(position: Int, view: View) {
            L.i(TAG, "showFullImage: $position")
            val bean = chooseFileListRvAdapter?.getItemByPosition(position)
            if (bean != null) {
                showPreviewDialog(bean, null)
            }
        }

        override fun openFileDirectory(position: Int, view: View) {
            L.i(TAG, "openFileDirectory : $position")
            val bean: BaseChooseFileBean? = chooseFileListRvAdapter?.list?.get(position)
            bean?.let {
                addNavBody(it.filePath, it.fileName)
                checkQueryType(it.filePath)
            }
        }
    }

    /**
     * 添加导航体
     */
    private fun addNavBody(filePath: String, fileName: String) {
        val bean = BaseChooseFileNavBean()
        bean.title = fileName
        bean.path = filePath
        chooseFileNavRvAdapter?.addChooseFileNav(bean)
    }

    /**
     * 检测 查询类型
     */
    private fun checkQueryType(filePath: String) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            queryFileList(filePath, showType)
        }
    }

    @RequiresPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    private fun queryFileList(filePath: String?, @ChooseFileShowType showType: Int) {
        L.i(TAG, "queryFileList---------- $showType")
        Observable
            .create(
                ObservableOnSubscribe<MutableList<BaseChooseFileBean>> { emitter ->
                    var list: MutableList<BaseChooseFileBean> = mutableListOf()
                    when (showType) {
                        Constants.FileShowType.SHOW_TYPE_DIR -> {
                            list = ProviderUtil.queryFileListByDir(filePath)
                        }
                        Constants.FileShowType.SHOW_TYPE_AUDIO -> {
                            list = ProviderUtil.queryFileAudioWithContentProvider(mContext)
                        }
                        Constants.FileShowType.SHOW_TYPE_IMAGE -> {
                            list = ProviderUtil.queryFileImageWithContextProvider(mContext)
                        }
                        Constants.FileShowType.SHOW_TYPE_VIDEO -> {

                        }
                        else -> {
                            emitter.onError(IllegalArgumentException("no find $showType this's data list"))
                            return@ObservableOnSubscribe
                        }
                    }

                    list.forEach { listIt ->
                        run checkEach@{
                            chooseFileList.forEach { chooseFileListIt ->
                                if (listIt.filePath == chooseFileListIt.filePath) {
                                    listIt.checked = true
                                    return@checkEach
                                }
                            }
                        }
                    }
                    val fileTypeHelper = FileTypeHelper(mContext)
                    list.forEach { fileItem ->
                        if (!fileItem.fileIsDirectory) {
                            val file = File(fileItem.filePath)
                            val isImage = fileTypeHelper.isImageWithFile(file)
                            if (isImage) {
                                fileItem.fileType = Constants.FileType.FILE_TYPE_IMAGE
                            }
                        }
                    }
                    emitter.onNext(list)
                }
            )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(
                object : DefaultObserver<MutableList<BaseChooseFileBean>>() {
                    override fun onNext(t: MutableList<BaseChooseFileBean>) {
                        L.i(TAG, "queryFileList ${Thread.currentThread().name} -- ${t.size} -- $t")
                        chooseFileListRvAdapter?.refreshData(t)
                    }

                    override fun onError(e: Throwable) {
                        L.e(TAG, "onError: ----> ", e)
                    }

                    override fun onComplete() {
                    }
                }
            )

    }

    /**
     * 检测是否拥有权限
     */
    private fun checkHavePermission(): Boolean {
        val needRequestPermissionArray = arrayListOf<String>()
        for (p in permissionArray) {
            if (ContextCompat.checkSelfPermission(
                    mContext,
                    p
                ) == PackageManager.PERMISSION_DENIED
            ) {
                needRequestPermissionArray.add(p)
            }
        }
        return if (needRequestPermissionArray.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                needRequestPermissionArray.toTypedArray(),
                requestPermissionCode
            )
            false
        } else {
            true
        }
    }

    /**
     * 更新选中文件大小
     *
     */
    private fun updateChooseFileSize() {
        Observable
            .create(
                ObservableOnSubscribe<Long> {
                    var totalFileSize = 0L
                    L.i(TAG, "setChooseFileSize: size = ${chooseFileList.size}")
                    for (bean in chooseFileList) {
                        val file = File(bean.filePath)
                        totalFileSize += file.length()
                    }
                    it.onNext(totalFileSize)
                }
            )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(
                object : DefaultObserver<Long>() {
                    override fun onNext(t: Long) {
                        L.i(TAG, "setChooseFileSize: --- $t")
                        updateFileSizeOnView(t)
                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onComplete() {

                    }
                }
            )

    }

    /**
     * 更新选中文件大小 ；
     */
    private fun updateFileSizeOnView(fileSize: Long) {
        mContext?.apply {
            val size = fileSize / 1024.0
            val fileSizeStr = this.getString(R.string.file_size_k_format)
                .format(Locale.getDefault(), String.format("%.2f", size))
            base_choose_file_bottom_total_size_tv?.text = fileSizeStr
        }
    }

    /**
     * 更新文件预览数
     */
    private fun updateFilePreviewCountOnView(chooseFileSize: Int) {
        mContext?.apply {
            val fileSizeStr = if (chooseFileSize == 0) {
                mContext.getString(R.string._preview)
            } else {
                mContext.getString(R.string.preview_format).format(chooseFileSize)
            }
            base_choose_file_bl_preview_tv?.apply {
                this.isClickable = chooseFileSize != 0
                if (chooseFileSize == 0) {
                    setTextColor(
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                mContext,
                                R.color.text_white_50_color
                            )
                        )
                    )
                } else {
                    setTextColor(
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                mContext,
                                R.color.colorAccent
                            )
                        )
                    )
                }
                text = fileSizeStr
            }
        }
    }

    /**
     * 添加选中文件
     */
    private fun addChooseFileBean(bean: BaseChooseFileBean) {
        var addedFileBeanPosition = -1
        if (chooseFileList.size > 0) {
            for (index in 0 until chooseFileList.size) {
                val fileBean = chooseFileList[index]
                if (fileBean.filePath == bean.filePath) {
                    addedFileBeanPosition = index
                    break
                }
            }
        }
        if (addedFileBeanPosition == -1) {
            chooseFileList.add(bean)
        }

    }

    /**
     * 移除选中文件
     */
    private fun removeChooseFileBean(bean: BaseChooseFileBean) {
        var addedFileBeanPosition = -1
        if (chooseFileList.size > 0) {
            for (index in 0 until chooseFileList.size) {
                val fileBean = chooseFileList[index]
                if (fileBean.filePath == bean.filePath) {
                    addedFileBeanPosition = index
                    break
                }
            }
        }
        if (addedFileBeanPosition != -1) {
            chooseFileList.removeAt(addedFileBeanPosition)
        }
    }


    /* ----------------------- 显示图片预览 -------------------------- */

    /**
     * 显示图片预览弹窗
     */
    private fun showPreviewDialog(
        selectedImageBean: BaseChooseFileBean,
        selectedImageList: ArrayList<BaseChooseFileBean>?
    ) {
        if (mContext == null) {
            L.w(TAG, "showPreviewDialog: mContext is null. ")
            return
        }
        val manager = supportFragmentManager
        var previewDialog =
            manager.findFragmentByTag("PreviewImageDialog") as PreviewImageFragmentDialog?
        if (previewDialog == null) {
            previewDialog = PreviewImageFragmentDialog.Builder(mContext)
                .setCurrentImageBean(selectedImageBean)
                .setImageList(selectedImageList)
                .setListener(PreviewImageDialogListenerImpl())
                .build()
        }
        if (!previewDialog.isAdded || !previewDialog.isVisible) {
            previewDialog.show(manager, "PreviewImageDialog")
        }
    }

    /**
     * 预览图片监听实现
     */
    private inner class PreviewImageDialogListenerImpl :
        PreviewImageFragmentDialog.PreviewImageFragmentDialogListener {
        override fun onTargetCheck(v: View?, position: Int) {

        }

        override fun onDialogShow() {
            QMUIDisplayHelper.setFullScreen(this@ChooseFileActivity)
        }

        override fun onDialogHide() {
            QMUIDisplayHelper.cancelFullScreen(this@ChooseFileActivity)
        }

        override fun onSubmit(previewImageList: List<BaseChooseFileBean>?) {
            // 适配器中选中项 总是 >= 预览后项
            // 取消选中的数据
            val list = previewImageList?.filter {
                return@filter !it.checked
            }
            val arrayList = ArrayList<BaseChooseFileBean>()
            list?.forEach {
                arrayList.add(it)
            }

            chooseFileHandler?.let {
                val message = Message.obtain()
                val bundle = Bundle()
                bundle.putParcelableArrayList(HANDLER_TAG_CHOOSE_FILE_LIST, arrayList)
                message.what = HANDLER_REMOVE_CHOOSE_FILE_LIST
                message.data = bundle
                it.sendMessage(message)
            }
        }
    }

    /**
     * 选择文件回调
     */
    private inner class ChooseFileHandlerCallback : Handler.Callback {
        override fun handleMessage(msg: Message): Boolean {
            val bundle = msg.data
            val bean =
                bundle.getSerializable(HANDLER_TAG_CHOOSE_FILE) as BaseChooseFileBean?
            when (msg.what) {
                HANDLER_ADD_CHOOSE_FILE -> {
                    bean?.let {
                        addChooseFileBean(it)
                    }
                }
                HANDLER_REMOVE_CHOOSE_FILE -> {
                    bean?.let {
                        removeChooseFileBean(it)
                    }
                }
                HANDLER_REMOVE_CHOOSE_FILE_LIST -> {
                    val fileList: ArrayList<BaseChooseFileBean>? =
                        bundle.getParcelableArrayList(HANDLER_TAG_CHOOSE_FILE_LIST)
                    val adapterList = chooseFileListRvAdapter?.list
                    fileList?.forEach {
                        removeChooseFileBean(it)
                        adapterList?.forEachIndexed { index, baseChooseFileBean ->
                            if (baseChooseFileBean.filePath == it.filePath) {
                                chooseFileListRvAdapter?.changeItemData(it, index)
                            }
                        }
                    }
                }
                else -> {

                }
            }
            updateFilePreviewCountOnView(chooseFileList.size)
            updateChooseFileSize()
            return true
        }
    }

    /* ----------------------- 显示图片预览 -------------------------- */
    ///////////////////////////////////////////////////////////////////////////
    // OPEN
    ///////////////////////////////////////////////////////////////////////////


}