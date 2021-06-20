package com.inz.z.base.view.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.IntDef
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.inz.z.base.view.activity.viewmodel.ChooseFileViewModel
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
import kotlin.math.min

/**
 * 图片文件选择 Activity .
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/08/20 11:31.
 */
class ChooseFileActivity : AbsBaseActivity() {

    companion object {
        private const val TAG = "ChooseFileActivity"

        /**
         * 显示列表布局
         */
        const val MODE_LIST = 0x000901

        /**
         * 显示表格布局
         */
        const val MODE_TABLE = 0x000902

        private const val DEFAULT_MAX_CHOOSE_FILE_COUNT = 10
        private const val DEFAULT_TABLE_COLUMNS = 4

        /**
         * 默认每次加载数量.
         */
        private const val DEFAULT_PER_LOAD_SIZE = 10

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
                Constants.ChooseFileConstants.SHOW_TYPE_DIR,
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

    /**
     * 权限列表。
     */
    private val permissionArray = arrayListOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private val requestPermissionCode = 0x0002

    /**
     * 文件列表适配器
     */
    private var chooseFileListRvAdapter: ChooseFileRvAdapter? = null

    /**
     * 文件选择顶部导航栏适配器
     */
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
    private var showType = Constants.ChooseFileConstants.SHOW_TYPE_DIR

    /**
     * 显示模式，默认：列表
     */
    @ShowMode
    private var showMode = MODE_LIST

    /**
     * 表单 模式下 显示列数
     */
    private var showTableModeColumn = DEFAULT_TABLE_COLUMNS

    /**
     * 当前选中路径文件列表
     */
    private var currentPathFileList: MutableList<BaseChooseFileBean> = mutableListOf()

    /**
     * 当前显示文件位置.
     */
    private var currentFileIndex = 0

    private var loadSizePer = DEFAULT_PER_LOAD_SIZE

    /**
     * 选中的文件列表
     */
    private var chooseFileList: ArrayList<BaseChooseFileBean> = arrayListOf()

    private var mLayoutManager: GridLayoutManager? = null
    private var navLayoutManager: LinearLayoutManager? = null

    private var chooseFileHandler: Handler? = null

    /**
     * 按钮可点击 颜色
     */
    private var buttonClickScl: ColorStateList? = null

    /**
     * 按钮默认颜色
     */
    private var buttonDefaultScl: ColorStateList? = null

    /**
     * ChooseFileViewModel.
     */
    private var chooseFileViewModel: ChooseFileViewModel? = null


    override fun initWindow() {

    }

    override fun resetBottomNavigationBar(): Boolean {
        return false
    }

    override fun getLayoutId(): Int {
        return R.layout.base_activity_choose_file
    }

    override fun initView() {

        initColorStateList()
//        base_choose_file_top_r_more_iv?.setOnClickListener { createMorePopupMenu() }
        setSupportActionBar(base_choose_file_top_btal.toolbar)

        navLayoutManager = LinearLayoutManager(mContext)
        navLayoutManager?.orientation = LinearLayoutManager.HORIZONTAL
        // 顶部导航栏。
        chooseFileNavRvAdapter = ChooseFileNavRvAdapter(mContext)
        chooseFileNavRvAdapter?.listener = ChooseFileNavRvAdapterListenerImpl()
        base_choose_file_nav_rv?.apply {
            this.layoutManager = navLayoutManager
            this.adapter = chooseFileNavRvAdapter
        }

        base_choose_file_bl_preview_tv?.setOnClickListener {
            if (!chooseFileList.isNullOrEmpty()) {
                val bean = chooseFileList[0]
                showPreviewDialog(bean, chooseFileList, false)
            } else {
                mContext?.apply {
                    showToast(mContext.getString(R.string.not_choose_file_to_preview))
                }
            }
        }

        base_choose_file_top_submit_tv?.setOnClickListener {
            closeChooseFileView()
        }
    }

    override fun initData() {
        chooseFileHandler = Handler(Looper.getMainLooper(), ChooseFileHandlerCallback())

        val bundle = intent?.extras
        bundle?.let {
            showType = it.getInt("showType", Constants.ChooseFileConstants.SHOW_TYPE_DIR)
            showMode = it.getInt("showMode", MODE_LIST)
            showTableModeColumn = it.getInt("tableColumn", DEFAULT_TABLE_COLUMNS)
            maxChooseFileCount = it.getInt("maxChooseFile", DEFAULT_MAX_CHOOSE_FILE_COUNT)
            loadSizePer = it.getInt("loadSizePer", DEFAULT_PER_LOAD_SIZE)
        }

        initContentRv()

        updateFileSizeOnView(0L)

        mRootPath = FileUtils.getSDPath()
        addNavBody(mRootPath, getString(R.string.root_directory))
        // 延迟查询文件。
        base_choose_file_content_rv.postDelayed(
            {
                if (checkHavePermission()) {
                    checkQueryType(mRootPath)
                }
            },
            100
        )
        // 默认为 选择模式。
        chooseFileListRvAdapter?.targetShowSelectView(true)

    }

    override fun onDestroy() {
        super.onDestroy()
        chooseFileHandler?.removeCallbacksAndMessages(null)
        chooseFileHandler = null
        chooseFileViewModel = null
        buttonClickScl = null
        buttonDefaultScl = null
        chooseFileListRvAdapter = null
        chooseFileNavRvAdapter = null
        chooseFileList.clear()
    }

    /**
     * 初始化颜色
     */
    private fun initColorStateList() {
        buttonClickScl = mContext.getColorStateList(R.color.colorAccent)
        buttonDefaultScl = mContext.getColorStateList(R.color.text_black_50_color)
    }

    /**
     * 初始化 ChooseFileViewModel.
     */
    private fun initViewModel() {
        chooseFileViewModel =
            ViewModelProvider.NewInstanceFactory().create(ChooseFileViewModel::class.java)
        chooseFileViewModel?.let {
            it.getFileList()
                .observe(this, androidx.lifecycle.Observer {
                    L.i(TAG, "initViewModel: -->> fileList ${it} ")
                })
        }
    }

    /**
     * 初始化内容
     */
    private fun initContentRv() {
        mLayoutManager = GridLayoutManager(mContext, 1)
        // 内容列表。
        chooseFileListRvAdapter = ChooseFileRvAdapter(mContext, showMode)
        chooseFileListRvAdapter?.listener = ChooseFileRvAdapterListenerImpl()
        base_choose_file_content_rv?.apply {
            layoutManager = mLayoutManager
            adapter = chooseFileListRvAdapter
            this.addOnScrollListener(
                object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        mLayoutManager?.let {
                            val lastPosition = it.findLastCompletelyVisibleItemPosition()
                            if (lastPosition >= it.itemCount - it.spanCount) {
                                showCurrentFileList(false)
                            }
                        }
                    }
                }
            )
        }
        val isDirContent = showType == Constants.ChooseFileConstants.SHOW_TYPE_DIR
        base_choose_file_nav_rv?.visibility = if (isDirContent) {
            View.VISIBLE
        } else {
            View.GONE
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
                DEFAULT_TABLE_COLUMNS
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
            // 权限获取成功，查询列表。
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
                // 返回
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
        bundle.putParcelableArrayList(
            Constants.ChooseFileConstants.CHOOSE_FILE_RESULT_LIST_TAG,
            chooseFileList
        )
        bundle.putInt(
            Constants.ChooseFileConstants.CHOOSE_FILE_RESULT_SIZE_TAG,
            chooseFileList.size
        )
        intent.putExtras(bundle)
        setResult(Constants.ChooseFileConstants.CHOOSE_FILE_RESULT_CODE, intent)
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
            L.i(TAG, "addChoseFile: $position --- ${chooseFileList.size}")
            if (maxChooseFileCount > 0 && chooseFileList.size >= maxChooseFileCount) {
                showToast(mContext.getString(R.string.selected_file_max))
                return
            }
//            val bean = chooseFileListRvAdapter?.getItemByPosition(position)
//            chooseFileHandler?.let {
//                val message = Message.obtain()
//                val bundle = Bundle()
//                bundle.putSerializable(HANDLER_TAG_CHOOSE_FILE, bean)
//                message.what = HANDLER_ADD_CHOOSE_FILE
//                message.data = bundle
//                it.sendMessageDelayed(message, 100)
//            }
        }

        override fun removeChoseFile(position: Int, view: View) {
            L.i(TAG, "removeChoseFile: $position")
//            val bean = chooseFileListRvAdapter?.getItemByPosition(position)
//            chooseFileHandler?.let {
//                val message = Message.obtain()
//                val bundle = Bundle()
//                bundle.putSerializable(HANDLER_TAG_CHOOSE_FILE, bean)
//                message.what = HANDLER_REMOVE_CHOOSE_FILE
//                message.data = bundle
//                it.sendMessageDelayed(message, 100)
//            }
        }

        override fun showFullImage(position: Int, view: View) {
            L.i(TAG, "showFullImage: $position")
            val bean = chooseFileListRvAdapter?.getItemByPosition(position)
            if (bean != null) {
                showPreviewDialog(bean, null, true)
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

        override fun chooseFileList(list: List<BaseChooseFileBean>, fileSize: Long) {
            L.i(TAG, "chooseFileList: --> ${list.size} >> $fileSize")
            chooseFileList = list as ArrayList<BaseChooseFileBean>
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

    /**
     * 查询文件列表。
     */
    @RequiresPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    private fun queryFileList(filePath: String?, @ChooseFileShowType showType: Int) {
        L.i(TAG, "queryFileList---------- $showType")
        Observable
            .create(
                ObservableOnSubscribe<MutableList<BaseChooseFileBean>> { emitter ->
                    var list: MutableList<BaseChooseFileBean> = mutableListOf()
                    when (showType) {
                        Constants.ChooseFileConstants.SHOW_TYPE_DIR -> {
                            list = ProviderUtil.queryFileListByDir(filePath)
                            // only type dir to set fileType.
                            val fileTypeHelper = FileTypeHelper(mContext)
                            list.forEach { fileItem ->
                                if (!fileItem.fileIsDirectory) {
                                    val file = File(fileItem.filePath)
                                    // TODO: 2021/4/25 TOO Many Open Files .
                                    val isImage = fileTypeHelper.isImageWithFile(file)
                                    if (isImage) {
                                        fileItem.fileType = Constants.FileType.FILE_TYPE_IMAGE
                                    }
                                }
                            }
                        }
                        Constants.ChooseFileConstants.SHOW_TYPE_AUDIO -> {
                            list = ProviderUtil.queryFileAudioWithContentProvider(mContext)
                        }
                        Constants.ChooseFileConstants.SHOW_TYPE_IMAGE -> {
                            list = ProviderUtil.queryFileImageWithContextProvider(mContext)
                        }
                        Constants.ChooseFileConstants.SHOW_TYPE_VIDEO -> {

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

                    emitter.onNext(list)
                }
            )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(
                object : DefaultObserver<MutableList<BaseChooseFileBean>>() {
                    override fun onNext(t: MutableList<BaseChooseFileBean>) {
                        L.i(TAG, "queryFileList ${Thread.currentThread().name} -- ${t.size} -- $t")
                        // 重置显示文件序号，
                        currentFileIndex = 0
                        currentPathFileList.clear()
                        currentPathFileList = t
                        // 刷新 数据列表
                        showCurrentFileList(true)
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
     * 显示当前路径文件
     * @param refresh 是否刷新
     */
    private fun showCurrentFileList(refresh: Boolean) {
        L.i(TAG, "showCurrentFileList: $refresh")
        if (currentPathFileList.isNullOrEmpty()) {
            L.w(TAG, "showCurrentFileList: list is empty. ")
            return
        }
        val listSize = currentPathFileList.size
        if (currentFileIndex >= listSize) {
            L.w(TAG, "showCurrentFileList: file is load over. ")
            return
        }
        val start = currentFileIndex
        var end = currentFileIndex + loadSizePer
        end = min(end, listSize - 1)
        val list = currentPathFileList.subList(start, end)
        if (refresh) {
            chooseFileListRvAdapter?.refreshData(list)
            // 首次加载时，列表数据大于当前最后一个序号，显示加载更多
            L.i(TAG, "showCurrentFileList: --->> $end + $listSize")
//            if (listSize - 1 > end) {
//                showCurrentFileList(false)
//            }
        } else {
            val haveMore = listSize - 1 != end
            L.i(TAG, "showCurrentFileList: haveMore: $haveMore")
            chooseFileListRvAdapter?.loadMoreData(list, haveMore)
        }
        currentFileIndex = end
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
            val haveSelected = chooseFileSize != 0
            val fileSizeStr = if (haveSelected) {
                mContext.getString(R.string.preview_format).format(chooseFileSize)
            } else {
                mContext.getString(R.string._preview)
            }
            val submitFileSizeStr = if (haveSelected) {
                mContext.getString(R.string._submit_format).format(chooseFileSize)
            } else {
                mContext.getString(R.string._submit)
            }
            base_choose_file_bl_preview_tv?.apply {
                this.isClickable = haveSelected
                if (haveSelected) {
                    setTextColor(buttonClickScl)
                } else {
                    setTextColor(buttonDefaultScl)
                }
                text = fileSizeStr
            }
            base_choose_file_top_submit_tv?.apply {
                this.isClickable = haveSelected
                this.backgroundTintList = if (haveSelected) {
                    buttonClickScl
                } else {
                    buttonDefaultScl
                }
                text = submitFileSizeStr
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
        selectedImageList: ArrayList<BaseChooseFileBean>?,
        isPreview: Boolean
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
                .setIsPreview(isPreview)
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
            // 更新文件预览状态.
            updateFilePreviewCountOnView(chooseFileList.size)
//            updateChooseFileSize()
            return true
        }
    }

    /* ----------------------- 显示图片预览 -------------------------- */
    ///////////////////////////////////////////////////////////////////////////
    // OPEN
    ///////////////////////////////////////////////////////////////////////////


}